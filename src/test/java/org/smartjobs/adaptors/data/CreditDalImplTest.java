package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.CreditRepository;
import org.smartjobs.adaptors.data.repository.data.Credit;
import org.smartjobs.core.constants.CreditType;
import org.smartjobs.core.ports.dal.CreditDal;

import java.util.List;

import static constants.TestConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CreditDalImplTest {

    public static final int CREDIT_AMOUNT = 100;
    public static final int DEBIT_AMOUNT = -10;
    public static final int REFUND_AMOUNT = 12;
    public static final int DEBIT_AMOUNT_2 = -12;
    public static final int DEBIT_AMOUNT_3 = -15;
    public static final int CREDIT_AMOUNT_2 = 50;
    public static final List<Credit> CREDIT_LIST = List.of(
            new Credit(1L, USER_ID, CREDIT_AMOUNT, (short) 1),
            new Credit(1L, USER_ID, DEBIT_AMOUNT, (short) 2),
            new Credit(1L, USER_ID, DEBIT_AMOUNT_2, (short) 2),
            new Credit(1L, USER_ID, REFUND_AMOUNT, (short) 3),
            new Credit(1L, USER_ID, DEBIT_AMOUNT_3, (short) 2),
            new Credit(1L, USER_ID, CREDIT_AMOUNT_2, (short) 1)
    );
    public static final long CREDIT_TOTAL = 125L;
    public static final int OVERSPEND_AMOUNT = 133;
    private final CreditRepository creditRepository = mock(CreditRepository.class);
    private final CreditDal creditDal = new CreditDalImpl(creditRepository);
    private final ArgumentCaptor<Credit> creditArgumentCaptor = ArgumentCaptor.forClass(Credit.class);

    @Test
    void testGetUserCreditWillSumTheCompleteAmountOfUserCredits() {
        when(creditRepository.findByUserId(USER_ID)).thenReturn(CREDIT_LIST);
        long userCredits = creditDal.getUserCredits(USER_ID);
        assertEquals(CREDIT_TOTAL, userCredits);
    }

    @Test
    void testSavingEventSavesTheCorrectValues(){
        creditDal.event(USER_ID, CREDIT_AMOUNT, CreditType.CREDIT);
        creditDal.event(USER_ID, DEBIT_AMOUNT, CreditType.DEBIT);
        creditDal.event(USER_ID, REFUND_AMOUNT, CreditType.REFUND);
        creditDal.event(USER_ID, OVERSPEND_AMOUNT, CreditType.OVERSPEND);
        verify(creditRepository, times(4)).save(creditArgumentCaptor.capture());
        List<Credit> allValues = creditArgumentCaptor.getAllValues();
        assertEquals(4, allValues.size());
        assertTrue(allValues.stream().allMatch(credit -> credit.getUserId() == USER_ID));
        Credit credit = allValues.get(0);
        assertEquals(CREDIT_AMOUNT, credit.getBalance());
        assertEquals(1, credit.getEvent());
        Credit debit = allValues.get(1);
        assertEquals(DEBIT_AMOUNT, debit.getBalance());
        assertEquals(2, debit.getEvent());
        Credit refund = allValues.get(2);
        assertEquals(REFUND_AMOUNT, refund.getBalance());
        assertEquals(3, refund.getEvent());
        Credit overspend = allValues.get(3);
        assertEquals(OVERSPEND_AMOUNT, overspend.getBalance());
        assertEquals(4, overspend.getEvent());
    }
}
