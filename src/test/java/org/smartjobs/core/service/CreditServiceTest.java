package org.smartjobs.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.ports.dal.CreditDal;
import org.smartjobs.core.service.credit.CreditServiceImpl;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.smartjobs.core.constants.CreditType.REFUND;

class CreditServiceTest {

    public static final int AMOUNT_TO_CHANGE = 50;
    private CreditService creditService;
    private CreditDal creditDal;

    @BeforeEach
    void setUp() {
        creditDal = creditDalMock();
        creditService = new CreditServiceImpl(creditDal, eventEmitter());
    }

    @Test
    void testUserCreditReturnsTheAmountOfUserCreditFromTheDatabase() {
        long currentUserCredit = creditService.userCredit(USER_ID);
        assertEquals(CREDIT_AMOUNT, currentUserCredit);
    }

    @Test
    void testDebitDoesNotThrowWhenThereIsSufficientCredit() {
        creditService.debit(USER_ID, AMOUNT_TO_CHANGE);
        assertDoesNotThrow(() -> new NotEnoughCreditException(USER_ID));
    }

    @Test
    void testRefundReturnsTheUsersCredit() {
        creditService.refund(USER_ID, AMOUNT_TO_CHANGE);
        verify(creditDal).event(USER_ID, AMOUNT_TO_CHANGE, REFUND);
    }
}