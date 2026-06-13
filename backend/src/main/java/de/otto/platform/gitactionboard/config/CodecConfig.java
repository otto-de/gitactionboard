package de.otto.platform.gitactionboard.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ext.javatime.ser.InstantSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class CodecConfig {

  public static final JsonMapper.Builder OBJECT_MAPPER_BUILDER =
      JsonMapper.builder()
          .addModule(
              new SimpleModule().addSerializer(Instant.class, new IsoWithMillisInstantSerializer()))
          .changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.NON_NULL))
          .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

  @Bean
  @Primary
  public JsonMapper objectMapper() {
    return OBJECT_MAPPER_BUILDER.build();
  }

  static class IsoWithMillisInstantSerializer extends InstantSerializer {
    @Serial private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter ISO_WITH_MILLIS =
        new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

    IsoWithMillisInstantSerializer() {
      super(InstantSerializer.INSTANCE, ISO_WITH_MILLIS, false, false, null);
    }
  }
}
