package com.develhope.spring.dealershipStatistics.controllers;

import com.develhope.spring.dealershipStatistics.service.DealershipStatisticsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class DealershipStatisticsController {

    @Autowired
    private DealershipStatisticsService dealershipStatisticsService;
}
