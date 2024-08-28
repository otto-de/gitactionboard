package de.otto.platform.gitactionboard.domain.workflow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES")
public enum JobStatus {
  EXCEPTION,
  SUCCESS,
  FAILURE,
  UNKNOWN
}
