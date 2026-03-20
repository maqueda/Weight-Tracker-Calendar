<template>
  <aside class="panel">
    <div v-if="selectedDay" class="panel-content">
      <p class="panel-label">Fecha seleccionada</p>
      <h2>{{ formattedDate }}</h2>
      <p class="panel-copy">
        Guarda tu peso diario y corrige el historico cuando lo necesites.
      </p>

      <form class="form" @submit.prevent="handleSubmit">
        <label>
          <span>Peso (kg)</span>
          <input
            v-model="weightKg"
            type="number"
            min="0.01"
            step="0.01"
            required
          />
        </label>

        <label>
          <span>Notas</span>
          <textarea
            v-model="notes"
            rows="4"
            placeholder="Opcional"
          />
        </label>

        <button :disabled="saving" type="submit">
          {{ selectedDay.hasEntry ? "Actualizar peso" : "Guardar peso" }}
        </button>
      </form>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import type { CalendarDay } from "../types/calendar";

const props = defineProps<{
  selectedDay: CalendarDay | null;
  saving: boolean;
}>();

const emit = defineEmits<{
  save: [payload: { entryId: number | null; entryDate: string; weightKg: number; notes?: string }];
}>();

const weightKg = ref("");
const notes = ref("");

watch(
  () => props.selectedDay,
  (day) => {
    weightKg.value = day?.weightKg?.toFixed(2) ?? "";
    notes.value = day?.notes ?? "";
  },
  { immediate: true }
);

const formattedDate = computed(() => {
  if (!props.selectedDay) {
    return "";
  }

  return new Date(props.selectedDay.date).toLocaleDateString("es-ES", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric"
  });
});

function handleSubmit() {
  if (!props.selectedDay) {
    return;
  }

  emit("save", {
    entryId: props.selectedDay.entryId,
    entryDate: props.selectedDay.date,
    weightKg: Number(weightKg.value),
    notes: notes.value || undefined
  });
}
</script>

<style scoped>
.panel {
  position: sticky;
  top: 24px;
}

.panel-content {
  border-radius: 24px;
  background: #132238;
  color: #f5f9ff;
  padding: 24px;
  box-shadow: 0 20px 50px rgba(19, 34, 56, 0.18);
}

.panel-label {
  margin: 0 0 8px;
  color: #97adc6;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.75rem;
}

.panel-copy {
  color: #c7d4e4;
  line-height: 1.5;
}

.form {
  display: grid;
  gap: 16px;
}

label {
  display: grid;
  gap: 8px;
}

input,
textarea {
  width: 100%;
  border: 1px solid #37506f;
  border-radius: 14px;
  padding: 12px 14px;
  background: #0d1a2b;
  color: #f5f9ff;
}

button {
  border: 0;
  border-radius: 14px;
  padding: 14px 18px;
  background: linear-gradient(135deg, #ffb347 0%, #ff7a59 100%);
  color: #132238;
  font-weight: 700;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: wait;
}
</style>
