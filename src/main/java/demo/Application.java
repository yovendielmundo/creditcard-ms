package demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter jacksonHttpMessageConverter() {
        final List<MediaType> mediaTypes = Arrays.asList(
                MediaType.valueOf("application/schema+json"),
                MediaType.valueOf("application/x-spring-data-verbose+json"),
                MediaType.valueOf("application/x-spring-data-compact+json"),
                MediaType.APPLICATION_JSON);

        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(this.getObjectMapper());
        converter.setSupportedMediaTypes(mediaTypes);

        return converter;
    }

//    @Bean public LoggingEventListener mongoEventListener() {
//        return new LoggingEventListener();
//    }

    @Bean
    @Primary
    public ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        final SimpleModule module = new SimpleModule("ObjectIdModule");
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());

        objectMapper.registerModule(module);

        return objectMapper;
    }

    public class ObjectIdSerializer extends JsonSerializer<ObjectId> {
        @Override
        public void serialize(final ObjectId value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }
}
