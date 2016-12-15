package integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.Application;
import demo.domain.CreditCard;
import demo.domain.CreditCardRepository;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTest {

    @Before public void setup() {
        creditCardRepository.deleteAll();
    }

    @After public void down(){
        creditCardRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        Document savedDocument = creditCardRepository.save(new CreditCard("name", "LuisMiguel"));

        assertThat(savedDocument).isNotNull().containsKey("name").containsValue("LuisMiguel");
    }

    @Test
    public void shouldCreateCreditCardsFromJson() {
        List<CreditCard> entities = loadEntityListFromFile("credit-cards.json");

        creditCardRepository.save(entities);

        assertThat(creditCardRepository.count()).isEqualTo(5);
    }

    private List<CreditCard> loadEntityListFromFile(String file) {
        URL url = getClass().getClassLoader().getResource(file);
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
        return objectMapper;
    }

    @Inject
    private CreditCardRepository creditCardRepository;

    private ObjectMapper objectMapper = objectMapper();

}