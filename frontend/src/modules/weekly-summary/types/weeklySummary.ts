export type WeeklySummary = {
  weekNumber: number;
  sundayStartDate: string;
  sundayStartWeight: number | null;
  sundayEndDate: string;
  sundayEndWeight: number | null;
  deltaKg: number | null;
  trend: "UP" | "DOWN" | "FLAT" | "INCOMPLETE";
  complete: boolean;
};
