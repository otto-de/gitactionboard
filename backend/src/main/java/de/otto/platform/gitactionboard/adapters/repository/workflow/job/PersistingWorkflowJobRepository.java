package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import de.otto.platform.gitactionboard.domain.repository.WorkflowJobRepository;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersistingWorkflowJobRepository implements WorkflowJobRepository {
  private static final int BATCH_SIZE = 100;
  private final JdbcTemplate jdbcTemplate;
  private final JdbcCurdWorkflowJobRepository jdbcCurdWorkflowJobRepository;

  @Override
  @Transactional
  public CompletableFuture<Void> save(List<WorkflowJob> workflowJobs) {
    log.debug("Saving workflow jobs into database {}", workflowJobs);

    if (workflowJobs.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.runAsync(
        () ->
            jdbcTemplate.batchUpdate(
                "insert or replace into workflow_jobs (id, name, status, conclusion, started_at, completed_at, workflow_id,workflow_run_id, run_attempt, url) values (?, ?, ?,?, ?, ?,?, ?, ?, ?)",
                workflowJobs,
                BATCH_SIZE,
                (ps, workflowJob) -> {
                  ps.setLong(1, workflowJob.getId());
                  ps.setString(2, workflowJob.getName());
                  ps.setString(3, workflowJob.getStatus().name());
                  ps.setString(
                      4,
                      Optional.ofNullable(workflowJob.getConclusion())
                          .map(Enum::name)
                          .orElse(null));
                  ps.setString(5, workflowJob.getStartedAt().toString());
                  ps.setString(
                      6,
                      Optional.ofNullable(workflowJob.getCompletedAt())
                          .map(Instant::toString)
                          .orElse(null));
                  ps.setLong(7, workflowJob.getWorkflowId());
                  ps.setLong(8, workflowJob.getWorkflowRunId());
                  ps.setLong(9, workflowJob.getRunAttempt());
                  ps.setString(10, workflowJob.getUrl());
                }));
  }

  @Override
  public CompletableFuture<List<WorkflowJob>> findByWorkflowRunIdAndRunAttempt(
      Long workflowRunId, Integer runAttempt) {
    return CompletableFuture.supplyAsync(
        () ->
            StreamSupport.stream(
                    jdbcCurdWorkflowJobRepository
                        .findByWorkflowRunIdAndRunAttempt(workflowRunId, runAttempt)
                        .spliterator(),
                    false)
                .map(WorkflowJobRecord::toWorkflowJob)
                .toList());
  }
}
