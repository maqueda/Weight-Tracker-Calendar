import { http } from "../../../shared/services/http";

export type CreateWeightEntryPayload = {
  entryDate: string;
  weightKg: number;
  notes?: string;
};

export type UpdateWeightEntryPayload = {
  weightKg: number;
  notes?: string;
};

export type WeightEntryResponse = {
  id: number;
  entryDate: string;
  weightKg: number;
  notes: string | null;
};

export function createWeightEntry(payload: CreateWeightEntryPayload) {
  return http<WeightEntryResponse>("/weight-entries", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function updateWeightEntry(entryId: number, payload: UpdateWeightEntryPayload) {
  return http<WeightEntryResponse>(`/weight-entries/${entryId}`, {
    method: "PUT",
    body: JSON.stringify(payload)
  });
}
