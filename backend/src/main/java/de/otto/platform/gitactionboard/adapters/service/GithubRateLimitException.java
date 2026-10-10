package de.otto.platform.gitactionboard.adapters.service;

import java.time.Instant;
import lombok.Getter;

@Getter
public class GithubRateLimitException extends RuntimeException {
  private final Instant resetAt;

  public GithubRateLimitException(Instant resetAt) {
    super("GitHub rate limit hit; skipping call until reset at %s".formatted(resetAt));
    this.resetAt = resetAt;
  }
}
