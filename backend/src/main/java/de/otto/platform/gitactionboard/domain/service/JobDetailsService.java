package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Workflow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface JobDetailsService {
  CompletableFuture<List<JobDetails>> fetchJobDetails(Workflow workflow);
}
