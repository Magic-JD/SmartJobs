package org.smartjobs.adaptors.data;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CouponRepository;
import org.smartjobs.adaptors.data.repository.data.Coupon;
import org.smartjobs.core.config.DateSupplier;
import org.smartjobs.core.entities.RedeemableCoupon;
import org.smartjobs.core.ports.dal.CouponDal;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CouponDalImpl implements CouponDal {


    private final CouponRepository couponRepository;
    private final DateSupplier dateSupplier;

    public CouponDalImpl(CouponRepository couponRepository, DateSupplier dateSupplier) {
        this.couponRepository = couponRepository;
        this.dateSupplier = dateSupplier;
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

    @Override
    public void issueCoupon(long userId, String code) {
        Coupon coupon = Coupon.builder()
                .code(code)
                .value(250)
                .created(dateSupplier.getDate())
                .userId(userId)
                .build();
        couponRepository.save(coupon);

    }
}
