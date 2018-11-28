package com.cart4j.product.dto.v1;

import com.cart4j.product.entity.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWeightDtoV1 {
    private Long id;

    private BigDecimal weight;

    private WeightUnit unit;
}
