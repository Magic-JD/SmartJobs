package org.smartjobs.com.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.dal.repository.CreditRepository;
import org.smartjobs.com.dal.repository.data.Credit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditDao {

    private static final Logger logger = LoggerFactory.getLogger(CreditDao.class);

    private final CreditRepository repository;

    @Autowired
    public CreditDao(CreditRepository repository) {
        this.repository = repository;
    }

    public int getUserCredits(String username) {
        return repository.findByUsername(username).stream()
                .map(Credit::getBalance)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
