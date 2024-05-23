package org.smartjobs.core.service;

public interface CreditService {
    boolean userHasEnoughCredits(long userId);

    long userCredit(long userId);

    boolean debitAndVerify(long userId, int amount);

    void refund(long userId, int amount);
}
