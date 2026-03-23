export type MonthlySummary = {
  month: number;
  monthLabel: string;
  entriesCount: number;
  averageWeightKg: number | null;
  minWeightKg: number | null;
  maxWeightKg: number | null;
  deltaKg: number | null;
};
