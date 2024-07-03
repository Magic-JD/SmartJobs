package org.smartjobs.core.service.coupon;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.RedeemableCoupon;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponAlreadyAppliedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponDoesNotExistException;
import org.smartjobs.core.ports.dal.CouponDal;
import org.smartjobs.core.service.CouponService;
import org.smartjobs.core.service.CreditService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponExpiredException;


@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponDal couponDal;
    private final CreditService creditService;

    public CouponServiceImpl(CouponDal couponDal, CreditService creditService) {
        this.couponDal = couponDal;
        this.creditService = creditService;
    }

    @Override
    public void validateCoupon(long userId, String code) {
        Optional<RedeemableCoupon> couponOptional = couponDal.getCoupon(userId, code);
        if (couponOptional.isEmpty()) {
            throw new CouponDoesNotExistException(userId);
        }
        RedeemableCoupon coupon = couponOptional.get();
        if (coupon.applied()) {
            throw new CouponAlreadyAppliedException(userId);
        }
        if (coupon.expired()) {
            throw new CouponExpiredException(userId);
        }
        creditService.credit(userId, coupon.value());
    }
}
