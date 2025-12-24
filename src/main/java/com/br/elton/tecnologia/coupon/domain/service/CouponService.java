package com.br.elton.tecnologia.coupon.domain.service;

import com.br.elton.tecnologia.coupon.api.dto.CouponCreated;
import com.br.elton.tecnologia.coupon.api.dto.CouponResponse;
import com.br.elton.tecnologia.coupon.api.exception.BussinesException;
import com.br.elton.tecnologia.coupon.api.mapper.CouponMapper;
import com.br.elton.tecnologia.coupon.domain.model.Coupon;
import com.br.elton.tecnologia.coupon.domain.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponResponse create(CouponCreated couponCreated) {
        String sanitizedCode = Coupon.sanitizeCode(couponCreated.code());

        if (couponRepository.existsByCode(sanitizedCode)) {
            throw new BussinesException("Já existe um cupom com este código");
        }

        Coupon coupon = CouponMapper.toEntity(couponCreated);

        try {
            coupon.validateForCreation();
        } catch (IllegalArgumentException e) {
            throw new BussinesException(e.getMessage());
        }

        return CouponMapper.toResponse(couponRepository.save(coupon));
    }

    public List<CouponResponse> listValidCoupon() {
        Optional<List<Coupon>> coupons =
                couponRepository.findAllByDeletedFalse();

        return coupons
                .map(couponList -> couponList.stream()
                        .map(CouponMapper::toResponse)
                        .toList())
                .orElseThrow(() -> new BussinesException("Nenhum cupom válido encontrado!"));
    }

    public Boolean deleteById(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BussinesException("Nenhum cupom encontrado com o ID informado!"));

        try {
            coupon.softDelete();
        } catch (IllegalStateException e) {
            throw new BussinesException(e.getMessage());
        }

        couponRepository.save(coupon);
        return true;
    }
}