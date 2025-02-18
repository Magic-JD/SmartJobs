package org.smartjobs.core.service.credit;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.CreditEvent;
import org.smartjobs.core.event.events.UserCreatedEvent;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.ports.dal.CreditDal;
import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.service.CreditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.smartjobs.core.constants.CreditType.*;


@Service
@Slf4j
public class CreditServiceImpl implements CreditService, Listener {

    private final CreditDal creditDal;
    private final EventEmitter eventEmitter;
    private final int signupBonus;

    public CreditServiceImpl(CreditDal creditDal, EventEmitter eventEmitter, @Value("${credit.signup.bonus}") int signupBonus) {
        this.creditDal = creditDal;
        this.eventEmitter = eventEmitter;
        this.signupBonus = signupBonus;
        eventEmitter.registerForEvents(this, UserCreatedEvent.class);
    }

    @Override
    public long userCredit(long userId) {
        return creditDal.getUserCredits(userId);
    }

    @Override
    public void debit(long userId, int amount) {
        creditDal.event(userId, amount * -1, DEBIT);
        long remainingCredits = userCredit(userId);
        if (remainingCredits >= 0) {
            log.info("User {} has spent {} credits.", userId, amount);
            eventEmitter.sendEvent(new CreditEvent(userId, remainingCredits, DEBIT));
        } else {
            log.info("User {} attempted to spend {} credits, but they didn't have sufficient credits.", userId, amount);
            creditDal.event(userId, amount, REFUND);
            throw new NotEnoughCreditException(userId);
        }
    }

    @Override
    public void refund(long userId, int amount) {
        log.error("User {} has had to be refunded {} credits.", userId, amount);
        creditDal.event(userId, amount, REFUND);
        long remainingCredits = userCredit(userId);
        eventEmitter.sendEvent(new CreditEvent(userId, remainingCredits, REFUND));
    }

    @Override
    public void credit(long userId, int amount) {
        log.error("User {} is credited with {} credits.", userId, amount);
        creditDal.event(userId, amount, CREDIT);
        long remainingCredits = userCredit(userId);
        eventEmitter.sendEvent(new CreditEvent(userId, remainingCredits, REFUND));
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case UserCreatedEvent(long id) -> credit(id, signupBonus);
            default -> throw new UnsupportedOperationException();
        }
    }
}
