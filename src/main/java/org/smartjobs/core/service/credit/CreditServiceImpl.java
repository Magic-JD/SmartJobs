package org.smartjobs.core.service.credit;

import org.smartjobs.core.ports.dal.CreditDao;
import org.smartjobs.core.service.CreditService;
import org.springframework.stereotype.Service;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditDao creditDao;

    public CreditServiceImpl(CreditDao creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    public boolean userHasEnoughCredits(String username) {
        return creditDao.getUserCredits(username) > 0;
    }

    @Override
    public int userCredit(String username) {
        return creditDao.getUserCredits(username);
    }

}
