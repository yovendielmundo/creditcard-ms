package demo.domain;

import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.util.Map;

public class CreditCard extends Document {

    public CreditCard() {}

    public CreditCard(final String key, final Object value) {
        putNormalized(key, value);
    }

    public CreditCard(final Map<String, Object> map) {
        map.entrySet().forEach(this::putNormalized);
    }

    public CreditCard(final CreditCard creditCard) {
        creditCard.entrySet().forEach(this::putNormalized);
    }

    public void putNormalized(final Entry<String, Object> entry) {
        putNormalized(entry.getKey(), entry.getValue());
    }

    public void putNormalized(final String key, final Object value) {
        Object newValue = value;
        if (value != null) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.isEmpty()) {
                    stringValue = stringValue.trim();
                    switch (stringValue){
                        case "1": newValue = true; break;
                        case "0": newValue = false; break;
                        default: newValue = stringValue;
                    }
                    this.putIfAbsent(key, newValue);
                }
            } else {
                this.putIfAbsent(key, newValue);
            }
        }
    }

    public CreditCard appendNormalized(final String key, final Object value) {
        putNormalized(key, value);
        return this;
    }

    public Boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public int compareTo(final CreditCard creditCard, final String key) {
        return this.getString(key).compareTo(creditCard.getString(key));
    }

    public String getStringId() {
        return this.get("_id").toString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Id
    private String id;
}