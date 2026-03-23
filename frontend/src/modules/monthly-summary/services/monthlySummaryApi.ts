import { http } from "../../../shared/services/http";
import type { MonthlySummary } from "../types/monthlySummary";

export function getMonthlySummaries(year: number) {
  return http<MonthlySummary[]>(`/monthly-summaries/${year}`);
}
