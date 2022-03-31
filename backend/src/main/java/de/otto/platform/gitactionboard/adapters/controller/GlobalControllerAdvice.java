package de.otto.platform.gitactionboard.adapters.controller;

import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
@Order(10000)
public class GlobalControllerAdvice {

  @InitBinder
  public void setAllowedFields(WebDataBinder dataBinder) {

    final String[] disallowedFields =
        List.of("class.*", "Class.*", "*.class.*", "*.Class.*").toArray(String[]::new);

    dataBinder.setDisallowedFields(disallowedFields);
  }
}
