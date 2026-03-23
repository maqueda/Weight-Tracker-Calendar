export type WeightGoal = {
  id: number;
  startWeightKg: number;
  targetWeightKg: number;
  targetDate: string | null;
  latestWeightKg: number | null;
  remainingKg: number | null;
  progressPercent: number | null;
};
