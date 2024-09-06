package de.otto.platform.gitactionboard.domain.repository;

import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorkflowRunRepository {
  CompletableFuture<Void> save(List<WorkflowRun> workflowRuns);

  CompletableFuture<List<WorkflowRun>> findAll();
}
