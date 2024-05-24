package org.smartjobs.core.service.credit;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.ports.dal.CreditDal;
import org.smartjobs.core.service.CreditService;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

import static org.smartjobs.core.entities.CreditEvent.DEBIT;
import static org.smartjobs.core.entities.CreditEvent.REFUND;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditDal creditDal;
    private final SseService sseService;
    private final DecimalFormat decimalFormat;

    public CreditServiceImpl(CreditDal creditDal, SseService sseService, DecimalFormat decimalFormat) {
        this.creditDal = creditDal;
        this.sseService = sseService;
        this.decimalFormat = decimalFormat;
    }

    @Override
    public boolean userHasEnoughCredits(long userId) {
        return creditDal.getUserCredits(userId) >= 0;
    }

    @Override
    public long userCredit(long userId) {
        return creditDal.getUserCredits(userId);
    }

    @Override
    public boolean debitAndVerify(long userId, int amount) {
        creditDal.event(userId, amount * -1, DEBIT);
        long remainingCredits = userCredit(userId);
        if (remainingCredits >= 0) {
            log.info("User {} has spent {} credits.", userId, amount);
            sseService.send(userId, "credit", STR. "Credit: \{ decimalFormat.format(remainingCredits) }" );
            return true;
        } else {
            log.info("User {} attempted to spend {} credits, but they didn't have sufficient credits.", userId, amount);
            creditDal.event(userId, amount, REFUND);
            return false;
        }
    }

    @Override
    public void refund(long userId, int amount) {
        log.error("User {} has had to be refunded {} credits.", userId, amount);
        creditDal.event(userId, amount, REFUND);
        long remainingCredits = userCredit(userId);
        sseService.send(userId, "credit", STR. "Credit: \{ decimalFormat.format(remainingCredits) }" );
    }

}
