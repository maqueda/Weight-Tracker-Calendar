package com.example.weighttrackercalendar.modules.calendar.api;

import java.util.List;

public record YearCalendarResponse(
        int year,
        List<CalendarDayResponse> days
) {
}

