import type { CalendarDay } from "../types/calendar";
import type { WeightGoal } from "../../weight-goal/types/weightGoal";

export type DashboardMetric = {
  label: string;
  value: string;
  hint: string;
  tone: "neutral" | "up" | "down" | "good";
};

export function buildDashboardMetrics(
  days: CalendarDay[],
  goal: WeightGoal | null,
  bestStreak: number,
  year: number
): DashboardMetric[] {
  const entries = days.filter((day) => day.weightKg !== null);
  const latestEntry = entries[entries.length - 1] ?? null;
  const latestWeight = latestEntry?.weightKg ?? null;
  const weekAgoWeight = latestEntry ? findLatestEntryOnOrBefore(entries, shiftDate(latestEntry.date, -7))?.weightKg ?? null : null;
  const monthStartWeight = latestEntry ? findFirstEntryOfMonth(entries, latestEntry.date)?.weightKg ?? null : null;
  const currentMonthAverage = latestEntry ? calculateMonthAverage(entries, latestEntry.date) : null;
  const goalRemaining = goal?.remainingKg ?? null;

  return [
    {
      label: "Peso actual",
      value: latestWeight !== null ? `${latestWeight.toFixed(1)} kg` : "Sin datos",
      hint: latestEntry ? `Último registro: ${formatDate(latestEntry.date)}` : "Todavía no hay registros en este año.",
      tone: "neutral"
    },
    {
      label: "Vs hace 7 días",
      value: formatDelta(latestWeight, weekAgoWeight),
      hint: weekAgoWeight !== null ? `Comparado con ${formatDate(shiftDate(latestEntry!.date, -7))}` : "Necesitas un registro de al menos hace una semana.",
      tone: resolveDeltaTone(latestWeight, weekAgoWeight)
    },
    {
      label: "Mes actual",
      value: formatDelta(latestWeight, monthStartWeight),
      hint: monthStartWeight !== null ? "Cambio respecto al primer peso del mes." : "Aún no hay referencia mensual suficiente.",
      tone: resolveDeltaTone(latestWeight, monthStartWeight)
    },
    {
      label: "Media del mes",
      value: currentMonthAverage !== null ? `${currentMonthAverage.toFixed(1)} kg` : "Sin datos",
      hint: currentMonthAverage !== null ? "Promedio del mes del último registro." : "Registra más pesos para calcular la media.",
      tone: "neutral"
    },
    {
      label: "Mejor racha",
      value: bestStreak > 0 ? `${bestStreak} días` : "Sin racha",
      hint: bestStreak > 0 ? `Tu mejor secuencia de ${year}.` : "La racha aparecerá cuando encadenes registros.",
      tone: bestStreak > 0 ? "good" : "neutral"
    },
    {
      label: "Objetivo restante",
      value: goalRemaining !== null ? `${goalRemaining.toFixed(1)} kg` : "Sin objetivo",
      hint: goalRemaining !== null ? "Diferencia entre tu último peso y el objetivo." : "Define un objetivo para ver cuánto falta.",
      tone: goalRemaining !== null && goalRemaining <= 0 ? "good" : "neutral"
    }
  ];
}

function calculateMonthAverage(entries: CalendarDay[], referenceDate: string): number | null {
  const [year, month] = referenceDate.split("-").map(Number);
  const monthEntries = entries.filter((entry) => {
    const [entryYear, entryMonth] = entry.date.split("-").map(Number);
    return entryYear === year && entryMonth === month && entry.weightKg !== null;
  });

  if (monthEntries.length === 0) {
    return null;
  }

  const total = monthEntries.reduce((sum, entry) => sum + (entry.weightKg ?? 0), 0);
  return total / monthEntries.length;
}

function findFirstEntryOfMonth(entries: CalendarDay[], referenceDate: string): CalendarDay | null {
  const [year, month] = referenceDate.split("-").map(Number);
  return entries.find((entry) => {
    const [entryYear, entryMonth] = entry.date.split("-").map(Number);
    return entryYear === year && entryMonth === month && entry.weightKg !== null;
  }) ?? null;
}

function findLatestEntryOnOrBefore(entries: CalendarDay[], limitDate: string): CalendarDay | null {
  const eligibleEntries = entries.filter((entry) => entry.date <= limitDate && entry.weightKg !== null);
  return eligibleEntries[eligibleEntries.length - 1] ?? null;
}

function shiftDate(value: string, days: number): string {
  const date = new Date(`${value}T00:00:00`);
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

function formatDate(value: string): string {
  return new Date(`${value}T00:00:00`).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "short"
  });
}

function formatDelta(current: number | null, reference: number | null): string {
  if (current === null || reference === null) {
    return "Sin datos";
  }

  const delta = current - reference;
  const prefix = delta > 0 ? "+" : "";
  return `${prefix}${delta.toFixed(1)} kg`;
}

function resolveDeltaTone(current: number | null, reference: number | null): DashboardMetric["tone"] {
  if (current === null || reference === null) {
    return "neutral";
  }

  const delta = current - reference;
  if (delta > 0.05) {
    return "up";
  }
  if (delta < -0.05) {
    return "down";
  }
  return "neutral";
}
