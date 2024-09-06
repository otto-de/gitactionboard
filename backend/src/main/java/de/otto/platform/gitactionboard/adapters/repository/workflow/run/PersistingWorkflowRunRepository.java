package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import de.otto.platform.gitactionboard.domain.repository.WorkflowRunRepository;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersistingWorkflowRunRepository implements WorkflowRunRepository {
  private static final int BATCH_SIZE = 100;
  private final JdbcCurdWorkflowRunRepository jdbcCurdWorkflowRunRepository;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public CompletableFuture<Void> save(List<WorkflowRun> workflowRuns) {
    log.debug("Saving workflow runs into database {}", workflowRuns);
    if (workflowRuns.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.runAsync(
        () ->
            jdbcTemplate.batchUpdate(
                "insert or replace into workflow_runs (id, run_attempt, workflow_id, status, conclusion, run_number, updated_at, created_at, triggered_event) values (?, ?, ?,?, ?, ?,?, ?, ?)",
                workflowRuns,
                BATCH_SIZE,
                (ps, workflowRun) -> {
                  ps.setLong(1, workflowRun.getId());
                  ps.setLong(2, workflowRun.getRunAttempt());
                  ps.setLong(3, workflowRun.getWorkflowId());
                  ps.setString(4, workflowRun.getStatus().name());
                  ps.setString(
                      5,
                      Optional.ofNullable(workflowRun.getConclusion())
                          .map(Enum::name)
                          .orElse(null));
                  ps.setLong(6, workflowRun.getRunNumber());
                  ps.setString(7, workflowRun.getUpdatedAt().toString());
                  ps.setString(8, workflowRun.getCreatedAt().toString());
                  ps.setString(9, workflowRun.getTriggeredEvent());
                }));
  }

  @Override
  public CompletableFuture<List<WorkflowRun>> findAll() {
    return CompletableFuture.supplyAsync(
        () ->
            StreamSupport.stream(jdbcCurdWorkflowRunRepository.findAll().spliterator(), false)
                .map(WorkflowRunRecord::toWorkflowRun)
                .toList());
  }
}
