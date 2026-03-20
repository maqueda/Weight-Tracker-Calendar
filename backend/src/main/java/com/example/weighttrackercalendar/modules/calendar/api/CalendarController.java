package com.example.weighttrackercalendar.modules.calendar.api;

import com.example.weighttrackercalendar.modules.calendar.application.GetYearCalendarUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {

    private final GetYearCalendarUseCase getYearCalendarUseCase;

    public CalendarController(GetYearCalendarUseCase getYearCalendarUseCase) {
        this.getYearCalendarUseCase = getYearCalendarUseCase;
    }

    @GetMapping("/{year}")
    public YearCalendarResponse getCalendar(@PathVariable int year) {
        return getYearCalendarUseCase.handle(year);
    }
}

