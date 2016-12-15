package integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.Application;
import demo.domain.CreditCard;
import demo.domain.CreditCardRepository;
import demo.domain.CreditCardService;
import integration.support.Page;
import integration.support.PageAssertion;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=9000")
public class CreditCardIntegrationTest {

    @After public void down() {
        service.deleteAll();
    }

    @Test
    public void shouldReturnBadRequestWhenCreateEmptyCreditCard() {
        final ResponseEntity response = postCreditCard(CREDIT_CARDS_URI, newEmptyCreditCard());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void shouldReturnOkWhenCreateCreditCard() {
        final URI response = restTemplate.postForLocation(CREDIT_CARDS_URI, newTestCreditCard());

        assertThat(response.getPath()).isNotEmpty();
    }


    @Test
    public void shouldReturnBadRequestWhenImportEmptyCreditCards() {
        final ResponseEntity response = postCreditCards(CREDIT_CARDS_IMPORT_URI, Collections.emptyList());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> message = (Map<String, String>) response.getBody();
        assertThat(message).containsKey("message");
    }

    @Test
    public void shouldReturnBadRequestWhenImportCreditCardsWithEmptyCreditCards() {
        final ResponseEntity response = postCreditCards(CREDIT_CARDS_IMPORT_URI, Arrays.asList(newEmptyCreditCard(), newEmptyCreditCard()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> message = (Map<String, String>) response.getBody();
        assertThat(message).containsKey("message");
    }

    @Test
    public void shouldReturnOkAndIdsListWhenImportCreditCards() {
        final ResponseEntity response = postCreditCards(CREDIT_CARDS_IMPORT_URI, Collections.singletonList(newTestCreditCard()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isNotNull().asList().hasSize(1);
    }

    @Test
    public void shouldReturnOkWithAllCreditCards() {
        createCreditCardsFromJson();

        final ResponseEntity<Page<CreditCard>> response = getPageCreditCards(CREDIT_CARDS_URI);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final Page<CreditCard> creditCardPage = response.getBody();
        PageAssertion.assertThat(creditCardPage)
                .hasTotalElements(5)
                .hasTotalPages(1)
                .hasPageSize(20)
                .hasPageNumber(0)
                .hasContentSize(5);
    }

    @Test
    public void shouldReturnOkWithOneCreditCard() {
        createCreditCardsFromJson();

        final ResponseEntity<Page<CreditCard>> response = getPageCreditCards(CREDIT_CARDS_URI + "?page={page}&size={size}", 0, 1);

        final Page<CreditCard> creditCardPage = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageAssertion.assertThat(creditCardPage)
                .hasTotalElements(5)
                .hasTotalPages(5)
                .hasPageSize(1)
                .hasPageNumber(0)
                .hasContentSize(1);
    }

    @Test
    public void shouldReturnOkWithAllCreditCardsSortedByKeyAsc() {
        createCreditCardsFromJson();

        final String keyToSort = "productName";
        final ResponseEntity<Page<CreditCard>> response = getPageCreditCards(CREDIT_CARDS_URI + "?sort={sort}", keyToSort);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final List<CreditCard> creditCards = response.getBody().getContent();
        assertThat(creditCards).isSortedAccordingTo(creditCardComparator(keyToSort));
    }


    @Test
    public void shouldReturnOkWithAllCreditCardsSortedByKeyDescending() {
        createCreditCardsFromJson();

        final String keyToSort = "productName";
        final ResponseEntity<Page<CreditCard>> response = getPageCreditCards(CREDIT_CARDS_URI + "?sort={sort},desc", keyToSort);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final List<CreditCard> creditCards = response.getBody().getContent();
        assertThat(creditCards).isSortedAccordingTo(creditCardComparator(keyToSort).reversed());
    }

    @Test
    public void shouldReturnOkWithAllCreditCardsFilterByLocale() {
        createCreditCardsFromJson();

        final String locale = "ID";
        final ResponseEntity<List<Resource<CreditCard>>> response = getListCreditCards("http://localhost:9000/hateoas/search?locale={locale}", locale);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final List<Resource<CreditCard>> creditCards = response.getBody();

        assertThat(creditCards)
                .filteredOn(creditCard -> !creditCard.getContent().getString("language").equals(locale))
                .hasSize(0);
    }

    @Test
    public void shouldReturnNotFoundCreditCard() {
        final String id = "fakeId";
        ResponseEntity response = restTemplate.getForEntity(CREDIT_CARDS_URI + "/{id}", Object.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, String> message = (Map<String, String>) response.getBody();
        assertThat(message).containsKey("message");

    }

    @Test
    public void shouldReturnOkWithACreditCard() {
        createCreditCardsFromJson();
        final String id = repository.findAll().get(0).getStringId();
        final ResponseEntity<CreditCard> response = restTemplate.getForEntity(CREDIT_CARDS_URI + "/{id}", CreditCard.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final CreditCard creditCard = response.getBody();
        assertThat(creditCard).isNotNull();
        assertThat(creditCard.getStringId()).isNotNull();
    }

    private Comparator<CreditCard> creditCardComparator(String key) {
        return (o1, o2) -> o1.getString(key).compareTo(o2.getString(key));
    }

    private ResponseEntity postCreditCard(final String uri, final CreditCard creditCard) {
        final HttpEntity<CreditCard> entity = new HttpEntity<>(creditCard, getHttpHeaders());
        return postCreditCards(uri, entity);
    }

    private ResponseEntity postCreditCards(final String uri, final List<CreditCard> creditCards) {
        final HttpEntity<List<CreditCard>> entity = new HttpEntity<>(creditCards, getHttpHeaders());
        return postCreditCards(uri, entity);
    }

    private ResponseEntity postCreditCards(final String uri, final HttpEntity entity) {
        return restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);
    }

    private ResponseEntity<Page<CreditCard>> getPageCreditCards(String uri, Object... params) {
        return restTemplate.exchange(uri, HttpMethod.GET, null, getPageTypeReference(), params);
    }

    private ResponseEntity<List<Resource<CreditCard>>> getListCreditCards(String uri, Object... params) {
        return restTemplate.exchange(uri, HttpMethod.GET, null, getListTypeReference(), params);
    }

    private HttpHeaders getHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ParameterizedTypeReference<List<Resource<CreditCard>>> getListTypeReference() {
        return new ParameterizedTypeReference<List<Resource<CreditCard>>>() {};
    }

    private ParameterizedTypeReference<Page<CreditCard>> getPageTypeReference() {
        return new ParameterizedTypeReference<Page<CreditCard>>() {};
    }

    private void createCreditCardsFromJson() {
        List<CreditCard> entities = loadCreditCardsFromFile("credit-cards.json");
        service.saveNormalizedCreditCards(entities);
    }

    private CreditCard newTestCreditCard(){
        return new CreditCard("name", "Luis");
    }

    private CreditCard newEmptyCreditCard(){
        return new CreditCard();
    }

    private List<CreditCard> loadCreditCardsFromFile(String file) {
        final URL url = getClass().getClassLoader().getResource(file);
        List<CreditCard> entities = null;
        byte[] encoded;
        try {
            assert url != null;
            encoded = Files.readAllBytes(Paths.get(url.toURI()));
            String document = new String(encoded, Charset.defaultCharset());
            entities = objectMapper.readValue(document, new TypeReference<List<CreditCard>>() {});
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return entities;
    }

    private ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
        return objectMapper;
    }

    private final RestTemplate restTemplate = new TestRestTemplate("demo", "123");

    private final ObjectMapper objectMapper = objectMapper();

    private final String CREDIT_CARDS_URI = "http://localhost:9000/credit-cards";

    private final String CREDIT_CARDS_IMPORT_URI = CREDIT_CARDS_URI + "/import";

    @Autowired
    private CreditCardService service;

    @Autowired
    private CreditCardRepository repository;
}