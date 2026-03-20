package com.example.weighttrackercalendar.modules.weeklysummary.api;

import com.example.weighttrackercalendar.modules.weeklysummary.application.GetWeeklySummariesUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weekly-summaries")
public class WeeklySummaryController {

    private final GetWeeklySummariesUseCase getWeeklySummariesUseCase;

    public WeeklySummaryController(GetWeeklySummariesUseCase getWeeklySummariesUseCase) {
        this.getWeeklySummariesUseCase = getWeeklySummariesUseCase;
    }

    @GetMapping("/{year}")
    public List<WeeklySummaryResponse> getByYear(@PathVariable int year) {
        return getWeeklySummariesUseCase.handle(year);
    }
}

