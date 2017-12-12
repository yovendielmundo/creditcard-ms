package demo;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public class WebappConfig extends WebMvcConfigurerAdapter {
        @Override
        public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.serializerByType(ObjectId.class, new ToStringSerializer());
            converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        }
    }
}
