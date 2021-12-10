package de.otto.platform.gitactionboard.adapters.service.workflow;

import de.otto.platform.gitactionboard.adapters.service.ApiService;
import de.otto.platform.gitactionboard.adapters.service.workflow.WorkflowsResponse.WorkflowIdentifier;
import de.otto.platform.gitactionboard.domain.Workflow;
import de.otto.platform.gitactionboard.domain.service.WorkflowService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubWorkflowService implements WorkflowService {
  private final ApiService apiService;

  @Override
  @Async
  public CompletableFuture<List<Workflow>> fetchWorkflows(String repoName, String accessToken) {
    return CompletableFuture.supplyAsync(
            () -> {
              log.info("Fetching workflows for {} repository", repoName);

              return getWorkflowIdentifiers(repoName, accessToken).stream()
                  .map(workflowIdentifier -> buildWorkflow(repoName, workflowIdentifier))
                  .collect(Collectors.toUnmodifiableList());
            })
        .exceptionally(
            throwable -> {
              log.error(throwable.getMessage(), throwable);
              return Collections.emptyList();
            });
  }

  private List<WorkflowIdentifier> getWorkflowIdentifiers(String repoName, String accessToken) {
    final String url = String.format("/%s/actions/workflows", repoName);

    return Optional.ofNullable(apiService.getForObject(url, accessToken, WorkflowsResponse.class))
        .map(WorkflowsResponse::getWorkflows)
        .orElse(Collections.emptyList())
        .stream()
        .filter(WorkflowIdentifier::isActive)
        .collect(Collectors.toUnmodifiableList());
  }

  private Workflow buildWorkflow(String repoName, WorkflowIdentifier workflowIdentifier) {
    return Workflow.builder()
        .name(workflowIdentifier.getName())
        .id(workflowIdentifier.getId())
        .repoName(repoName)
        .build();
  }
}
