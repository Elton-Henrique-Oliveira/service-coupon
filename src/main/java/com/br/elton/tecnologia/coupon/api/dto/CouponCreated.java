package com.br.elton.tecnologia.coupon.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponCreated(
        String code,
        String description,
        BigDecimal discountValue,
        LocalDateTime expirationDate,
        Boolean published
) {
}
