package de.otto.platform.gitactionboard.adapters.repository.workflow;

import de.otto.platform.gitactionboard.domain.repository.WorkflowRepository;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersistingWorkflowRepository implements WorkflowRepository {
  private static final int BATCH_SIZE = 100;
  private final JdbcCurdWorkflowRepository jdbcCurdWorkflowRepository;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public CompletableFuture<Void> save(List<Workflow> workflows) {
    log.debug("Saving workflows into database {}", workflows);
    if (workflows.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }
    return CompletableFuture.runAsync(
        () ->
            jdbcTemplate.batchUpdate(
                "insert or replace into workflows (id, name, repo_name) values (?, ?, ?)",
                workflows,
                BATCH_SIZE,
                (ps, workflow) -> {
                  ps.setLong(1, workflow.getId());
                  ps.setString(2, workflow.getName());
                  ps.setString(3, workflow.getRepoName());
                }));
  }

  @Override
  public CompletableFuture<List<Workflow>> findAll() {
    return CompletableFuture.supplyAsync(
        () ->
            StreamSupport.stream(jdbcCurdWorkflowRepository.findAll().spliterator(), false)
                .map(WorkflowRecord::toWorkflow)
                .toList());
  }

  @Override
  public CompletableFuture<List<Workflow>> findByRepoName(String repoName) {
    return CompletableFuture.supplyAsync(
        () ->
            jdbcCurdWorkflowRepository
                .findByRepoName(repoName)
                .map(WorkflowRecord::toWorkflow)
                .toList());
  }
}
