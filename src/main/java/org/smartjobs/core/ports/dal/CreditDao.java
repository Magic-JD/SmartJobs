package org.smartjobs.core.ports.dal;

public interface CreditDao {
    int getUserCredits(String username);

    void event(String username, int amount, String type);
}
