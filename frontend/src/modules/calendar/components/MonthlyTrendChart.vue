<template>
  <section class="chart-card">
    <div class="chart-head">
      <div>
        <p class="chart-label">Tendencia mensual</p>
        <h2>Promedio por mes</h2>
      </div>
      <p class="chart-copy">{{ chartCopy }}</p>
    </div>
    <div class="chart">
      <div v-for="month in chartMonths" :key="month.month" class="bar-group">
        <div class="bar-track">
          <div class="bar-fill" :class="month.tone" :style="{ height: `${month.height}%` }"></div>
        </div>
        <strong>{{ month.label }}</strong>
        <small>{{ month.value }}</small>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { MonthlySummary } from "../../monthly-summary/types/monthlySummary";
import type { WeightGoal } from "../../weight-goal/types/weightGoal";

type ChartTone = "down" | "up" | "flat" | "none";

const props = defineProps<{
  monthlySummaries: MonthlySummary[];
  weightGoal: WeightGoal | null;
}>();

const baselineWeight = computed(() => props.weightGoal?.startWeightKg ?? null);

const chartCopy = computed(() =>
  baselineWeight.value !== null
    ? `Cada barra representa el peso medio y se compara con el peso inicial de ${baselineWeight.value.toFixed(1)} kg.`
    : "Cada barra representa el peso medio de ese mes."
);

const chartMonths = computed(() => {
  const values = props.monthlySummaries
    .map((month) => month.averageWeightKg)
    .filter((value): value is number => value !== null);
  const min = values.length > 0 ? Math.min(...values) : 0;
  const max = values.length > 0 ? Math.max(...values) : 0;
  const range = Math.max(max - min, 1);

  return props.monthlySummaries.map((month) => ({
    month: month.month,
    label: month.monthLabel,
    value: month.averageWeightKg !== null ? `${month.averageWeightKg.toFixed(1)} kg` : "Sin datos",
    height: month.averageWeightKg !== null ? Math.max(((month.averageWeightKg - min) / range) * 100, 12) : 0,
    tone: resolveTone(month.averageWeightKg, baselineWeight.value)
  }));
});

function resolveTone(value: number | null, baseline: number | null): ChartTone {
  if (value === null || baseline === null) {
    return "none";
  }

  const delta = value - baseline;
  if (delta > 0.1) {
    return "up";
  }
  if (delta < -0.1) {
    return "down";
  }
  return "flat";
}
</script>

<style scoped>
.chart-card { padding: 22px; border-radius: 22px; background: rgba(255,255,255,0.88); border: 1px solid #d9e5f2; }
.chart-head { display: flex; justify-content: space-between; gap: 16px; align-items: end; margin-bottom: 18px; }
.chart-label { margin: 0 0 6px; color: #5f7895; text-transform: uppercase; letter-spacing: 0.08em; font-size: 0.72rem; }
.chart-head h2, .chart-copy { margin: 0; }
.chart-copy { color: #5f7895; }
.chart { display: grid; grid-template-columns: repeat(12, minmax(0, 1fr)); gap: 10px; align-items: end; }
.bar-group { text-align: center; }
.bar-track { height: 180px; display: flex; align-items: end; justify-content: center; padding: 8px 0; border-radius: 14px; background: #eef4fb; }
.bar-fill { width: 100%; max-width: 24px; border-radius: 999px; background: linear-gradient(180deg, #7b8da4 0%, #132238 100%); }
.bar-fill.down { background: linear-gradient(180deg, #52b788 0%, #132238 100%); }
.bar-fill.up { background: linear-gradient(180deg, #ff8b61 0%, #132238 100%); }
.bar-fill.flat { background: linear-gradient(180deg, #7b8da4 0%, #132238 100%); }
.bar-fill.none { background: linear-gradient(180deg, #aebfd1 0%, #5f7895 100%); }
.bar-group strong, .bar-group small { display: block; }
.bar-group strong { margin-top: 8px; text-transform: capitalize; }
.bar-group small { color: #5f7895; }
@media (max-width: 900px) { .chart { grid-template-columns: repeat(6, minmax(0, 1fr)); } }
</style>
