#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" ; pwd -P)"

# Source glob libs
  # shellcheck source=bin/lib.sh
. "${SCRIPT_DIR}/bin/lib.sh"

_usage() {
    cat <<EOF
Usage: $0 command

commands:
  check                                                Run checks (OWASP dependency check)
  format                                               Auto format source files
  run-locally <auth token>                             Run local version of service
  test                                                 Build and test the invoicing service
  docker-build                                         Build docker image
  talisman-verify                                      Verify no sensitive information is committed using talisman
EOF
  exit 1
}

CMD=${1:-}
shift || true
case ${CMD} in
  check) _check ;;
  format) _format_sources ;;
  run-locally) _run_locally "${1:-}" ;;
  test) _test ;;
  docker-build) _docker_build ;;
  talisman-verify) _talisman_verify ;;
  *) _usage ;;
esac
