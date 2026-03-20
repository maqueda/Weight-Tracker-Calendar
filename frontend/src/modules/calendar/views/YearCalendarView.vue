<template>
  <main class="page">
    <header class="hero">
      <div>
        <p class="eyebrow">Weight Tracker Calendar</p>
        <h1>Seguimiento anual de peso</h1>
        <p class="subtitle">
          Registra tu peso por dia y revisa la variacion semanal de domingo a domingo.
        </p>
      </div>
      <div class="summary-card">
        <p class="summary-label">Resumen semanal</p>
        <template v-if="currentWeekSummary">
          <p class="summary-value">{{ summaryHeadline }}</p>
          <p class="summary-copy">{{ summaryCopy }}</p>
        </template>
        <template v-else>
          <p class="summary-value">Sin datos aun</p>
        </template>
      </div>
    </header>

    <p v-if="store.errorMessage" class="feedback error">{{ store.errorMessage }}</p>
    <p v-if="store.loading" class="feedback">Cargando calendario...</p>

    <section v-else class="layout">
      <div class="calendar-section">
        <div class="toolbar">
          <button type="button" @click="changeYear(-1)">Ano anterior</button>
          <strong>{{ store.year }}</strong>
          <button type="button" @click="changeYear(1)">Ano siguiente</button>
        </div>

        <div class="month-grid">
          <article
            v-for="month in months"
            :key="month.name"
            class="month-card"
          >
            <h2>{{ month.name }}</h2>
            <div class="days">
              <button
                v-for="day in month.days"
                :key="day.date"
                :class="['day', { selected: day.date === store.selectedDate, sunday: day.sunday, filled: day.hasEntry }]"
                type="button"
                @click="store.selectDate(day.date)"
              >
                <span>{{ day.label }}</span>
                <small>{{ day.weight }}</small>
                <em v-if="day.sunday && weeklySummaryMap[day.date]">{{ weeklySummaryMap[day.date] }}</em>
              </button>
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
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import WeightEntryPanel from "../components/WeightEntryPanel.vue";
import { useCalendarStore } from "../store/useCalendarStore";

type MonthBlock = {
  name: string;
  days: Array<{
    entryId: number | null;
    date: string;
    label: number;
    weight: string;
    notes: string | null;
    hasEntry: boolean;
    sunday: boolean;
  }>;
};

const store = useCalendarStore();

onMounted(() => {
  store.loadYear(store.year);
});

const months = computed<MonthBlock[]>(() => {
  const formatter = new Intl.DateTimeFormat("es-ES", { month: "long" });
  const groups = new Map<number, MonthBlock>();

  store.days.forEach((day) => {
    if (!groups.has(day.month)) {
      groups.set(day.month, {
        name: formatter.format(new Date(day.date)).replace(/^\w/, (value) => value.toUpperCase()),
        days: []
      });
    }

    groups.get(day.month)?.days.push({
      entryId: day.entryId,
      date: day.date,
      label: day.dayOfMonth,
      weight: day.weightKg !== null ? `${day.weightKg.toFixed(2)} kg` : "-",
      notes: day.notes,
      hasEntry: day.hasEntry,
      sunday: day.sunday
    });
  });

  return Array.from(groups.values());
});

const weeklySummaryMap = computed<Record<string, string>>(() =>
  Object.fromEntries(
    store.weeklySummaries.map((summary) => {
      if (!summary.complete || summary.deltaKg === null) {
        return [summary.sundayEndDate, "Incompleta"];
      }

      const prefix = summary.deltaKg > 0 ? "+" : "";
      return [summary.sundayEndDate, `${prefix}${summary.deltaKg.toFixed(2)} kg`];
    })
  )
);

const currentWeekSummary = computed(() => store.currentWeekSummary);

const summaryHeadline = computed(() => {
  if (!currentWeekSummary.value) {
    return "Sin datos";
  }

  if (!currentWeekSummary.value.complete || currentWeekSummary.value.deltaKg === null) {
    return `Semana ${currentWeekSummary.value.weekNumber} incompleta`;
  }

  const delta = currentWeekSummary.value.deltaKg;
  if (delta > 0) {
    return `Has subido ${delta.toFixed(2)} kg`;
  }
  if (delta < 0) {
    return `Has bajado ${Math.abs(delta).toFixed(2)} kg`;
  }
  return "Peso estable";
});

const summaryCopy = computed(() => {
  if (!currentWeekSummary.value) {
    return "";
  }

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
</script>

<style scoped>
.page {
  padding: 32px;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: stretch;
  margin-bottom: 32px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #5f7895;
}

h1 {
  margin: 8px 0 12px;
  font-size: clamp(2rem, 4vw, 3.8rem);
}

.subtitle {
  margin: 0;
  max-width: 700px;
  color: #4d627b;
}

.summary-card {
  min-width: 240px;
  padding: 20px;
  border-radius: 20px;
  background: #132238;
  color: #f5f9ff;
}

.summary-label {
  margin: 0 0 8px;
  color: #b8c7d9;
}

.summary-value {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.summary-copy {
  color: #c8d5e3;
  line-height: 1.4;
}

.feedback {
  margin: 0 0 16px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  color: #26425f;
}

.error {
  background: #ffe6e2;
  color: #8a2f25;
}

.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid #dce6f2;
}

.toolbar button {
  border: 0;
  border-radius: 12px;
  padding: 10px 14px;
  background: #132238;
  color: #f5f9ff;
  cursor: pointer;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.month-card {
  padding: 20px;
  border: 1px solid #d9e5f2;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(10px);
}

.month-card h2 {
  margin-top: 0;
}

.days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.day {
  min-height: 72px;
  border: 0;
  border-radius: 14px;
  background: #eef4fb;
  color: #18304c;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px 8px;
  cursor: pointer;
  text-align: left;
}

.day small {
  color: #6b8099;
}

.day em {
  font-style: normal;
  font-size: 0.68rem;
  color: #214a31;
}

.day.selected {
  outline: 2px solid #ff8b61;
}

.day.filled {
  background: #d9ecff;
}

.day.sunday {
  background: #eef8e8;
}

@media (max-width: 768px) {
  .page {
    padding: 20px;
  }

  .hero {
    flex-direction: column;
  }

  .layout {
    grid-template-columns: 1fr;
  }
}
</style>
