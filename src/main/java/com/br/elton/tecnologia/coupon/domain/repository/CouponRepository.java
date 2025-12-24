package com.br.elton.tecnologia.coupon.domain.repository;

import com.br.elton.tecnologia.coupon.domain.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query(
            "SELECT COUNT(c) > 0 " +
            " FROM Coupon c " +
            " WHERE c.deleted = false" +
            "   AND c.code = :code"
    )
    boolean existsByCode(String code);

    Optional<Coupon> findByCodeAndDeletedFalse(String code);

    @Query(
            "SELECT c " +
            " FROM Coupon c " +
            " WHERE c.deleted = false" +
            "   AND c.code = :code " +
            "   AND c.published = true " +
            "   AND c.expirationDate >= :date"
    )
    Optional<Coupon> findValidByCode(@Param("code") String code, @Param("date") LocalDateTime date);

    Optional<List<Coupon>> findAllByDeletedFalse();

    @Modifying
    @Transactional
    @Query("update Coupon c set c.deleted = true where c.id = :id and c.deleted = false")
    int softDeleteById(@Param("id") UUID id);
}
