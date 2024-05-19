package org.smartjobs.core.service;

public interface CreditService {
    boolean userHasEnoughCredits(long userId);

    int userCredit(long userId);

    boolean debitAndVerify(long userId, int size);
}
