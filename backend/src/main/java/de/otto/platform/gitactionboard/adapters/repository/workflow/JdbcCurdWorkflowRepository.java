package de.otto.platform.gitactionboard.adapters.repository.workflow;

import java.util.stream.Stream;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcCurdWorkflowRepository extends CrudRepository<WorkflowRecord, Long> {
  Stream<WorkflowRecord> findByRepoName(String repoName);
}
