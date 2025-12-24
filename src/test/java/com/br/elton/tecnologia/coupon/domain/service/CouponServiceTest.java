package com.br.elton.tecnologia.coupon.domain.service;

import com.br.elton.tecnologia.coupon.api.dto.CouponCreated;
import com.br.elton.tecnologia.coupon.api.dto.CouponResponse;
import com.br.elton.tecnologia.coupon.api.exception.BussinesException;
import com.br.elton.tecnologia.coupon.domain.model.Coupon;
import com.br.elton.tecnologia.coupon.domain.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponService couponService;

    @Captor
    ArgumentCaptor<Coupon> couponCaptor;

    private static class Companion {
        static final String CREATED_CODE_RAW = "ab.cd12";
        static final String EXPECTED_CODE = "ABCD12";
        static final String SHORT_CODE_RAW = "ab1!";
        static final String DESC = "desc";
        static final String SANITIZED = "ABCDEF";
        static final String MIN_DISCOUNT_CODE = "ABC123";

        static final String MSG_EXACTLY_6 = "O código do cupom deve conter exatamente 6 caracteres alfanuméricos";
        static final String MSG_ALREADY_EXISTS = "Já existe um cupom com este código";
        static final String MSG_MIN_DISCOUNT = "Desconto mínimo é 0.5";
        static final String MSG_EXPIRATION = "A data de expiração deve ser uma data futura";
        static final String MSG_NONE_VALID = "Nenhum cupom válido encontrado!";
        static final String MSG_NONE_FOUND = "Nenhum cupom encontrado com o ID informado";
    }

    @BeforeEach
    void setUp() {
        couponService = new CouponService(couponRepository);
    }

    @Test
    void create_shouldSaveAndReturnResponse_whenValid() {
        CouponCreated created = new CouponCreated(Companion.EXPECTED_CODE, Companion.DESC, BigDecimal.valueOf(1.50), LocalDateTime.now().plusDays(10), true);
        String expectedCode = Companion.EXPECTED_CODE;

        when(couponRepository.existsByCode(expectedCode)).thenReturn(false);

        Coupon saved = Coupon.builder()
                .id(UUID.randomUUID())
                .code(expectedCode)
                .description(Companion.DESC)
                .discountValue(BigDecimal.valueOf(1.50))
                .expirationDate(created.expirationDate())
                .published(true)
                .deleted(false)
                .build();

        when(couponRepository.save(any(Coupon.class))).thenReturn(saved);

        CouponResponse response = couponService.create(created);

        assertNotNull(response);
        assertEquals(expectedCode, response.code());
        assertEquals(saved.getDiscountValue(), response.discountValue());
        verify(couponRepository).existsByCode(expectedCode);
        verify(couponRepository).save(couponCaptor.capture());
        Coupon captured = couponCaptor.getValue();
        assertEquals(expectedCode, captured.getCode());
        assertEquals(Companion.DESC, captured.getDescription());
    }

    @Test
    void create_shouldThrow_whenSanitizedCodeLengthNot6() {
        CouponCreated created = new CouponCreated(Companion.SHORT_CODE_RAW, Companion.DESC, BigDecimal.valueOf(1.00), LocalDateTime.now().plusDays(1), false);

        String expectedSanitized = Coupon.sanitizeCode(created.code());

        when(couponRepository.existsByCode(expectedSanitized)).thenReturn(false);

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.create(created));
        assertTrue(ex.getMessage().contains(Companion.MSG_EXACTLY_6));
        verify(couponRepository).existsByCode(expectedSanitized);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenSanitizedCodeLengthNot6_codeIsNull() {
        CouponCreated created = new CouponCreated(null, Companion.DESC, BigDecimal.valueOf(1.00), LocalDateTime.now().plusDays(1), false);

        String expectedSanitized = Coupon.sanitizeCode(created.code());

        when(couponRepository.existsByCode(expectedSanitized)).thenReturn(false);

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.create(created));
        assertTrue(ex.getMessage().contains(Companion.MSG_EXACTLY_6));
        verify(couponRepository).existsByCode(expectedSanitized);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenCodeAlreadyExists() {
        CouponCreated created = new CouponCreated("abcDEF", Companion.DESC, BigDecimal.valueOf(1.00), LocalDateTime.now().plusDays(1), false);
        String sanitized = Companion.SANITIZED;

        when(couponRepository.existsByCode(sanitized)).thenReturn(true);

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.create(created));
        assertTrue(ex.getMessage().contains(Companion.MSG_ALREADY_EXISTS));
        verify(couponRepository).existsByCode(sanitized);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenDiscountTooSmall() {
        CouponCreated created = new CouponCreated(Companion.MIN_DISCOUNT_CODE, Companion.DESC, BigDecimal.valueOf(0.40), LocalDateTime.now().plusDays(1), false);

        when(couponRepository.existsByCode(Companion.MIN_DISCOUNT_CODE)).thenReturn(false);

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.create(created));
        assertTrue(ex.getMessage().contains(Companion.MSG_MIN_DISCOUNT));
        verify(couponRepository).existsByCode(Companion.MIN_DISCOUNT_CODE);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenExpirationDateInPast() {
        CouponCreated created = new CouponCreated(Companion.MIN_DISCOUNT_CODE, Companion.DESC, BigDecimal.valueOf(1.00), LocalDateTime.now().minusDays(1), false);

        when(couponRepository.existsByCode(Companion.MIN_DISCOUNT_CODE)).thenReturn(false);

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.create(created));
        assertTrue(ex.getMessage().contains(Companion.MSG_EXPIRATION));
        verify(couponRepository).existsByCode(Companion.MIN_DISCOUNT_CODE);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void listValidCoupon_shouldReturnResponses_whenFound() {
        Coupon c1 = Coupon.builder()
                .id(UUID.randomUUID())
                .code("AAA111")
                .description("d1")
                .discountValue(BigDecimal.valueOf(1))
                .expirationDate(LocalDateTime.now().plusDays(5))
                .published(true)
                .deleted(false)
                .build();

        Coupon c2 = Coupon.builder()
                .id(UUID.randomUUID())
                .code("BBB222")
                .description("d2")
                .discountValue(BigDecimal.valueOf(2))
                .expirationDate(LocalDateTime.now().plusDays(10))
                .published(true)
                .deleted(false)
                .build();

        when(couponRepository.findAllByDeletedFalse()).thenReturn(Optional.of(List.of(c1, c2)));

        List<CouponResponse> responses = couponService.listValidCoupon();

        assertEquals(2, responses.size());
        assertEquals("AAA111", responses.get(0).code());
        assertEquals("BBB222", responses.get(1).code());
    }

    @Test
    void listValidCoupon_shouldThrow_whenNoneFound() {
        when(couponRepository.findAllByDeletedFalse()).thenReturn(Optional.empty());

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.listValidCoupon());
        assertTrue(ex.getMessage().contains(Companion.MSG_NONE_VALID));
    }

    @Test
    void deleteById_shouldReturnTrue_whenDeleted() {
        UUID id = UUID.randomUUID();

        Coupon existing = Coupon.builder()
                .id(id)
                .code("DEL123")
                .description("d")
                .discountValue(BigDecimal.valueOf(1))
                .expirationDate(LocalDateTime.now().plusDays(5))
                .published(true)
                .deleted(false)
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(existing));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boolean result = couponService.deleteById(id);

        assertTrue(result);
        verify(couponRepository).findById(id);
        verify(couponRepository).save(couponCaptor.capture());
        Coupon saved = couponCaptor.getValue();
        assertTrue(saved.isDeleted());
    }

    @Test
    void deleteById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        BussinesException ex = assertThrows(BussinesException.class, () -> couponService.deleteById(id));
        assertTrue(ex.getMessage().contains(Companion.MSG_NONE_FOUND));
        verify(couponRepository).findById(id);
    }
}
