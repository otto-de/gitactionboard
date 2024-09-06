package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcCurdWorkflowRunRepository extends CrudRepository<WorkflowRunRecord, Long> {}
