package org.smartjobs.core.service;

public interface CreditService {

    long userCredit(long userId);

    void debit(long userId, int amount);

    void refund(long userId, int amount);
}
