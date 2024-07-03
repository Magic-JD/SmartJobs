package org.smartjobs.adaptors.data;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CouponRepository;
import org.smartjobs.core.entities.RedeemableCoupon;
import org.smartjobs.core.ports.dal.CouponDal;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CouponDalImpl implements CouponDal {


    private final CouponRepository couponRepository;

    public CouponDalImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }


    @Override
    public Optional<RedeemableCoupon> getCoupon(long userId, String code) {
        var coupon = couponRepository.getByCodeAndUserId(code, userId);
        var redeemableCoupon = coupon.map(c -> new RedeemableCoupon(c.getValue(), c.isApplied(), c.isExpired()));
        coupon.ifPresent(c -> {
            if (!c.isApplied()) {
                c.setApplied(true);
                couponRepository.save(c);
            }
        });
        return redeemableCoupon;
    }
}
