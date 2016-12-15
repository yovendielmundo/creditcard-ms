package demo.rest.controller;

import demo.domain.CreditCard;
import demo.domain.CreditCardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreditCardControllerTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateACreditCardEmpty() {
        controller.createCreditCard(new CreditCard());
    }

    @Test
    public void shouldCreateCreditCard() {
        try {
            controller.createCreditCard(newTestCreditCard());
        } catch (IllegalStateException e) {
            verify(service, times(1)).saveNormalizedCreditCard(any(CreditCard.class));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateCreditCardsEmpty() {
        controller.createCreditCards(Collections.<CreditCard>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateCreditCardsWithAListOfEmptyCreditCards() {
        controller.createCreditCards(Arrays.asList(newEmptyCreditCard(), newEmptyCreditCard()));
    }

    @Test
    public void shouldImportCreditCards() {
        controller.createCreditCards(Collections.singletonList(newTestCreditCard()));

        verify(service, times(1)).importNormalizedCreditCards(anyListOf(CreditCard.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetCreditCardsWithBadDirection() {
        controller.getCreditCards(createPageRequest(0, 100, "decreasing", Collections.singleton("name")));
    }

    @Test
    public void shouldFindAllWithPageRequestWithOnlyTwoParams() {
        final Page<CreditCard> expectedPage = new PageImpl<>(Collections.emptyList());
        when(service.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        final Page<CreditCard> actualPage = controller.getCreditCards(createPageRequest(0, 100));

        verify(service, times(1)).findAll(any(PageRequest.class));
        assertThat(actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void shouldFindAllWithPageRequestWithAllParams() {
        final Page<CreditCard> expectedPage = new PageImpl<>(Collections.emptyList());
        when(service.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        final Page<CreditCard> actualPage = controller.getCreditCards(createPageRequest(0, 100, "desc", Collections.singleton("name")));

        verify(service, times(1)).findAll(any(PageRequest.class));
        assertThat(actualPage).isEqualTo(expectedPage);
    }

    private Pageable createPageRequest(int page, int size) {
        return new PageRequest(page, size);
    }

    private Pageable createPageRequest(int page, int size, String direction, Set<String> sort) {
        return new PageRequest(page, size, Sort.Direction.fromString(direction), sort.toArray(new String[sort.size()]));
    }

    private CreditCard newEmptyCreditCard(){
        return new CreditCard();
    }

    private CreditCard newTestCreditCard(){
        return new CreditCard("name", "Luis");
    }


    @Mock private CreditCardService service;

    @InjectMocks private CreditCardController controller;
}