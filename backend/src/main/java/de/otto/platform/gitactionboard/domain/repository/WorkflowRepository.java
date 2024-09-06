package de.otto.platform.gitactionboard.domain.repository;

import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorkflowRepository {
  CompletableFuture<Void> save(List<Workflow> workflows);

  CompletableFuture<List<Workflow>> findAll();

  CompletableFuture<List<Workflow>> findByRepoName(String repoName);
}
