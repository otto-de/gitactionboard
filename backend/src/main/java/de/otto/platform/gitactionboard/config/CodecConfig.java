package de.otto.platform.gitactionboard.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import java.io.Serial;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class CodecConfig {

  public static final Jackson2ObjectMapperBuilder OBJECT_MAPPER_BUILDER =
      new Jackson2ObjectMapperBuilder()
          .modules(
              new JavaTimeModule()
                  .addSerializer(Instant.class, new IsoWithMillisInstantSerializer()))
          .serializationInclusion(JsonInclude.Include.NON_NULL)
          .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return OBJECT_MAPPER_BUILDER.build();
  }

  static class IsoWithMillisInstantSerializer extends InstantSerializer {
    @Serial private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter ISO_WITH_MILLIS =
        new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

    IsoWithMillisInstantSerializer() {
      super(InstantSerializer.INSTANCE, false, false, ISO_WITH_MILLIS);
    }
  }
}
