package demo.domain;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CreditCardTest extends TestCase {

    @Before
    public void setup(){
        creditCard = new CreditCard();
    }

    @Test
    public void shouldBeAnEmptyCreditCardWhenValueIsNull() {
        creditCard.putNormalized("key", null);

        assertThat(creditCard).isEmpty();
        assertThat(new CreditCard("key", null)).isEmpty();
    }

    @Test
    public void shouldNotPutAStringValueWhenIsEmpty() {
        String key = "someKey";
        String stringValue = "";

        creditCard.putNormalized(key, stringValue);

        assertThat(creditCard).isEmpty();
        assertThat(new CreditCard("someKey", "")).isEmpty();
    }

    @Test
    public void shouldPutAStringValue() {
        String key = "someKey";
        String stringValue = "stringValue";

        creditCard.putNormalized(key, stringValue);

        assertThat(creditCard).containsKey(key).containsValue(stringValue);
    }

    @Test
    public void shouldHasNonDuplicateKeys() {
        String key = "someKey";
        String duplicatedKey = "someKey";
        String stringValue = "stringValue";

        creditCard.putNormalized(key, stringValue);
        creditCard.putNormalized(duplicatedKey, stringValue);

        assertThat(creditCard).hasSize(1).containsKey(key).containsValue(stringValue);
    }

    @Test
    public void shouldPutNonStringValues() {
        String stringValue = "string";
        Integer integerValue = 1;
        Double doubleValue = 1.0;
        Boolean booleanValue = true;
        String[] listValue = new String[]{"one"};

        creditCard.putNormalized("stringValue", stringValue);
        creditCard.putNormalized("integerValue", integerValue);
        creditCard.putNormalized("doubleValue", doubleValue);
        creditCard.putNormalized("booleanValue", booleanValue);
        creditCard.putNormalized("listValue", listValue);

        assertThat(creditCard).hasSize(5);
        assertThat(creditCard.get("stringValue")).isInstanceOf(String.class);
        assertThat(creditCard.get("integerValue")).isInstanceOf(Integer.class);
        assertThat(creditCard.get("doubleValue")).isInstanceOf(Double.class);
        assertThat(creditCard.get("booleanValue")).isInstanceOf(Boolean.class);
        assertThat(creditCard.get("listValue")).isInstanceOf(String[].class);
    }


    @Test
    public void shouldAppendNonStringValues() {
        String stringValue = "string";
        Integer integerValue = 1;
        Double doubleValue = 1.0;
        Boolean booleanValue = true;
        String[] listValue = new String[]{"one"};

        creditCard.appendNormalized("stringValue", stringValue);
        creditCard.appendNormalized("integerValue", integerValue);
        creditCard.appendNormalized("doubleValue", doubleValue);
        creditCard.appendNormalized("booleanValue", booleanValue);
        creditCard.appendNormalized("listValue", listValue);

        assertThat(creditCard).hasSize(5);
        assertThat(creditCard.get("stringValue")).isInstanceOf(String.class);
        assertThat(creditCard.get("integerValue")).isInstanceOf(Integer.class);
        assertThat(creditCard.get("doubleValue")).isInstanceOf(Double.class);
        assertThat(creditCard.get("booleanValue")).isInstanceOf(Boolean.class);
        assertThat(creditCard.get("listValue")).isInstanceOf(String[].class);
    }

    @Test
    public void shouldPutTrimStringValues() {
        creditCard.putNormalized("key", "stringValue    ");

        assertThat(creditCard).containsKey("key").containsValue("stringValue");
    }

    @Test
    public void shouldPutBooleanValueForStringWithNumberValue() {
        creditCard.putNormalized("keyTrue", "1");
        creditCard.putNormalized("keyFalse", "0");

        assertThat(creditCard.get("keyTrue")).isInstanceOf(Boolean.class).isEqualTo(true);
        assertThat(creditCard.get("keyFalse")).isInstanceOf(Boolean.class).isEqualTo(false);
    }

    @Test
    public void shouldCreateNormalizedCreditCardFromAnotherOne() {
        CreditCard badCreditCard = new CreditCard();
        badCreditCard.append("name", "luis")
                .append("empty", "")
                .append("null", null)
                .append("bool", true);

        CreditCard normalizedCreditCard = new CreditCard(badCreditCard);

        assertThat(badCreditCard).hasSize(4);
        assertThat(normalizedCreditCard)
                .hasSize(2)
                .containsKey("name")
                .containsValue("luis")
                .containsKey("bool")
                .containsValue(true);
    }

    private CreditCard creditCard;
}