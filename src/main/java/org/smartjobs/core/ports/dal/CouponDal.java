package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.RedeemableCoupon;

import java.util.Optional;

public interface CouponDal {

    Optional<RedeemableCoupon> getCoupon(long userId, String code);

}
