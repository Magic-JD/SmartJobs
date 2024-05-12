package org.smartjobs.core.service.credit;

import org.smartjobs.core.ports.dal.CreditDao;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.SseService;
import org.springframework.stereotype.Service;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditDao creditDao;
    private final SseService sseService;

    public CreditServiceImpl(CreditDao creditDao, SseService sseService) {
        this.creditDao = creditDao;
        this.sseService = sseService;
    }

    @Override
    public boolean userHasEnoughCredits(String username) {
        return creditDao.getUserCredits(username) >= 0;
    }

    @Override
    public int userCredit(String username) {
        return creditDao.getUserCredits(username);
    }

    @Override
    public boolean debitAndVerify(String username, int amount) {
        creditDao.event(username, amount * -1, "DEBIT");
        int remainingCredits = userCredit(username);
        if (remainingCredits >= 0) {
            sseService.send(username, "credit", STR. "<div>Credit: \{ remainingCredits }</div>" );
            return true;
        } else {
            creditDao.event(username, amount, "REFUND");
            return false;
        }
    }

}
