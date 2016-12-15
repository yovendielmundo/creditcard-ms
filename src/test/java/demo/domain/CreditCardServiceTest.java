package demo.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreditCardServiceTest {

    @Test
    public void shouldCreateACreditCardListWithOutEmptyValues() {

        final CreditCard badCreditCard1 = newEmptyCreditCard();
        badCreditCard1.append("empty1", "").append("empty2", "").append("name", "Luis");

        final CreditCard badCreditCard2 = newEmptyCreditCard();
        badCreditCard2.append("times", 2);

        final CreditCard badCreditCard3 = newEmptyCreditCard();
        badCreditCard3.append("null", null).append("empty", "");

        final CreditCard badCreditCard4 = newEmptyCreditCard();
        badCreditCard4.append("boolean", "0");

        final CreditCard badCreditCard5 = newEmptyCreditCard();
        badCreditCard5.append("true", "1");

        final List<CreditCard> badCreditCards = Arrays.asList(
                badCreditCard1, badCreditCard2, badCreditCard3, badCreditCard4, badCreditCard5);

        final List<CreditCard> normalizedCreditCards = service.createCreditCardsWithOutEmptyValues(badCreditCards);

        assertThat(normalizedCreditCards)
                .hasSize(4)
                .containsExactly(
                        new CreditCard("name", "Luis"),
                        new CreditCard("times", 2),
                        new CreditCard("boolean", false),
                        new CreditCard("true", true));
    }


    @Test
    public void shouldImportCreditCards() {
        final List<CreditCard> creditCards = Collections.singletonList(newTestCreditCard());

        service.importNormalizedCreditCards(creditCards);

        verify(repository, times(1)).deleteAll();
        verify(repository, times(1)).save(anyListOf(CreditCard.class));
    }

    @Test
    public void shouldSaveCreditCards() {
        final List<CreditCard> savedCreditCards = stubRepositoryToReturnCreditCardsOnSave();
        final List<CreditCard> creditCards = Collections.emptyList();

        final List<CreditCard> returnedCreditCards = service.saveNormalizedCreditCards(creditCards);

        verify(repository, times(1)).save(creditCards);
        assertEquals("Returned credit cards should come from the repository", savedCreditCards, returnedCreditCards);
    }

    @Test
    public void shouldSaveCreditCard() {
        final CreditCard savedCreditCard = stubRepositoryToReturnCreditCardOnSave();
        final CreditCard creditCard = newEmptyCreditCard();

        final CreditCard returnedUser = service.saveNormalizedCreditCard(creditCard);

        verify(repository, times(1)).save(creditCard);
        assertEquals("Returned credit card should come from the repository", savedCreditCard, returnedUser);
    }

    @Test(expected = CreditCardNotFoundException.class)
    public void shouldThrowCreditCardNotFoundException() {
        service.getCreditCardById("any");
    }

    @Test
    public void shouldGetCreditCardById() {
        final CreditCard savedCreditCard = stubRepositoryToReturnCreditCardOnFindOne();

        final CreditCard creditCard = service.getCreditCardById("any");

        verify(repository, times(1)).findOne(anyString());
        assertThat(creditCard).isEqualTo(savedCreditCard);
    }

    private CreditCard stubRepositoryToReturnCreditCardOnFindOne() {
        final CreditCard creditCard = newTestCreditCard();
        when(repository.findOne(anyString())).thenReturn(creditCard);
        return creditCard;
    }

    private CreditCard stubRepositoryToReturnCreditCardOnSave() {
        final CreditCard creditCard = newTestCreditCard();
        when(repository.save(any(CreditCard.class))).thenReturn(creditCard);
        return creditCard;
    }

    private List<CreditCard> stubRepositoryToReturnCreditCardsOnSave() {
        final List<CreditCard> creditCards = Collections.singletonList(newTestCreditCard());
        when(repository.save(anyListOf(CreditCard.class))).thenReturn(creditCards);
        return creditCards;
    }


    private CreditCard newEmptyCreditCard(){
        return new CreditCard();
    }

    private CreditCard newTestCreditCard(){
        return new CreditCard("name", "Luis");
    }

    @Mock
    private CreditCardRepository repository;

    @InjectMocks
    private CreditCardService service;
}