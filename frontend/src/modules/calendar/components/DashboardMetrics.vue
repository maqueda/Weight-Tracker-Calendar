<template>
  <section class="metrics-card">
    <div class="metrics-head">
      <div>
        <p class="metrics-label">Resumen rápido</p>
        <h2>Panel del año</h2>
      </div>
      <button type="button" class="export-button" @click="$emit('export')">Exportar CSV</button>
    </div>

    <div class="metrics-grid">
      <article v-for="metric in metrics" :key="metric.label" class="metric" :class="metric.tone">
        <p class="metric-label">{{ metric.label }}</p>
        <strong class="metric-value">{{ metric.value }}</strong>
        <small class="metric-hint">{{ metric.hint }}</small>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { DashboardMetric } from "../utils/dashboardMetrics";

defineProps<{
  metrics: DashboardMetric[];
}>();

defineEmits<{
  export: [];
}>();
</script>

<style scoped>
.metrics-card { padding: 22px; border-radius: 22px; background: rgba(255,255,255,0.88); border: 1px solid #d9e5f2; }
.metrics-head { display: flex; justify-content: space-between; gap: 16px; align-items: center; margin-bottom: 18px; }
.metrics-label { margin: 0 0 6px; color: #5f7895; text-transform: uppercase; letter-spacing: 0.08em; font-size: 0.72rem; }
.metrics-head h2 { margin: 0; }
.export-button { border: 0; border-radius: 12px; padding: 10px 14px; background: #132238; color: #f5f9ff; cursor: pointer; }
.metrics-grid { display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); gap: 12px; }
.metric { padding: 16px; border-radius: 18px; background: #eef4fb; display: grid; gap: 6px; }
.metric.up { background: #fff1ea; }
.metric.down { background: #edf8f3; }
.metric.good { background: #eef8e8; }
.metric-label { margin: 0; color: #5f7895; font-size: 0.78rem; }
.metric-value { font-size: 1.15rem; color: #18304c; }
.metric-hint { color: #5f7895; line-height: 1.35; }
@media (max-width: 1200px) { .metrics-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); } }
@media (max-width: 768px) { .metrics-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .metrics-head { flex-direction: column; align-items: stretch; } }
</style>
