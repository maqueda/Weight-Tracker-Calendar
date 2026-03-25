import { defineStore } from "pinia";
import { getYearCalendar } from "../services/calendarApi";
import type { CalendarDay } from "../types/calendar";
import { getWeeklySummaries } from "../../weekly-summary/services/weeklySummaryApi";
import type { WeeklySummary } from "../../weekly-summary/types/weeklySummary";
import { createWeightEntry, updateWeightEntry } from "../../weight-entry/services/weightEntryApi";
import { getWeightGoal, upsertWeightGoal } from "../../weight-goal/services/weightGoalApi";
import type { WeightGoal } from "../../weight-goal/types/weightGoal";
import { getMonthlySummaries } from "../../monthly-summary/services/monthlySummaryApi";
import type { MonthlySummary } from "../../monthly-summary/types/monthlySummary";

type SaveEntryInput = {
  entryId: number | null;
  entryDate: string;
  weightKg: number;
  notes?: string;
};

export const useCalendarStore = defineStore("calendar", {
  state: () => ({
    year: new Date().getFullYear(),
    days: [] as CalendarDay[],
    weeklySummaries: [] as WeeklySummary[],
    monthlySummaries: [] as MonthlySummary[],
    weightGoal: null as WeightGoal | null,
    loading: false,
    saving: false,
    savingGoal: false,
    errorMessage: "",
    selectedDate: ""
  }),
  getters: {
    selectedDay(state) {
      return state.days.find((day) => day.date === state.selectedDate) ?? null;
    },
    currentWeekSummary(state) {
      const selected = state.selectedDate || new Date().toISOString().slice(0, 10);
      return state.weeklySummaries.find((summary) => summary.sundayEndDate >= selected)
        ?? state.weeklySummaries[state.weeklySummaries.length - 1]
        ?? null;
    }
  },
  actions: {
    async loadYear(year?: number) {
      await this.fetchYearData(year ?? this.year, true);
    },
    async fetchYearData(targetYear: number, showLoading: boolean) {
      if (showLoading) {
        this.loading = true;
      }
      this.errorMessage = "";
      try {
        const [calendar, weeklySummaries, monthlySummaries, weightGoal] = await Promise.all([
          getYearCalendar(targetYear),
          getWeeklySummaries(targetYear),
          getMonthlySummaries(targetYear),
          getWeightGoal()
        ]);
        this.year = calendar.year;
        this.days = calendar.days;
        this.weeklySummaries = weeklySummaries;
        this.monthlySummaries = monthlySummaries;
        this.weightGoal = weightGoal;
        if (!this.days.some((day) => day.date === this.selectedDate) && this.days.length > 0) {
          this.selectedDate = this.days[0].date;
        }
      } catch (error) {
        this.errorMessage = error instanceof Error ? error.message : "No se pudieron cargar los datos.";
      } finally {
        if (showLoading) {
          this.loading = false;
        }
      }
    },
    selectDate(date: string) {
      this.selectedDate = date;
    },
    async saveEntry(input: SaveEntryInput) {
      this.saving = true;
      this.errorMessage = "";
      try {
        if (input.entryId) {
          await updateWeightEntry(input.entryId, {
            weightKg: input.weightKg,
            notes: input.notes
          });
        } else {
          await createWeightEntry({
            entryDate: input.entryDate,
            weightKg: input.weightKg,
            notes: input.notes
          });
        }

        await this.fetchYearData(this.year, false);
        this.selectedDate = input.entryDate;
      } catch (error) {
        this.errorMessage = error instanceof Error ? error.message : "No se pudo guardar el registro.";
        throw error;
      } finally {
        this.saving = false;
      }
    },
    async saveGoal(input: { startWeightKg: number; targetWeightKg: number; targetDate?: string }) {
      this.savingGoal = true;
      this.errorMessage = "";
      try {
        this.weightGoal = await upsertWeightGoal(input);
      } catch (error) {
        this.errorMessage = error instanceof Error ? error.message : "No se pudo guardar el objetivo.";
        throw error;
      } finally {
        this.savingGoal = false;
      }
    }
  }
});
