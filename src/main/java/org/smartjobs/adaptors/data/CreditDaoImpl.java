package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.CreditRepository;
import org.smartjobs.adaptors.data.repository.data.Credit;
import org.smartjobs.core.ports.dal.CreditDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditDaoImpl implements CreditDao {

    private final CreditRepository repository;

    @Autowired
    public CreditDaoImpl(CreditRepository repository) {
        this.repository = repository;
    }

    @Override
    public int getUserCredits(long userId) {
        return repository.findByUserId(userId).stream()
                .map(Credit::getBalance)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public void event(long userId, int amount, String type) {
        repository.save(new Credit(null, userId, amount, type));
    }
}
