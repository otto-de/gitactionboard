package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.adapters.controller.Utils.createResponseEntityBodyBuilder;
import static de.otto.platform.gitactionboard.adapters.controller.Utils.decodeUrlEncodedText;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.CruiseControlService;
import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.Project;
import de.otto.platform.gitactionboard.domain.service.PipelineService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class GithubController {
  private final PipelineService pipelineService;
  private final CruiseControlService cruiseControlService;

  @Cacheable(cacheNames = "cctrayXml", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  @GetMapping(value = "/cctray.xml", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getCctrayXml(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {
    return createResponseEntityBodyBuilder()
        .body(cruiseControlService.convertToXml(fetchJobs(accessToken)));
  }

  @Cacheable(cacheNames = "cctray", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  @GetMapping(value = "/cctray", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Project>> getCctray(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {
    return createResponseEntityBodyBuilder()
        .body(cruiseControlService.convertToJson(fetchJobs(accessToken)));
  }

  private List<JobDetails> fetchJobs(String accessToken) {
    return pipelineService.fetchJobs(decodeUrlEncodedText(accessToken));
  }
}
