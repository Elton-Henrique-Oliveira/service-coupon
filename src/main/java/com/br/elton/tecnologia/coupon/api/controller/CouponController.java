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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Endpoints para gerenciar cupons")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Criar cupom", description = "Cria um novo cupom e retorna o recurso criado")
    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody @Valid CouponCreated couponCreated) {
        CouponResponse createdCoupon = couponService.create(couponCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @Operation(summary = "Deletar cupom (soft delete)", description = "Marca o cupom como deletado (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable @Parameter(description = "ID do cupom", required = true) UUID id) {
        Boolean couponDeleted = couponService.deleteById(id);
        return ResponseEntity.ok(couponDeleted);
    }

    @Operation(summary = "Listar cupons válidos", description = "Retorna a lista de cupons que não estão marcados como deletados e que sejam válidos")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> listValidCoupon() {
        List<CouponResponse> couponResponse = couponService.listValidCoupon();
        return ResponseEntity.ok(couponResponse);
    }
}
