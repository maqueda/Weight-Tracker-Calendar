export type CalendarDay = {
  entryId: number | null;
  date: string;
  month: number;
  dayOfMonth: number;
  dayOfWeek: string;
  weightKg: number | null;
  notes: string | null;
  hasEntry: boolean;
  sunday: boolean;
};

export type YearCalendar = {
  year: number;
  days: CalendarDay[];
};
