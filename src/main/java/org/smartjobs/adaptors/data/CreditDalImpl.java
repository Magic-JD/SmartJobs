package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.CreditRepository;
import org.smartjobs.adaptors.data.repository.data.Credit;
import org.smartjobs.core.constants.CreditType;
import org.smartjobs.core.ports.dal.CreditDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditDalImpl implements CreditDal {

    private final CreditRepository repository;

    @Autowired
    public CreditDalImpl(CreditRepository repository) {
        this.repository = repository;
    }

    @Override
    public long getUserCredits(long userId) {
        return repository.findByUserId(userId).stream()
                .map(Credit::getBalance)
                .mapToLong(Long::longValue)
                .sum();
    }

    @Override
    public void event(long userId, int amount, CreditType type) {
        repository.save(new Credit(null, userId, amount, creditEventMapping(type)));
    }

    /**
     * IMPORTANT! Do not change existing values, only add new ones.
     * This will ensure that the database and the enums stay in sync even if the enums are renamed or reordered.
     *
     * @param type The enum to translate
     * @return short value representing enum in the database.
     */
    private short creditEventMapping(CreditType type) {
        return switch (type) {
            case CREDIT -> 1;
            case DEBIT -> 2;
            case REFUND -> 3;
            case OVERSPEND -> 4;
        };
    }
}
