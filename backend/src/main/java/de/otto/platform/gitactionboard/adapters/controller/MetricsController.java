package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.adapters.controller.Utils.createResponseEntityBodyBuilder;

import de.otto.platform.gitactionboard.domain.metrics.MetricsService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/metrics")
public class MetricsController {
  private final MetricsService metricsService;

  @GetMapping("/workflow-runs/{repoName}")
  public ResponseEntity<Map<String, List<WorkflowRunMetricDetails>>> getWorkflowRunMetrics(
      @PathVariable String repoName,
      @RequestParam(name = "from", required = false) Optional<Instant> from,
      @RequestParam(name = "to", required = false) Optional<Instant> to) {

    final Instant now = Instant.now();
    final Instant endTime = to.orElse(now);
    final Instant startTime = from.orElse(endTime.minus(7, ChronoUnit.DAYS));

    final Map<String, List<WorkflowRunMetricDetails>> responseBody =
        metricsService.getWorkflowRunMetrics(repoName, startTime, endTime).entrySet().stream()
            .map(
                entry ->
                    new AbstractMap.SimpleImmutableEntry<>(
                        entry.getKey(),
                        entry.getValue().stream().map(WorkflowRunMetricDetails::from).toList()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return createResponseEntityBodyBuilder().body(responseBody);
  }
}
