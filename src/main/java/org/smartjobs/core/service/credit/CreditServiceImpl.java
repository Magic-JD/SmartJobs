package org.smartjobs.core.service.credit;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.ports.dal.CreditDao;
import org.smartjobs.core.service.CreditService;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

import static org.smartjobs.core.entities.CreditEvent.DEBIT;
import static org.smartjobs.core.entities.CreditEvent.REFUND;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditDao creditDao;
    private final SseService sseService;
    private final DecimalFormat decimalFormat;

    public CreditServiceImpl(CreditDao creditDao, SseService sseService, DecimalFormat decimalFormat) {
        this.creditDao = creditDao;
        this.sseService = sseService;
        this.decimalFormat = decimalFormat;
    }

    @Override
    public boolean userHasEnoughCredits(long userId) {
        return creditDao.getUserCredits(userId) >= 0;
    }

    @Override
    public long userCredit(long userId) {
        return creditDao.getUserCredits(userId);
    }

    @Override
    public boolean debitAndVerify(long userId, int amount) {
        creditDao.event(userId, amount * -1, DEBIT);
        long remainingCredits = userCredit(userId);
        if (remainingCredits >= 0) {
            log.info("User {} has spent {} credits.", userId, amount);
            sseService.send(userId, "credit", STR. "Credit: \{ decimalFormat.format(remainingCredits) }" );
            return true;
        } else {
            log.info("User {} attempted to spend {} credits, but they didn't have sufficient credits.", userId, amount);
            creditDao.event(userId, amount, REFUND);
            return false;
        }
    }

    @Override
    public void refund(long userId, int amount) {
        log.error("User {} has had to be refunded {} credits.", userId, amount);
        creditDao.event(userId, amount, REFUND);
        long remainingCredits = userCredit(userId);
        sseService.send(userId, "credit", STR. "Credit: \{ decimalFormat.format(remainingCredits) }" );
    }

}
