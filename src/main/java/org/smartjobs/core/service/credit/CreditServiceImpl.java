package org.smartjobs.core.service.credit;

import org.smartjobs.core.dal.CreditDao;
import org.smartjobs.core.service.CreditService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditDao creditDao;

    public CreditServiceImpl(CreditDao creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    public boolean userHasEnoughCredits(String username) {
        // Retrieve current user from security context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return creditDao.getUserCredits(userDetails.getUsername()) > 0;
        }
        return false;
    }

}
