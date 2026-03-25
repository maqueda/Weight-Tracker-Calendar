import { describe, expect, it } from "vitest";
import { buildWeekSummaryPresentation, calculateStreakSummary, filterMonthsBySelection } from "./calendarInsights";

describe("calendarInsights helpers", () => {
  it("calculates current and best streaks from consecutive registered days", () => {
    const summary = calculateStreakSummary([
      { date: "2026-03-20", hasEntry: true },
      { date: "2026-03-21", hasEntry: true },
      { date: "2026-03-22", hasEntry: true },
      { date: "2026-03-24", hasEntry: true },
      { date: "2026-03-25", hasEntry: true }
    ]);

    expect(summary.currentStreak).toBe(2);
    expect(summary.bestStreak).toBe(3);
    expect(summary.lastEntryDate).toBe("2026-03-25");
  });

  it("returns zero streaks when there are no entries yet", () => {
    const summary = calculateStreakSummary([
      { date: "2026-03-20", hasEntry: false },
      { date: "2026-03-21", hasEntry: false }
    ]);

    expect(summary).toEqual({
      currentStreak: 0,
      bestStreak: 0,
      lastEntryDate: null
    });
  });

  it("explains which sunday is missing when a week is incomplete", () => {
    const presentation = buildWeekSummaryPresentation({
      weekNumber: 12,
      sundayStartDate: "2026-03-15",
      sundayStartWeight: null,
      sundayEndDate: "2026-03-22",
      sundayEndWeight: 79.3,
      deltaKg: null,
      trend: "INCOMPLETE",
      complete: false
    });

    expect(presentation.headline).toBe("Semana 12 incompleta");
    expect(presentation.copy).toContain("15 mar");
  });

  it("keeps the previous complete message when the week can be compared", () => {
    const presentation = buildWeekSummaryPresentation({
      weekNumber: 12,
      sundayStartDate: "2026-03-15",
      sundayStartWeight: 80.1,
      sundayEndDate: "2026-03-22",
      sundayEndWeight: 79.3,
      deltaKg: -0.8,
      trend: "DOWN",
      complete: true
    });

    expect(presentation.headline).toBe("Has bajado 0.80 kg");
    expect(presentation.copy).toContain("22 mar");
  });

  it("filters visible months when the user selects one concrete month", () => {
    const visible = filterMonthsBySelection(
      [
        { monthNumber: 1, name: "Enero" },
        { monthNumber: 2, name: "Febrero" }
      ],
      2
    );

    expect(visible).toEqual([{ monthNumber: 2, name: "Febrero" }]);
  });

  it("returns every month when the filter is set to all", () => {
    const visible = filterMonthsBySelection(
      [
        { monthNumber: 1, name: "Enero" },
        { monthNumber: 2, name: "Febrero" }
      ],
      "all"
    );

    expect(visible).toHaveLength(2);
  });
});
