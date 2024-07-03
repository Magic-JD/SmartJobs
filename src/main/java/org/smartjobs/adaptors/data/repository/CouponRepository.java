package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> getByCodeAndUserId(String code, long userId);

}
