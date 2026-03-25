import { describe, expect, it } from "vitest";
import { buildDashboardMetrics } from "./dashboardMetrics";
import type { CalendarDay } from "../types/calendar";
import type { WeightGoal } from "../../weight-goal/types/weightGoal";

describe("dashboardMetrics helpers", () => {
  it("builds the expected summary metrics from the latest available entries", () => {
    const metrics = buildDashboardMetrics(sampleDays(), sampleGoal(), 5, 2026);

    expect(metrics[0].value).toBe("79.4 kg");
    expect(metrics[1].value).toBe("-0.8 kg");
    expect(metrics[2].value).toBe("-1.0 kg");
    expect(metrics[3].value).toBe("80.0 kg");
    expect(metrics[4].value).toBe("5 días");
    expect(metrics[5].value).toBe("-1.4 kg");
  });

  it("falls back gracefully when there is not enough data yet", () => {
    const metrics = buildDashboardMetrics([], null, 0, 2026);

    expect(metrics[0].value).toBe("Sin datos");
    expect(metrics[1].value).toBe("Sin datos");
    expect(metrics[4].value).toBe("Sin racha");
    expect(metrics[5].value).toBe("Sin objetivo");
  });
});

function sampleDays(): CalendarDay[] {
  return [
    createDay("2026-03-01", 80.4),
    createDay("2026-03-15", 80.2),
    createDay("2026-03-18", 80.0),
    createDay("2026-03-20", 79.9),
    createDay("2026-03-22", 79.8),
    createDay("2026-03-25", 79.4)
  ];
}

function sampleGoal(): WeightGoal {
  return {
    id: 1,
    startWeightKg: 82,
    targetWeightKg: 78,
    targetDate: "2026-06-01",
    latestWeightKg: 79.4,
    remainingKg: -1.4,
    progressPercent: 65
  };
}

function createDay(date: string, weightKg: number): CalendarDay {
  return {
    entryId: 1,
    date,
    month: Number(date.slice(5, 7)),
    dayOfMonth: Number(date.slice(8, 10)),
    dayOfWeek: "MONDAY",
    weightKg,
    notes: null,
    hasEntry: true,
    sunday: false
  };
}
