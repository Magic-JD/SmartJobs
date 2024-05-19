package org.smartjobs.core.ports.dal;

public interface CreditDao {
    int getUserCredits(long userId);

    void event(long userId, int amount, String type);
}
