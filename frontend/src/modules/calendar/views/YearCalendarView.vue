<template>
  <main class="page">
    <header class="hero">
      <div>
        <p class="eyebrow">Weight Tracker Calendar</p>
        <h1>Seguimiento anual de peso</h1>
        <p class="subtitle">
          Registra tu peso por día, revisa la variación semanal y compara la tendencia de cada mes.
        </p>
      </div>
      <div class="hero-side">
        <div class="account-card">
          <div>
            <p class="summary-label">Sesión</p>
            <p class="account-name">{{ authStore.user?.displayName ?? authStore.user?.username }}</p>
          </div>
          <div class="account-actions">
            <button type="button" class="profile-button" @click="openProfileWindow">Ver mis datos</button>
            <button type="button" class="logout-button" @click="authStore.logout()">Salir</button>
          </div>
        </div>

        <div class="summary-card">
          <p class="summary-label">Resumen semanal</p>
          <template v-if="currentWeekSummary">
            <p class="summary-value">{{ summaryHeadline }}</p>
            <p class="summary-copy">{{ summaryCopy }}</p>
          </template>
          <template v-else>
            <p class="summary-value">Semana sin registros</p>
            <p class="summary-copy">Cuando guardes tu primer domingo, aquí verás la comparación semanal.</p>
          </template>
        </div>

        <div class="streak-card">
          <p class="summary-label">Rachas</p>
          <p class="summary-value">{{ streakHeadline }}</p>
          <p class="summary-copy">{{ streakCopy }}</p>
        </div>
      </div>
    </header>

    <p v-if="store.errorMessage" class="feedback error">{{ store.errorMessage }}</p>
    <p v-if="showInitialLoading" class="feedback">Cargando calendario...</p>
    <p v-else-if="store.loading" class="feedback info">Actualizando calendario...</p>

    <section v-if="!showInitialLoading" class="stack">
      <DashboardMetrics
        :metrics="dashboardMetrics"
        @export="exportCsv"
      />

      <WeightGoalPanel
        :goal="store.weightGoal"
        :saving="store.savingGoal"
        @save="handleGoalSave"
      />

      <MonthlyTrendChart
        :monthly-summaries="store.monthlySummaries"
        :weight-goal="store.weightGoal"
      />

      <section class="layout">
        <div class="calendar-section">
          <div class="toolbar">
            <div class="toolbar-nav">
              <button type="button" @click="changeYear(-1)">Año anterior</button>
              <strong>{{ store.year }}</strong>
              <button type="button" @click="changeYear(1)">Año siguiente</button>
            </div>
            <div class="toolbar-actions">
              <label class="month-filter">
                <span>Mes</span>
                <select v-model="selectedMonthFilter">
                  <option value="all">Todos</option>
                  <option
                    v-for="month in monthOptions"
                    :key="month.value"
                    :value="month.value"
                  >
                    {{ month.label }}
                  </option>
                </select>
              </label>
              <button type="button" class="secondary-button" @click="jumpToToday">Hoy</button>
            </div>
          </div>

          <div class="legend">
            <span><i class="legend-dot down"></i>Bajada</span>
            <span><i class="legend-dot up"></i>Subida</span>
            <span><i class="legend-dot flat"></i>Estable</span>
          </div>

          <div class="month-grid">
            <article
              v-for="month in visibleMonths"
              :key="month.name"
              class="month-card"
              :ref="(element) => setMonthCardRef(month.monthNumber, element)"
            >
              <div class="month-head">
                <h2>{{ month.name }}</h2>
                <small>{{ month.summary }}</small>
              </div>

              <div class="weekday-row">
                <span v-for="weekday in weekdays" :key="weekday" class="weekday">{{ weekday }}</span>
              </div>

              <div class="days">
                <template v-for="day in month.days" :key="day.key">
                  <button
                    v-if="day.kind === 'day'"
                    :class="[
                      'day',
                      day.tone,
                      { selected: day.date === store.selectedDate, sunday: day.sunday, filled: day.hasEntry }
                    ]"
                    type="button"
                    @click="store.selectDate(day.date)"
                  >
                    <span class="day-number">{{ day.label }}</span>
                    <div class="day-content">
                      <small v-if="day.weight" class="day-weight">{{ day.weight }}</small>
                    </div>
                    <div v-if="day.sunday && weeklySummaryMap[day.date]" class="day-summary-wrap">
                      <em class="day-summary">
                        {{ weeklySummaryMap[day.date] }}
                      </em>
                    </div>
                  </button>
                  <div v-else class="day day-placeholder" aria-hidden="true"></div>
                </template>
              </div>
            </article>
          </div>
        </div>

        <WeightEntryPanel
          :selected-day="store.selectedDay"
          :saving="store.saving"
          @save="handleSave"
        />
      </section>
    </section>

    <transition name="profile-modal">
      <div v-if="showProfileModal" class="profile-modal" @click.self="closeProfileModal">
        <div class="profile-dialog">
          <ProfilePanel :show-close="true" @close="closeProfileModal" />
        </div>
      </div>
    </transition>
  </main>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, type ComponentPublicInstance } from "vue";
import WeightEntryPanel from "../components/WeightEntryPanel.vue";
import WeightGoalPanel from "../components/WeightGoalPanel.vue";
import MonthlyTrendChart from "../components/MonthlyTrendChart.vue";
import DashboardMetrics from "../components/DashboardMetrics.vue";
import { useCalendarStore } from "../store/useCalendarStore";
import type { DayTone } from "../types/calendar";
import { buildWeekSummaryPresentation, calculateStreakSummary, filterMonthsBySelection } from "../utils/calendarInsights";
import { buildDashboardMetrics } from "../utils/dashboardMetrics";
import { buildWeightEntriesCsv, downloadCsvFile } from "../utils/csvExport";
import { useAuthStore } from "../../auth/store/useAuthStore";
import ProfilePanel from "../../auth/components/ProfilePanel.vue";

type DayCell = {
  kind: "day";
  key: string;
  entryId: number | null;
  date: string;
  label: number;
  weight: string;
  hasEntry: boolean;
  sunday: boolean;
  tone: DayTone;
};

type PlaceholderCell = {
  kind: "placeholder";
  key: string;
};

type CalendarCell = DayCell | PlaceholderCell;

type MonthBlock = {
  monthNumber: number;
  name: string;
  summary: string;
  days: CalendarCell[];
};

// La vista anual centraliza lectura y escritura del calendario para que
// filtros, resúmenes y edición compartan el mismo estado reactivo.
const store = useCalendarStore();
const authStore = useAuthStore();
const weekdays = ["L", "M", "X", "J", "V", "S", "D"];
const selectedMonthFilter = ref<number | "all">("all");
const monthCardRefs = ref<Record<number, HTMLElement | null>>({});
const showProfileModal = ref(false);

onMounted(() => {
  store.loadYear(store.year);
});

const toneByDate = computed<Record<string, DayTone>>(() => {
  const entries = store.days.filter((day) => day.weightKg !== null);
  const tones: Record<string, DayTone> = {};
  entries.forEach((day, index) => {
    if (index === 0 || day.weightKg === null || entries[index - 1].weightKg === null) {
      tones[day.date] = "none";
      return;
    }
    const delta = day.weightKg - (entries[index - 1].weightKg ?? 0);
    tones[day.date] = delta > 0 ? "up" : delta < 0 ? "down" : "flat";
  });
  return tones;
});

const monthlySummaryMap = computed<Record<number, string>>(() =>
  Object.fromEntries(
    store.monthlySummaries.map((summary) => [
      summary.month,
      summary.averageWeightKg !== null
        ? `Media ${summary.averageWeightKg.toFixed(1)} kg`
        : "Sin registros"
    ])
  )
);

const months = computed<MonthBlock[]>(() => {
  const formatter = new Intl.DateTimeFormat("es-ES", { month: "long" });
  const groups = new Map<number, { name: string; rawDays: DayCell[] }>();

  store.days.forEach((day) => {
    if (!groups.has(day.month)) {
      groups.set(day.month, {
        name: formatter.format(new Date(`${day.date}T00:00:00`)).replace(/^\w/, (value) => value.toUpperCase()),
        rawDays: []
      });
    }

    groups.get(day.month)?.rawDays.push({
      kind: "day",
      key: day.date,
      entryId: day.entryId,
      date: day.date,
      label: day.dayOfMonth,
      weight: day.weightKg !== null ? day.weightKg.toFixed(1) : "",
      hasEntry: day.hasEntry,
      sunday: day.sunday,
      tone: toneByDate.value[day.date] ?? "none"
    });
  });

  return Array.from(groups.entries()).map(([monthNumber, month]) => ({
    monthNumber,
    name: month.name,
    summary: monthlySummaryMap.value[monthNumber] ?? "Sin registros",
    days: buildCalendarCells(month.rawDays)
  }));
});

const visibleMonths = computed(() => filterMonthsBySelection(months.value, selectedMonthFilter.value));

const monthOptions = computed(() =>
  months.value.map((month) => ({
    value: month.monthNumber,
    label: month.name
  }))
);

const weeklySummaryMap = computed<Record<string, string>>(() =>
  Object.fromEntries(
    store.weeklySummaries.map((summary) => {
      if (!summary.complete || summary.deltaKg === null) {
        return [summary.sundayEndDate, ""];
      }
      const prefix = summary.deltaKg > 0 ? "+" : "";
      return [summary.sundayEndDate, `${prefix}${summary.deltaKg.toFixed(1)}`];
    })
  )
);

const currentWeekSummary = computed(() => store.currentWeekSummary);
const showInitialLoading = computed(() => store.loading && store.days.length === 0);
const weekSummaryPresentation = computed(() => buildWeekSummaryPresentation(currentWeekSummary.value));
const summaryHeadline = computed(() => weekSummaryPresentation.value.headline);
const summaryCopy = computed(() => weekSummaryPresentation.value.copy);
const streakSummary = computed(() => calculateStreakSummary(store.days));
const dashboardMetrics = computed(() =>
  buildDashboardMetrics(store.days, store.weightGoal, streakSummary.value.bestStreak, store.year)
);

const streakHeadline = computed(() =>
  streakSummary.value.currentStreak > 0
    ? `${streakSummary.value.currentStreak} días seguidos`
    : "Sin racha activa"
);

const streakCopy = computed(() => {
  if (streakSummary.value.bestStreak === 0 || !streakSummary.value.lastEntryDate) {
    return "Empieza a registrar tu peso para ver tu mejor racha.";
  }

  const lastEntry = new Date(`${streakSummary.value.lastEntryDate}T00:00:00`).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "short"
  });

  return `Mejor racha: ${streakSummary.value.bestStreak} días. Último registro: ${lastEntry}.`;
});

function changeYear(direction: number) {
  store.loadYear(store.year + direction);
  selectedMonthFilter.value = "all";
}

async function handleSave(payload: { entryId: number | null; entryDate: string; weightKg: number; notes?: string }) {
  await store.saveEntry(payload);
}

async function handleGoalSave(payload: { startWeightKg: number; targetWeightKg: number; targetDate?: string }) {
  await store.saveGoal(payload);
}

async function jumpToToday() {
  const today = new Date();
  const todayYear = today.getFullYear();
  const todayMonth = today.getMonth() + 1;
  const todayDate = today.toISOString().slice(0, 10);

  if (store.year !== todayYear) {
    await store.loadYear(todayYear);
  }

  selectedMonthFilter.value = todayMonth;
  store.selectDate(todayDate);

  await nextTick();
  monthCardRefs.value[todayMonth]?.scrollIntoView({
    behavior: "smooth",
    block: "start"
  });
}

function exportCsv() {
  const csv = buildWeightEntriesCsv(store.days);
  downloadCsvFile(csv, `weight-tracker-calendar-${store.year}.csv`);
}

function openProfileWindow() {
  showProfileModal.value = true;
}

function closeProfileModal() {
  showProfileModal.value = false;
}

function setMonthCardRef(monthNumber: number, element: Element | ComponentPublicInstance | null) {
  monthCardRefs.value[monthNumber] = element instanceof HTMLElement ? element : null;
}

function buildCalendarCells(days: DayCell[]): CalendarCell[] {
  if (days.length === 0) return [];
  const firstDate = new Date(`${days[0].date}T00:00:00`);
  const mondayBasedOffset = (firstDate.getDay() + 6) % 7;
  const cells: CalendarCell[] = [];

  for (let index = 0; index < mondayBasedOffset; index += 1) {
    cells.push({ kind: "placeholder", key: `${days[0].date}-leading-${index}` });
  }

  cells.push(...days);

  while (cells.length % 7 !== 0) {
    cells.push({ kind: "placeholder", key: `${days[0].date}-trailing-${cells.length}` });
  }
  while (cells.length < 42) {
    cells.push({ kind: "placeholder", key: `${days[0].date}-tail-${cells.length}` });
  }
  return cells;
}
</script>

<style scoped>
.page { padding: 32px; }
.stack { display: grid; gap: 24px; }
.hero { display: flex; justify-content: space-between; gap: 24px; align-items: stretch; margin-bottom: 32px; }
.hero-side { display: grid; gap: 16px; min-width: 260px; }
.eyebrow { text-transform: uppercase; letter-spacing: 0.12em; font-size: 12px; color: #5f7895; }
h1 { margin: 8px 0 12px; font-size: clamp(2rem, 4vw, 3.8rem); }
.subtitle { margin: 0; max-width: 700px; color: #4d627b; }
.account-card { display: flex; justify-content: space-between; gap: 16px; align-items: center; padding: 18px 20px; border-radius: 20px; background: rgba(255,255,255,0.86); border: 1px solid #d9e5f2; }
.account-actions { display: flex; gap: 10px; align-items: center; }
.account-name { margin: 0; font-size: 1rem; font-weight: 700; color: #18304c; }
.profile-button { border: 1px solid #d3dfec; border-radius: 12px; padding: 10px 14px; background: #fff; color: #18304c; cursor: pointer; }
.logout-button { border: 0; border-radius: 12px; padding: 10px 14px; background: #132238; color: #f5f9ff; cursor: pointer; }
.profile-modal { position: fixed; inset: 0; padding: 28px; background: rgba(10, 19, 31, 0.46); backdrop-filter: blur(10px); display: grid; place-items: center; z-index: 50; }
.profile-dialog { width: min(100%, 920px); max-height: calc(100vh - 56px); overflow: auto; border-radius: 28px; }
.profile-modal-enter-active, .profile-modal-leave-active { transition: opacity 0.22s ease; }
.profile-modal-enter-active .profile-dialog, .profile-modal-leave-active .profile-dialog { transition: transform 0.22s ease, opacity 0.22s ease; }
.profile-modal-enter-from, .profile-modal-leave-to { opacity: 0; }
.profile-modal-enter-from .profile-dialog, .profile-modal-leave-to .profile-dialog { transform: translateY(12px) scale(0.98); opacity: 0; }
.summary-card, .streak-card { min-width: 240px; padding: 20px; border-radius: 20px; background: #132238; color: #f5f9ff; }
.streak-card { background: #1b2b19; }
.summary-label { margin: 0 0 8px; color: #b8c7d9; }
.summary-value { margin: 0; font-size: 1.25rem; font-weight: 600; }
.summary-copy { color: #c8d5e3; line-height: 1.4; }
.feedback { margin: 0 0 16px; padding: 14px 16px; border-radius: 14px; background: rgba(255,255,255,0.9); color: #26425f; }
.info { background: rgba(217, 236, 255, 0.9); color: #21476a; }
.error { background: #ffe6e2; color: #8a2f25; }
.layout { display: grid; grid-template-columns: minmax(0, 1fr) 320px; gap: 24px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 14px; padding: 16px 18px; border-radius: 18px; background: rgba(255,255,255,0.82); border: 1px solid #dce6f2; }
.toolbar-nav, .toolbar-actions { display: flex; align-items: center; gap: 12px; }
.toolbar button { border: 0; border-radius: 12px; padding: 10px 14px; background: #132238; color: #f5f9ff; cursor: pointer; }
.secondary-button { background: #35506d !important; }
.month-filter { display: inline-flex; align-items: center; gap: 8px; color: #35506d; font-weight: 600; }
.month-filter select { border: 1px solid #d3dfec; border-radius: 10px; padding: 8px 10px; background: #fff; color: #18304c; }
.legend { display: flex; gap: 16px; align-items: center; margin-bottom: 18px; color: #4d627b; flex-wrap: wrap; }
.legend span { display: inline-flex; gap: 8px; align-items: center; }
.legend-dot { width: 10px; height: 10px; border-radius: 999px; display: inline-block; }
.legend-dot.down { background: #52b788; }
.legend-dot.up { background: #ff8b61; }
.legend-dot.flat { background: #7b8da4; }
.month-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; }
.month-card { padding: 20px; border: 1px solid #d9e5f2; border-radius: 20px; background: rgba(255,255,255,0.86); backdrop-filter: blur(10px); }
.month-head { display: flex; justify-content: space-between; gap: 12px; align-items: baseline; margin-bottom: 10px; }
.month-head h2 { margin: 0; }
.month-head small { color: #5f7895; }
.weekday-row { display: grid; grid-template-columns: repeat(7, minmax(0, 1fr)); gap: 8px; margin-bottom: 10px; }
.weekday { text-align: center; font-size: 0.74rem; font-weight: 700; color: #6b8099; }
.days { display: grid; grid-template-columns: repeat(7, minmax(0, 1fr)); grid-auto-rows: 98px; gap: 8px; align-items: stretch; }
.day { position: relative; min-height: 0; width: 100%; border: 0; border-radius: 14px; background: #eef4fb; color: #18304c; display: flex; flex-direction: column; justify-content: flex-start; padding: 10px 9px; cursor: pointer; text-align: left; overflow: hidden; gap: 6px; }
.day-number { font-size: 1rem; font-weight: 700; line-height: 1; }
.day-content { display: flex; flex: 1; min-height: 0; flex-direction: column; justify-content: flex-end; padding-right: 0; }
.day-weight { display: block; color: #35506d; font-size: 0.84rem; font-weight: 700; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
.day-summary-wrap { position: absolute; right: 6px; bottom: 6px; pointer-events: none; }
.day-summary { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; max-width: 38px; padding: 2px 5px; border-radius: 999px; background: rgba(19,34,56,0.14); font-style: normal; font-size: 0.62rem; font-weight: 700; color: #214a31; line-height: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-shrink: 0; }
.day.sunday .day-weight { padding-right: 34px; }
.day.selected { outline: 2px solid #ff8b61; }
.day.filled { background: #d9ecff; }
.day.sunday { background: #eef8e8; }
.day.up { box-shadow: inset 0 0 0 2px rgba(255, 139, 97, 0.22); }
.day.down { box-shadow: inset 0 0 0 2px rgba(82, 183, 136, 0.24); }
.day.flat { box-shadow: inset 0 0 0 2px rgba(123, 141, 164, 0.2); }
.day-placeholder { background: transparent; border: 1px dashed rgba(170,187,205,0.35); cursor: default; gap: 0; }
@media (max-width: 1400px) { .month-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 900px) {
  .layout { grid-template-columns: 1fr; }
  .month-grid { grid-template-columns: 1fr; }
  .toolbar { flex-direction: column; align-items: stretch; }
  .toolbar-nav, .toolbar-actions { justify-content: space-between; flex-wrap: wrap; }
}
@media (max-width: 768px) {
  .page { padding: 20px; }
  .hero { flex-direction: column; }
  .month-card { padding: 16px; }
  .weekday-row, .days { gap: 5px; }
  .days { grid-auto-rows: 64px; }
  .day { padding: 7px 5px; border-radius: 12px; }
  .day-number { font-size: 0.86rem; }
  .day-weight { font-size: 0.72rem; }
  .day-summary-wrap { right: 4px; bottom: 4px; }
  .day-summary { min-width: 24px; max-width: 34px; padding: 1px 4px; font-size: 0.56rem; }
  .day.sunday .day-weight { padding-right: 30px; }
}
</style>
