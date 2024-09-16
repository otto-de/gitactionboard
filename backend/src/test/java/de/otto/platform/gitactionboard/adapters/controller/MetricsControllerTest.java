package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.BUILD_AND_DEPLOYMENT_WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.metrics.MetricsService;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.data.TemporalUnitOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@Sequential
@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {
  @Mock private MetricsService metricsService;

  @Mock private WorkflowRunMetric workflowRunMetric1;
  @Mock private WorkflowRunMetric workflowRunMetric2;
  @Mock private WorkflowRunMetric workflowRunMetric3;

  @Mock private WorkflowRunMetricDetails workflowRunMetricDetails1;
  @Mock private WorkflowRunMetricDetails workflowRunMetricDetails2;
  @Mock private WorkflowRunMetricDetails workflowRunMetricDetails3;

  @Captor private ArgumentCaptor<Instant> fromArgumentCaptor;

  @Captor private ArgumentCaptor<Instant> toArgumentCaptor;

  private MetricsController metricsController;

  @BeforeEach
  void setUp() {
    metricsController = new MetricsController(metricsService);
  }

  @Test
  void shouldReturnWorkflowRunMetricsForGivenTimeRange() {
    final Instant from = Instant.now().minus(1, DAYS);
    final Instant to = Instant.now();

    final String workflowName2 = "%s_2".formatted(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);

    when(metricsService.getWorkflowRunMetrics(REPO_NAME, from, to))
        .thenReturn(
            Map.of(
                BUILD_AND_DEPLOYMENT_WORKFLOW_NAME,
                List.of(workflowRunMetric1, workflowRunMetric3),
                workflowName2,
                List.of(workflowRunMetric2)));

    try (MockedStatic<WorkflowRunMetricDetails> workflowRunMetricDetailsMockedStatic =
        mockStatic(WorkflowRunMetricDetails.class)) {
      workflowRunMetricDetailsMockedStatic
          .when(() -> WorkflowRunMetricDetails.from(any(WorkflowRunMetric.class)))
          .thenAnswer(
              invocation -> {
                final WorkflowRunMetric argument =
                    invocation.getArgument(0, WorkflowRunMetric.class);
                if (argument == workflowRunMetric1) return workflowRunMetricDetails1;
                if (argument == workflowRunMetric2) return workflowRunMetricDetails2;
                return workflowRunMetricDetails3;
              });
      assertThat(
              metricsController
                  .getWorkflowRunMetrics(
                      REPO_NAME, Optional.ofNullable(from), Optional.ofNullable(to))
                  .getBody())
          .hasSize(2)
          .containsEntry(
              BUILD_AND_DEPLOYMENT_WORKFLOW_NAME,
              List.of(workflowRunMetricDetails1, workflowRunMetricDetails3))
          .containsEntry(workflowName2, List.of(workflowRunMetricDetails2));
    }

    verify(metricsService, times(1)).getWorkflowRunMetrics(REPO_NAME, from, to);
  }

  @Test
  void shouldReturnEmptyIfMetricsIsNotAvailableForGivenTimeRange() {
    final Instant now = Instant.now();

    assertThat(
            metricsController
                .getWorkflowRunMetrics(
                    REPO_NAME, Optional.ofNullable(now), Optional.ofNullable(now))
                .getBody())
        .isEmpty();

    verify(metricsService, times(1)).getWorkflowRunMetrics(REPO_NAME, now, now);
  }

  @ParameterizedTest(name = "{index} ==> from = {0}, to = {1}")
  @MethodSource("getParameterForTimeRange")
  void shouldUseDefaultValueForFromAndToRequestParam(
      Instant from,
      Instant to,
      InstantValidator fromArgumentValidator,
      InstantValidator toArgumentValidator) {
    assertThat(
            metricsController
                .getWorkflowRunMetrics(
                    REPO_NAME, Optional.ofNullable(from), Optional.ofNullable(to))
                .getBody())
        .isEmpty();

    verify(metricsService, times(1))
        .getWorkflowRunMetrics(
            eq(REPO_NAME), fromArgumentCaptor.capture(), toArgumentCaptor.capture());
    fromArgumentValidator.validate(fromArgumentCaptor.getValue());
    toArgumentValidator.validate(toArgumentCaptor.getValue());
  }

  private static Stream<Arguments> getParameterForTimeRange() {
    final TemporalUnitOffset within1Min = within(1, ChronoUnit.MINUTES);
    final Instant now = Instant.now();
    final Instant yesterday = now.minus(1, DAYS);
    final Instant lastMonth = now.minus(30, DAYS);

    return Stream.of(
        Arguments.of(
            null,
            null,
            (InstantValidator)
                actualInstant ->
                    assertThat(actualInstant).isCloseTo(now.minus(7, DAYS), within1Min),
            (InstantValidator)
                actualInstant -> assertThat(actualInstant).isCloseTo(now, within1Min)),
        Arguments.of(
            null,
            now,
            (InstantValidator)
                actualInstant -> assertThat(actualInstant).isEqualTo(now.minus(7, DAYS)),
            (InstantValidator) actualInstant -> assertThat(actualInstant).isEqualTo(now)),
        Arguments.of(
            null,
            yesterday,
            (InstantValidator)
                actualInstant -> assertThat(actualInstant).isEqualTo(yesterday.minus(7, DAYS)),
            (InstantValidator) actualInstant -> assertThat(actualInstant).isEqualTo(yesterday)),
        Arguments.of(
            lastMonth,
            yesterday,
            (InstantValidator) actualInstant -> assertThat(actualInstant).isEqualTo(lastMonth),
            (InstantValidator) actualInstant -> assertThat(actualInstant).isEqualTo(yesterday)),
        Arguments.of(
            lastMonth,
            null,
            (InstantValidator) actualInstant -> assertThat(actualInstant).isEqualTo(lastMonth),
            (InstantValidator)
                actualInstant -> assertThat(actualInstant).isCloseTo(now, within1Min)));
  }

  @FunctionalInterface
  public interface InstantValidator {
    void validate(Instant actualInstant);
  }
}
