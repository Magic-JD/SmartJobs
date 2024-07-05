package org.smartjobs.core.service;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.ports.dal.CreditDal;
import org.smartjobs.core.service.credit.CreditServiceImpl;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.smartjobs.core.constants.CreditType.REFUND;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CreditServiceTest {

    public static final int AMOUNT_TO_CHANGE = 50;

    @Test
    void testUserCreditReturnsTheAmountOfUserCreditFromTheDatabase() {
        long currentUserCredit = CREDIT_SERVICE.userCredit(USER_ID);
        assertEquals(CREDIT_AMOUNT, currentUserCredit);
    }

    @Test
    void testDebitDoesNotThrowWhenThereIsSufficientCredit() {
        CREDIT_SERVICE.debit(USER_ID, AMOUNT_TO_CHANGE);
        assertDoesNotThrow(() -> new NotEnoughCreditException(USER_ID));
    }

    @Test
    void testRefundReturnsTheUsersCredit() {
        CreditDal creditDal = creditDalMock();
        CreditService creditService = new CreditServiceImpl(creditDal, EVENT_EMITTER, SIGNUP_BONUS);
        creditService.refund(USER_ID, AMOUNT_TO_CHANGE);
        verify(creditDal).event(USER_ID, AMOUNT_TO_CHANGE, REFUND);
    }
}
