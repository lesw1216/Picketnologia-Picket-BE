package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PerformanceRoundServiceTest {

    @Autowired
    private PerformanceRoundService performanceRoundService;

    @Autowired
    private ProductRepository productRepository;

//    @Test
//    void register() throws SQLException, IOException {
//
//        Product savedProduct = productRepository.save(Product.builder().build());
//
//        PerformanceRoundRegister.SelectedDay selectedDay1 = PerformanceRoundRegister.SelectedDay.builder()
//                .code(DayOfWeek.MONDAY.name())
//                .times(Arrays.asList(LocalTime.parse("10:00"), LocalTime.parse("14:00")))
//                .build();
//
//        PerformanceRoundRegister.SelectedDay selectedDay2 = PerformanceRoundRegister.SelectedDay.builder()
//                .code(DayOfWeek.WEDNESDAY.name())
//                .times(Arrays.asList(LocalTime.parse("12:00"), LocalTime.parse(("16:00"))))
//                .build();
//
//        PerformanceRoundRegister.ManualRound manualRound1 = PerformanceRoundRegister.ManualRound.builder()
//                .date(LocalDate.of(2025, 8, 25))
//                .time(LocalTime.parse("18:00"))
//                .build();
//
//        PerformanceRoundRegister.ManualRound manualRound2 = PerformanceRoundRegister.ManualRound.builder()
//                .date(LocalDate.of(2025, 8, 26))
//                .time(LocalTime.parse("20:00"))
//                .build();
//
//        PerformanceRoundRegister performance = PerformanceRoundRegister.builder()
//                .startDate(LocalDate.of(2025, 8, 20))
//                .endDate(LocalDate.of(2026, 8, 30))
//                .selectedDays(Arrays.asList(selectedDay1, selectedDay2))
//                .sameTimes(List.of())
//                .manualRounds(Arrays.asList(manualRound1, manualRound2))
//                .build();
//
//        performanceRoundService.register(performance, savedProduct);
//    }
}