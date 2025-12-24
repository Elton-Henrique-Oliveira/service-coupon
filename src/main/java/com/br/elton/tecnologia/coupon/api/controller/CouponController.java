package com.br.elton.tecnologia.coupon.api.controller;

import com.br.elton.tecnologia.coupon.api.dto.CouponCreated;
import com.br.elton.tecnologia.coupon.api.dto.CouponResponse;
import com.br.elton.tecnologia.coupon.domain.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody @Valid CouponCreated couponCreated) {
        CouponResponse createdCoupon = couponService.create(couponCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean couponDeleted = couponService.deleteById(id);
        return ResponseEntity.ok(couponDeleted);
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> listValidCoupon() {
        List<CouponResponse> couponResponse = couponService.listValidCoupon();
        return ResponseEntity.ok(couponResponse);
    }
}
