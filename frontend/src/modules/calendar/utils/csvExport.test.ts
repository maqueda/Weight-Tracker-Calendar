import { describe, expect, it } from "vitest";
import { buildWeightEntriesCsv } from "./csvExport";
import type { CalendarDay } from "../types/calendar";

describe("csvExport helpers", () => {
  it("exports only the days that have weight and keeps a header row", () => {
    const csv = buildWeightEntriesCsv([
      createDay("2026-03-20", 79.8, "Piernas"),
      createDay("2026-03-21", null, null)
    ]);

    expect(csv).toContain("fecha,dia_semana,peso_kg,notas");
    expect(csv).toContain("2026-03-20,FRIDAY,79.8,Piernas");
    expect(csv).not.toContain("2026-03-21");
  });

  it("escapes commas and quotes in notes so the CSV stays valid", () => {
    const csv = buildWeightEntriesCsv([
      createDay("2026-03-20", 79.8, "Cena, \"libre\"")
    ]);

    expect(csv).toContain("\"Cena, \"\"libre\"\"\"");
  });
});

function createDay(date: string, weightKg: number | null, notes: string | null): CalendarDay {
  return {
    entryId: weightKg !== null ? 1 : null,
    date,
    month: Number(date.slice(5, 7)),
    dayOfMonth: Number(date.slice(8, 10)),
    dayOfWeek: "FRIDAY",
    weightKg,
    notes,
    hasEntry: weightKg !== null,
    sunday: false
  };
}
