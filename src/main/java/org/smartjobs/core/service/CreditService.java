package org.smartjobs.core.service;

public interface CreditService {
    boolean userHasEnoughCredits(String username);

    int userCredit(String username);
}
