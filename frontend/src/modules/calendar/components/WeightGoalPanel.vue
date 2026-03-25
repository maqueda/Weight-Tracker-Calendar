<template>
  <section class="goal-card">
    <div class="goal-header">
      <div>
        <p class="goal-label">Objetivo</p>
        <h2>{{ headline }}</h2>
      </div>
      <strong v-if="goal?.progressPercent !== null" class="goal-progress">{{ goal?.progressPercent }}%</strong>
    </div>

    <p class="goal-copy">{{ copy }}</p>

    <div class="goal-chips">
      <span v-for="chip in helperChips" :key="chip" class="goal-chip">{{ chip }}</span>
    </div>

    <form class="goal-form" @submit.prevent="handleSubmit">
      <label>
        <span>Peso inicial</span>
        <input v-model="startWeightKg" type="number" min="0.01" step="0.1" required />
      </label>
      <label>
        <span>Peso objetivo</span>
        <input v-model="targetWeightKg" type="number" min="0.01" step="0.1" required />
      </label>
      <label>
        <span>Fecha objetivo</span>
        <input v-model="targetDate" type="date" />
      </label>
      <button :disabled="saving" type="submit">Guardar objetivo</button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import type { WeightGoal } from "../../weight-goal/types/weightGoal";

// El objetivo se edita dentro de la misma vista anual para mantener el flujo
// de análisis y edición en un único lugar.
const props = defineProps<{
  goal: WeightGoal | null;
  saving: boolean;
}>();

const emit = defineEmits<{
  save: [payload: { startWeightKg: number; targetWeightKg: number; targetDate?: string }];
}>();

const startWeightKg = ref("");
const targetWeightKg = ref("");
const targetDate = ref("");

watch(
  () => props.goal,
  (goal) => {
    startWeightKg.value = goal?.startWeightKg?.toFixed(1) ?? "";
    targetWeightKg.value = goal?.targetWeightKg?.toFixed(1) ?? "";
    targetDate.value = goal?.targetDate ?? "";
  },
  { immediate: true }
);

const headline = computed(() => {
  if (!props.goal) return "Sin objetivo";
  return `${props.goal.targetWeightKg.toFixed(1)} kg`;
});

const copy = computed(() => {
  if (!props.goal || props.goal.remainingKg === null) {
    return "Define un peso objetivo para medir tu progreso frente al último registro.";
  }
  const remaining = props.goal.remainingKg;
  if (remaining > 0) return `Te faltan ${remaining.toFixed(1)} kg para llegar al objetivo.`;
  if (remaining < 0) return `Has superado el objetivo por ${Math.abs(remaining).toFixed(1)} kg.`;
  return "Ya has alcanzado tu objetivo.";
});

const helperChips = computed(() => {
  if (!props.goal) {
    return [
      "Te ayuda a medir el progreso real.",
      "Se compara contra tu último peso registrado."
    ];
  }

  return [
    `Inicio ${props.goal.startWeightKg.toFixed(1)} kg`,
    props.goal.targetDate ? `Meta ${props.goal.targetDate}` : "Sin fecha límite"
  ];
});

function handleSubmit() {
  emit("save", {
    startWeightKg: Number(startWeightKg.value),
    targetWeightKg: Number(targetWeightKg.value),
    targetDate: targetDate.value || undefined
  });
}
</script>

<style scoped>
.goal-card { padding: 22px; border-radius: 22px; background: rgba(255,255,255,0.88); border: 1px solid #d9e5f2; }
.goal-header { display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; }
.goal-label { margin: 0 0 6px; color: #5f7895; text-transform: uppercase; letter-spacing: 0.08em; font-size: 0.72rem; }
.goal-header h2 { margin: 0; }
.goal-progress { font-size: 1.6rem; color: #1f5e3b; }
.goal-copy { color: #4d627b; line-height: 1.5; }
.goal-chips { display: flex; gap: 10px; flex-wrap: wrap; margin: 0 0 18px; }
.goal-chip { display: inline-flex; align-items: center; padding: 8px 12px; border-radius: 999px; background: #eef4fb; color: #35506d; font-size: 0.85rem; font-weight: 600; }
.goal-form { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }
.goal-form label { display: grid; gap: 6px; }
.goal-form input { width: 100%; border: 1px solid #d3dfec; border-radius: 12px; padding: 10px 12px; }
.goal-form button { align-self: end; border: 0; border-radius: 12px; padding: 12px 14px; background: #132238; color: #f5f9ff; cursor: pointer; }
@media (max-width: 900px) { .goal-form { grid-template-columns: 1fr; } }
</style>
