package com.br.elton.tecnologia.coupon.api.mapper;

import com.br.elton.tecnologia.coupon.api.dto.CouponCreated;
import com.br.elton.tecnologia.coupon.api.dto.CouponResponse;
import com.br.elton.tecnologia.coupon.domain.model.Coupon;

public final class CouponMapper {

    private CouponMapper() {
    }

    public static CouponResponse toResponse(Coupon coupon) {
        if (coupon == null) {
            return null;
        }

        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getPublished()
        );
    }

    public static Coupon toEntity(CouponCreated couponCreated) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponCreated.code());
        coupon.setDescription(couponCreated.description());
        coupon.setDiscountValue(couponCreated.discountValue());
        coupon.setExpirationDate(couponCreated.expirationDate());
        coupon.setPublished(couponCreated.published() != null ? couponCreated.published() : false);
        return coupon;
    }
}