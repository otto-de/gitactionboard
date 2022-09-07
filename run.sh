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
  run-locally [auth token] [with frontend]             Run local version of service with/without frontend
  run-frontend-locally                                 Run local version of frontend service with mock data
  test                                                 Build and test the service
  docker-build                                         Build docker image
  build-jar                                            Build executable boot jar file
  talisman-verify                                      Verify no sensitive information is committed using talisman
  bump-version <major|minor|patch>                     Release a new <major|minor|patch> version
  copy-frontend                                        Build and copy the frontend code base to backend resources folder
  generate-changelog                                   Generate changelog from last tag and write in CHANGELOG.md
  generate-changelog-url [tag]                         Generate changelog url for given tag
  add-contributor <username> <contribution type>       Add new contributor to contributors list
  generate-contributors-list                           Generate contributors list from .all-contributorsrc file and update it in readme
EOF
  exit 1
}

CMD=${1:-}
shift || true
case ${CMD} in
  check) _check ;;
  format) _format_sources ;;
  run-locally) _run_locally "${1:-}" "${2:-true}" ;;
  run-frontend-locally) _run_frontend_locally ;;
  test) _test ;;
  docker-build) _docker_build ;;
  talisman-verify) _talisman_verify ;;
  bump-version) _bump_version "${1}" ;;
  copy-frontend) _copy_frontend ;;
  generate-changelog) _generate_changelog ;;
  add-contributor) _add_contributor "${1}" "${2}" ;;
  generate-contributors-list) _generate_contributors_list ;;
  generate-changelog-url) _generate_changelog_url "${1}" ;;
  build-jar) _build_jar ;;
  *) _usage ;;
esac
