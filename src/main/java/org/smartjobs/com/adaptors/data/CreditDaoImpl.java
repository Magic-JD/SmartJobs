package org.smartjobs.com.adaptors.data;

import org.smartjobs.com.adaptors.data.repository.CreditRepository;
import org.smartjobs.com.adaptors.data.repository.data.Credit;
import org.smartjobs.com.core.dal.CreditDao;
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
    public int getUserCredits(String username) {
        return repository.findByUsername(username).stream()
                .map(Credit::getBalance)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
