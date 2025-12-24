package com.br.elton.tecnologia.coupon.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@SuppressWarnings("unused")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, length = 6)
    @NotBlank
    @Size(min = 6, max = 6)
    private String code;

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.5", inclusive = true)
    private BigDecimal discountValue;

    @Column(name = "expiration_date", nullable = false)
    @NotNull
    @FutureOrPresent
    private LocalDateTime expirationDate;

    @Column(name = "published", nullable = false)
    @Builder.Default
    private Boolean published = false;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }

    public void softDelete() {
        if (isDeleted()) {
            throw new IllegalStateException("Cupom já está deletado.");
        }
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    public static String sanitizeCode(String code) {
        if (code == null) return null;
        return code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    @PrePersist
    @PreUpdate
    private void sanitizeCode() {
        if (this.code != null) {
            this.code = sanitizeCode(this.code);
        }
    }

    public void validateForCreation() {
        if (this.code == null) {
            throw new IllegalArgumentException("O código do cupom deve conter exatamente 6 caracteres alfanuméricos");
        }

        if (this.code.length() != 6) {
            throw new IllegalArgumentException("O código do cupom deve conter exatamente 6 caracteres alfanuméricos");
        }

        if (this.discountValue == null) {
            throw new IllegalArgumentException("Desconto mínimo é 0.5");
        }

        if (this.discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new IllegalArgumentException("Desconto mínimo é 0.5");
        }

        if (this.expirationDate == null) {
            throw new IllegalArgumentException("A data de expiração deve ser informada");
        }

        if (this.expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data de expiração deve ser uma data futura");
        }
    }
}