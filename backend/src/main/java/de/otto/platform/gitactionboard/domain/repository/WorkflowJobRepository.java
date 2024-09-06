package de.otto.platform.gitactionboard.domain.repository;

import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorkflowJobRepository {
  CompletableFuture<Void> save(List<WorkflowJob> workflowJobs);

  CompletableFuture<List<WorkflowJob>> findByWorkflowRunIdAndRunAttempt(
      Long workflowRunId, Integer runAttempt);
}
