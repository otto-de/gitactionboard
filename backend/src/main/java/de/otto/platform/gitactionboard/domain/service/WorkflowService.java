package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.Workflow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface WorkflowService {
  CompletableFuture<List<Workflow>> fetchWorkflows(String repoName, String accessToken);
}
