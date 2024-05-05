package org.smartjobs.com.core.service.credit;

import org.smartjobs.com.core.dal.CreditDao;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CreditService {

    private final CreditDao creditDao;

    public CreditService(CreditDao creditDao) {
        this.creditDao = creditDao;
    }

    public boolean userHasEnoughCredits(String username) {
        // Retrieve current user from security context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return creditDao.getUserCredits(userDetails.getUsername()) > 0;
        }
        return false;
    }

}
