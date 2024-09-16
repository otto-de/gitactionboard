package de.otto.platform.gitactionboard.adapters.metrics;

import java.util.Set;
import java.util.stream.Stream;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcWorkflowRunMetricCurdRepository
    extends CrudRepository<WorkflowRunMetricRecord, Long> {

  @Query(
      """
      SELECT w.name          workflow_name,
             w.repo_name,
             runs.conclusion,
             runs.run_attempt,
             runs.created_at started_at,
             runs.updated_at completed_at,
             runs.triggered_event
      FROM workflow_runs runs
               JOIN workflows w ON w.id = runs.workflow_id
      WHERE runs.conclusion IS NOT NULL
        AND w.id in (:workflowIds)
        AND started_at BETWEEN :from AND :to
      ORDER BY workflow_name, started_at DESC
      """)
  Stream<WorkflowRunMetricRecord>
      findAllByWorkflowIdsAndConclusionIsNotNullAndStartedAtBetweenOrderByStartedAtDesc(
          Set<Long> workflowIds, String from, String to);
}
