package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.CreditEvent;

public interface CreditDal {
    long getUserCredits(long userId);

    void event(long userId, int amount, CreditEvent type);
}
