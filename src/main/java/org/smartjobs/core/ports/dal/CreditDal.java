package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.CreditType;

public interface CreditDal {
    long getUserCredits(long userId);

    void event(long userId, int amount, CreditType type);
}
