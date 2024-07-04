package org.smartjobs.core.service;

import org.smartjobs.core.service.coupon.dto.EmailSendingResult;

import java.util.List;

public interface CouponService {

    void validateCoupon(long userId, String code);

    List<EmailSendingResult> issueCouponsFor(List<String> emails);
}
