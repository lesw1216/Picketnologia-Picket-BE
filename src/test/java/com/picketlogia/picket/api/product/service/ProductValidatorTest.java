package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.dto.register.ProductRegisterRequest;
import com.picketlogia.picket.api.product.validator.ProductValidator;
import com.picketlogia.picket.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductValidator.class})
class ProductValidatorTest {

    @Autowired
    private ProductValidator productValidator;

    @Test
    @DisplayName("오픈 예정일이 공연 시작일 보다 느리면 예외 발생")
    void validateOpenDate() {
        // given
        LocalDateTime openDate = LocalDateTime.now().plusDays(1);
        LocalDate startDate = LocalDate.now();

        // when
        // then
        assertThrows(BaseException.class, () -> productValidator.validate(ProductRegisterRequest.builder()
                        .openDate(openDate)
                        .startDate(startDate)
                        .build()
                )
        );
    }
}