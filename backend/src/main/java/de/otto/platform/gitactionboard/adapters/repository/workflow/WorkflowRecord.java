package de.otto.platform.gitactionboard.adapters.repository.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Value
@Table(name = "workflows")
public class WorkflowRecord {
  String name;

  @JsonProperty("repo_name")
  String repoName;

  @Id Long id;

  public static WorkflowRecord from(Workflow workflow) {
    return WorkflowRecord.builder()
        .name(workflow.getName())
        .id(workflow.getId())
        .repoName(workflow.getRepoName())
        .build();
  }

  public Workflow toWorkflow() {
    return Workflow.builder().name(name).repoName(repoName).id(id).build();
  }
}
