import { http } from "../../../shared/services/http";
import type { WeeklySummary } from "../types/weeklySummary";

export function getWeeklySummaries(year: number) {
  return http<WeeklySummary[]>(`/weekly-summaries/${year}`);
}
