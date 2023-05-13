package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.service.notifications.NotificationsService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PipelineService {
  private final WorkflowService workflowService;
  private final JobDetailsService jobDetailsService;
  private final NotificationsService notificationsService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final List<String> repoNames;

  public PipelineService(
      WorkflowService workflowService,
      JobDetailsService jobDetailsService,
      NotificationsService notificationsService,
      @Qualifier("repoNames") List<String> repoNames) {
    this.workflowService = workflowService;
    this.jobDetailsService = jobDetailsService;
    this.notificationsService = notificationsService;
    this.repoNames = repoNames;
  }

  @Cacheable(cacheNames = "jobDetails", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  public List<JobDetails> fetchJobs(String accessToken) {
    final List<Workflow> workflows = fetchWorkflows(accessToken);

    final List<JobDetails> jobDetails = fetchJobs(accessToken, workflows);

    notificationsService.sendNotificationsForWorkflowJobs(jobDetails);

    return jobDetails;
  }

  private List<JobDetails> fetchJobs(String accessToken, List<Workflow> workflows) {
    return workflows.stream()
        .parallel()
        .map(
            workflow -> {
              log.info("Fetching job details for {}", workflow);
              return jobDetailsService.fetchJobDetails(workflow, accessToken);
            })
        .map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .toList();
  }

  private List<Workflow> fetchWorkflows(String accessToken) {
    return repoNames.stream()
        .parallel()
        .map(
            repoName -> {
              log.info("Fetching workflows for {} repo", repoName);
              return workflowService.fetchWorkflows(repoName, accessToken);
            })
        .map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .toList();
  }
}
