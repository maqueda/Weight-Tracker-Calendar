package com.example.weighttrackercalendar.modules.monthlysummary.api;

import com.example.weighttrackercalendar.modules.monthlysummary.application.GetMonthlySummariesUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monthly-summaries")
public class MonthlySummaryController {

    private final GetMonthlySummariesUseCase getMonthlySummariesUseCase;

    public MonthlySummaryController(GetMonthlySummariesUseCase getMonthlySummariesUseCase) {
        this.getMonthlySummariesUseCase = getMonthlySummariesUseCase;
    }

    @GetMapping("/{year}")
    public List<MonthlySummaryResponse> getByYear(@PathVariable int year) {
        return getMonthlySummariesUseCase.handle(year);
    }
}
