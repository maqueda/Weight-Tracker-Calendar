import { http } from "../../../shared/services/http";
import type { YearCalendar } from "../types/calendar";

export function getYearCalendar(year: number) {
  return http<YearCalendar>(`/calendar/${year}`);
}
