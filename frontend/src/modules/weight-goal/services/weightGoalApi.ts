import { http } from "../../../shared/services/http";
import type { WeightGoal } from "../types/weightGoal";

export type UpsertWeightGoalPayload = {
  startWeightKg: number;
  targetWeightKg: number;
  targetDate?: string;
};

export function getWeightGoal() {
  return http<WeightGoal>("/weight-goal");
}

export function upsertWeightGoal(payload: UpsertWeightGoalPayload) {
  return http<WeightGoal>("/weight-goal", {
    method: "PUT",
    body: JSON.stringify(payload)
  });
}
