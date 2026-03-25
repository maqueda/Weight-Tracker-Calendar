import type { WeeklySummary } from "../../weekly-summary/types/weeklySummary";

type EntryLike = {
  date: string;
  hasEntry: boolean;
};

type MonthLike = {
  monthNumber: number;
};

export type StreakSummary = {
  currentStreak: number;
  bestStreak: number;
  lastEntryDate: string | null;
};

export type WeekSummaryPresentation = {
  headline: string;
  copy: string;
};

export function calculateStreakSummary(days: EntryLike[]): StreakSummary {
  const entryDates = days
    .filter((day) => day.hasEntry)
    .map((day) => day.date)
    .sort();

  if (entryDates.length === 0) {
    return {
      currentStreak: 0,
      bestStreak: 0,
      lastEntryDate: null
    };
  }

  let bestStreak = 1;
  let runningStreak = 1;

  for (let index = 1; index < entryDates.length; index += 1) {
    if (daysBetween(entryDates[index - 1], entryDates[index]) === 1) {
      runningStreak += 1;
      bestStreak = Math.max(bestStreak, runningStreak);
    } else {
      runningStreak = 1;
    }
  }

  let currentStreak = 1;
  for (let index = entryDates.length - 1; index > 0; index -= 1) {
    if (daysBetween(entryDates[index - 1], entryDates[index]) === 1) {
      currentStreak += 1;
    } else {
      break;
    }
  }

  return {
    currentStreak,
    bestStreak,
    lastEntryDate: entryDates[entryDates.length - 1]
  };
}

export function buildWeekSummaryPresentation(summary: WeeklySummary | null): WeekSummaryPresentation {
  if (!summary) {
    return {
      headline: "Sin datos",
      copy: ""
    };
  }

  if (summary.complete && summary.deltaKg !== null) {
    if (summary.deltaKg > 0) {
      return {
        headline: `Has subido ${summary.deltaKg.toFixed(2)} kg`,
        copy: `Del ${formatShortDate(summary.sundayStartDate)} al ${formatShortDate(summary.sundayEndDate)}.`
      };
    }
    if (summary.deltaKg < 0) {
      return {
        headline: `Has bajado ${Math.abs(summary.deltaKg).toFixed(2)} kg`,
        copy: `Del ${formatShortDate(summary.sundayStartDate)} al ${formatShortDate(summary.sundayEndDate)}.`
      };
    }
    return {
      headline: "Peso estable",
      copy: `Del ${formatShortDate(summary.sundayStartDate)} al ${formatShortDate(summary.sundayEndDate)}.`
    };
  }

  const missingDates: string[] = [];
  if (summary.sundayStartWeight === null) {
    missingDates.push(formatFullDate(summary.sundayStartDate));
  }
  if (summary.sundayEndWeight === null) {
    missingDates.push(formatFullDate(summary.sundayEndDate));
  }

  const copy = missingDates.length === 2
    ? `Faltan los pesos de ${missingDates[0]} y ${missingDates[1]} para comparar la semana.`
    : `Falta el peso de ${missingDates[0]} para comparar la semana.`;

  return {
    headline: `Semana ${summary.weekNumber} incompleta`,
    copy
  };
}

export function filterMonthsBySelection<T extends MonthLike>(months: T[], selectedMonth: number | "all"): T[] {
  if (selectedMonth === "all") {
    return months;
  }
  return months.filter((month) => month.monthNumber === selectedMonth);
}

function daysBetween(first: string, second: string): number {
  const firstDate = new Date(`${first}T00:00:00`);
  const secondDate = new Date(`${second}T00:00:00`);
  return Math.round((secondDate.getTime() - firstDate.getTime()) / 86400000);
}

function formatShortDate(value: string): string {
  return new Date(`${value}T00:00:00`).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "short"
  });
}

function formatFullDate(value: string): string {
  return new Date(`${value}T00:00:00`).toLocaleDateString("es-ES", {
    weekday: "long",
    day: "numeric",
    month: "short"
  });
}
