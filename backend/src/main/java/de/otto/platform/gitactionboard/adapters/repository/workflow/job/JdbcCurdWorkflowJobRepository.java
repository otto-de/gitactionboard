package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import java.util.stream.Stream;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcCurdWorkflowJobRepository extends CrudRepository<WorkflowJobRecord, Long> {
  Stream<WorkflowJobRecord> findByWorkflowRunIdAndRunAttempt(
      Long workflowRunId, Integer runAttempt);
}
