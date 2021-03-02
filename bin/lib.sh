#!/usr/bin/env bash

#REVISION=$(git rev-parse HEAD)
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
  _ensure_jenv
  jenv exec ./gradlew clean dependencyCheckAnalyze --info --stacktrace

}

_license_report() {
  _ensure_jenv
  jenv exec ./gradlew clean generateLicenseReport

}

_test() {
  prettier --check "src/**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  _ensure_jenv
  jenv exec ./gradlew clean check

  while IFS= read -r -d '' file; do
    hadolint "${file}"
  done < <(find . -name "*Dockerfile*" -print0)
}

_format_sources() {
  prettier --write "src/**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  _ensure_jenv
  "${SCRIPT_DIR}/gradlew" goJF
}

_run_locally() {
  _ensure_jenv
  SPRING_PROFILES_ACTIVE=local GITHUB_ACCESS_TOKEN="${1}" jenv exec ./gradlew clean bootRun
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
