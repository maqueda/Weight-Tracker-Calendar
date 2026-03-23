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
      <div class="summary-card">
        <p class="summary-label">Resumen semanal</p>
        <template v-if="currentWeekSummary">
          <p class="summary-value">{{ summaryHeadline }}</p>
          <p class="summary-copy">{{ summaryCopy }}</p>
        </template>
        <template v-else>
          <p class="summary-value">Sin datos aún</p>
        </template>
      </div>
    </header>

    <p v-if="store.errorMessage" class="feedback error">{{ store.errorMessage }}</p>
    <p v-if="store.loading" class="feedback">Cargando calendario...</p>

    <section v-else class="stack">
      <WeightGoalPanel
        :goal="store.weightGoal"
        :saving="store.savingGoal"
        @save="handleGoalSave"
      />

      <MonthlyTrendChart :monthly-summaries="store.monthlySummaries" />

      <section class="layout">
        <div class="calendar-section">
          <div class="toolbar">
            <button type="button" @click="changeYear(-1)">Año anterior</button>
            <strong>{{ store.year }}</strong>
            <button type="button" @click="changeYear(1)">Año siguiente</button>
          </div>

          <div class="legend">
            <span><i class="legend-dot down"></i>Bajada</span>
            <span><i class="legend-dot up"></i>Subida</span>
            <span><i class="legend-dot flat"></i>Estable</span>
          </div>

          <div class="month-grid">
            <article
              v-for="month in months"
              :key="month.name"
              class="month-card"
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
                    <em v-if="day.sunday && weeklySummaryMap[day.date]" class="day-summary">
                      {{ weeklySummaryMap[day.date] }}
                    </em>
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
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import WeightEntryPanel from "../components/WeightEntryPanel.vue";
import WeightGoalPanel from "../components/WeightGoalPanel.vue";
import MonthlyTrendChart from "../components/MonthlyTrendChart.vue";
import { useCalendarStore } from "../store/useCalendarStore";
import type { DayTone } from "../types/calendar";

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
  name: string;
  summary: string;
  days: CalendarCell[];
};

const store = useCalendarStore();
const weekdays = ["L", "M", "X", "J", "V", "S", "D"];

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
        name: formatter.format(new Date(day.date)).replace(/^\w/, (value) => value.toUpperCase()),
        rawDays: []
      });
    }

    groups.get(day.month)?.rawDays.push({
      kind: "day",
      key: day.date,
      entryId: day.entryId,
      date: day.date,
      label: day.dayOfMonth,
      weight: day.weightKg !== null ? `${day.weightKg.toFixed(1)} kg` : "",
      hasEntry: day.hasEntry,
      sunday: day.sunday,
      tone: toneByDate.value[day.date] ?? "none"
    });
  });

  return Array.from(groups.entries()).map(([monthNumber, month]) => ({
    name: month.name,
    summary: monthlySummaryMap.value[monthNumber] ?? "Sin registros",
    days: buildCalendarCells(month.rawDays)
  }));
});

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

const summaryHeadline = computed(() => {
  if (!currentWeekSummary.value) return "Sin datos";
  if (!currentWeekSummary.value.complete || currentWeekSummary.value.deltaKg === null) {
    return `Semana ${currentWeekSummary.value.weekNumber} incompleta`;
  }
  const delta = currentWeekSummary.value.deltaKg;
  if (delta > 0) return `Has subido ${delta.toFixed(2)} kg`;
  if (delta < 0) return `Has bajado ${Math.abs(delta).toFixed(2)} kg`;
  return "Peso estable";
});

const summaryCopy = computed(() => {
  if (!currentWeekSummary.value) return "";
  return `Del ${formatShortDate(currentWeekSummary.value.sundayStartDate)} al ${formatShortDate(currentWeekSummary.value.sundayEndDate)}.`;
});

function formatShortDate(value: string) {
  return new Date(value).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "short"
  });
}

function changeYear(direction: number) {
  store.loadYear(store.year + direction);
}

async function handleSave(payload: { entryId: number | null; entryDate: string; weightKg: number; notes?: string }) {
  await store.saveEntry(payload);
}

async function handleGoalSave(payload: { startWeightKg: number; targetWeightKg: number; targetDate?: string }) {
  await store.saveGoal(payload);
}

function buildCalendarCells(days: DayCell[]): CalendarCell[] {
  if (days.length === 0) return [];
  const firstDate = new Date(days[0].date);
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
.eyebrow { text-transform: uppercase; letter-spacing: 0.12em; font-size: 12px; color: #5f7895; }
h1 { margin: 8px 0 12px; font-size: clamp(2rem, 4vw, 3.8rem); }
.subtitle { margin: 0; max-width: 700px; color: #4d627b; }
.summary-card { min-width: 240px; padding: 20px; border-radius: 20px; background: #132238; color: #f5f9ff; }
.summary-label { margin: 0 0 8px; color: #b8c7d9; }
.summary-value { margin: 0; font-size: 1.25rem; font-weight: 600; }
.summary-copy { color: #c8d5e3; line-height: 1.4; }
.feedback { margin: 0 0 16px; padding: 14px 16px; border-radius: 14px; background: rgba(255,255,255,0.9); color: #26425f; }
.error { background: #ffe6e2; color: #8a2f25; }
.layout { display: grid; grid-template-columns: minmax(0, 1fr) 320px; gap: 24px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; padding: 16px 18px; border-radius: 18px; background: rgba(255,255,255,0.82); border: 1px solid #dce6f2; }
.toolbar button { border: 0; border-radius: 12px; padding: 10px 14px; background: #132238; color: #f5f9ff; cursor: pointer; }
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
.day-content { display: flex; flex: 1; min-height: 0; flex-direction: column; justify-content: flex-end; }
.day-weight { display: block; color: #35506d; font-size: 0.86rem; font-weight: 600; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.day-summary { position: absolute; right: 6px; bottom: 6px; display: inline-flex; align-items: center; justify-content: center; min-width: 30px; max-width: calc(100% - 12px); padding: 2px 6px; border-radius: 999px; background: rgba(19,34,56,0.12); font-style: normal; font-size: 0.66rem; font-weight: 700; color: #214a31; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.day.selected { outline: 2px solid #ff8b61; }
.day.filled { background: #d9ecff; }
.day.sunday { background: #eef8e8; }
.day.up { box-shadow: inset 0 0 0 2px rgba(255, 139, 97, 0.22); }
.day.down { box-shadow: inset 0 0 0 2px rgba(82, 183, 136, 0.24); }
.day.flat { box-shadow: inset 0 0 0 2px rgba(123, 141, 164, 0.2); }
.day-placeholder { background: transparent; border: 1px dashed rgba(170,187,205,0.35); cursor: default; gap: 0; }
@media (max-width: 1400px) { .month-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 900px) { .layout { grid-template-columns: 1fr; } .month-grid { grid-template-columns: 1fr; } }
@media (max-width: 768px) {
  .page { padding: 20px; }
  .hero { flex-direction: column; }
  .month-card { padding: 16px; }
  .weekday-row, .days { gap: 5px; }
  .days { grid-auto-rows: 64px; }
  .day { padding: 7px 5px; border-radius: 12px; }
  .day-number { font-size: 0.86rem; }
  .day-weight { font-size: 0.68rem; }
  .day-summary { right: 4px; bottom: 4px; min-width: 24px; padding: 1px 4px; font-size: 0.58rem; }
}
</style>
