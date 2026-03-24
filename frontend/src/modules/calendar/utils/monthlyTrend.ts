export type ChartTone = "down" | "up" | "flat" | "none";

export function buildChartCopy(baselineWeight: number | null): string {
  return baselineWeight !== null
    ? `Cada barra representa el peso medio y se compara con el peso inicial de ${baselineWeight.toFixed(1)} kg.`
    : "Cada barra representa el peso medio de ese mes.";
}

export function resolveTrendTone(value: number | null, baseline: number | null): ChartTone {
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

export function calculateBarHeight(value: number | null, min: number, range: number): number {
  if (value === null) {
    return 0;
  }

  return Math.max(((value - min) / range) * 100, 12);
}
