package org.smartjobs.core.service.coupon;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.RedeemableCoupon;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.IssueCouponEvent;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponAlreadyAppliedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponDoesNotExistException;
import org.smartjobs.core.ports.dal.CouponDal;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.smartjobs.core.service.CouponService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.coupon.dto.EmailSendingResult;
import org.smartjobs.core.service.user.CodeSupplier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.smartjobs.core.exception.categories.UserResolvedExceptions.CouponExpiredException;


@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponDal couponDal;
    private final CredentialDal credentialDal;
    private final CreditService creditService;
    private final EventEmitter emitter;
    private final CodeSupplier codeSupplier;

    public CouponServiceImpl(CouponDal couponDal, CredentialDal credentialDal, CreditService creditService, EventEmitter emitter, CodeSupplier codeSupplier) {
        this.couponDal = couponDal;
        this.credentialDal = credentialDal;
        this.creditService = creditService;
        this.emitter = emitter;
        this.codeSupplier = codeSupplier;
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

    @Override
    public List<EmailSendingResult> issueCouponsFor(List<String> emails) {
        return emails.stream().map(e -> credentialDal.getUser(e).map(u -> {
            String code = codeSupplier.createCode();
            couponDal.issueCoupon(u.getId(), code);
            emitter.sendEvent(new IssueCouponEvent(u.getUsername(), code));
            return new EmailSendingResult(u.getUsername(), true);
        }).orElse(new EmailSendingResult(e, false))).toList();
    }
}
