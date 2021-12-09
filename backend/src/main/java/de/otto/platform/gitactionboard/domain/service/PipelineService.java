package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Workflow;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PipelineService {
  private final WorkflowService workflowService;
  private final JobDetailsService jobDetailsService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final List<String> repoNames;

  public PipelineService(
      WorkflowService workflowService,
      JobDetailsService jobDetailsService,
      @Qualifier("repoNames") List<String> repoNames) {
    this.workflowService = workflowService;
    this.jobDetailsService = jobDetailsService;
    this.repoNames = repoNames;
  }

  @Cacheable(cacheNames = "jobDetails", sync = true)
  public List<JobDetails> fetchJobs() {
    final List<Workflow> workflows =
        repoNames.stream()
            .parallel()
            .peek(repoName -> log.info("Fetching workflows for {} repo", repoName))
            .map(workflowService::fetchWorkflows)
            .map(CompletableFuture::join)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    return workflows.stream()
        .parallel()
        .peek(workflow -> log.info("Fetching job details for {}", workflow))
        .map(jobDetailsService::fetchJobDetails)
        .map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }
}
