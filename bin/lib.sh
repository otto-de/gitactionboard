#!/usr/bin/env bash

REVISION=$(git rev-parse HEAD)
SERVICE_NAME="gitactionboard"

_ensure_jenv() {
  if ! type jenv &>/dev/null; then
    export PATH="$PATH:$HOME/.jenv/bin"
  fi
  if ! type jenv &>/dev/null; then
    echo "Please install jenv!" 1>&2
    exit 1
  fi
  if [ -z "${JENV_LOADED:-}" ]; then
    eval "$(SHELL=/bin/bash jenv init -)"
  fi
}

_check() {
  pushd "backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean dependencyCheckAnalyze --info --stacktrace
  popd >/dev/null || exit

}

_license_report() {
  pushd "backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean generateLicenseReport
  popd >/dev/null || exit

}

_test() {
  prettier --check "backend/src/**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  pushd "backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean check
  popd >/dev/null || exit

  while IFS= read -r -d '' file; do
    hadolint "${file}"
  done < <(find . -name "*Dockerfile*" -print0)
}

_format_sources() {
  prettier --write "backend/src/**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  pushd "backend" >/dev/null || exit
  _ensure_jenv
  "${SCRIPT_DIR}/backend/gradlew" goJF
  popd >/dev/null || exit
}

_run_locally() {
  pushd "backend" >/dev/null || exit
  _ensure_jenv
  SPRING_PROFILES_ACTIVE=local GITHUB_ACCESS_TOKEN="${1}" jenv exec ./gradlew clean bootRun
  popd >/dev/null || exit
}

_docker_build() {
  docker build -t "${SERVICE_NAME}:${REVISION}" .
}

_talisman_verify() {
  if ! echo "/refs/heads/main $(git rev-list --max-count=1 HEAD) /refs/heads/main $(git rev-list --max-parents=0 HEAD)" | talisman -g pre-push; then
    local RED="\033[0;31m"
    local RESET="\033[0m"
    echo
    echo -e "${RED}There are things that look like secrets in your code. If you are really sure that this unproblematic just push the next commit."
    echo -e "If the last commit really contained a secret, please invalidate it and force push to make the build green.${RESET}"
    exit 1
  fi
}
