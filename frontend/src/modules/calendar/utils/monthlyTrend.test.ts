import { describe, expect, it } from "vitest";
import { buildChartCopy, calculateBarHeight, resolveTrendTone } from "./monthlyTrend";

describe("monthlyTrend helpers", () => {
  it("uses the start weight as the explanatory reference of the chart", () => {
    expect(buildChartCopy(82)).toBe(
      "Cada barra representa el peso medio y se compara con el peso inicial de 82.0 kg."
    );
  });

  it("returns an explanatory fallback when there is no initial weight yet", () => {
    expect(buildChartCopy(null)).toBe("Cada barra representa el peso medio de ese mes.");
  });

  it("marks the month as down when the average is below the initial weight", () => {
    expect(resolveTrendTone(78.4, 80)).toBe("down");
  });

  it("marks the month as up when the average is above the initial weight", () => {
    expect(resolveTrendTone(80.4, 80)).toBe("up");
  });

  it("treats very small differences as stable", () => {
    expect(resolveTrendTone(80.05, 80)).toBe("flat");
  });

  it("keeps a minimum visible bar height when there is data", () => {
    expect(calculateBarHeight(78, 78, 4)).toBe(12);
    expect(calculateBarHeight(null, 78, 4)).toBe(0);
  });
});
