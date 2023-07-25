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

_ensure_nvm() {
  if ! type nvm &>/dev/null; then
    export NVM_DIR="$HOME/.nvm"
    # shellcheck disable=SC1091 disable=SC1090
    [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
  fi

  if ! type nvm &>/dev/null; then
    echo "Please install nvm!" 1>&2
    exit 1
  fi
  nvm use || nvm install
}

_check() {
  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean dependencyCheckAnalyze --info --stacktrace
  popd >/dev/null || exit

  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm audit --audit-level=moderate
  popd >/dev/null || exit

}

_license_report() {
  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean generateLicenseReport
  popd >/dev/null || exit

}

_test() {
  prettier --check "**/*.{json,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  local code_folder="${1:-}"

  if [[ -z ${code_folder} || ${code_folder} == "backend" ]]; then
      pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
      _ensure_jenv
      jenv exec ./gradlew clean check
      popd >/dev/null || exit
  fi

  if [[ -z ${code_folder} || ${code_folder} == "frontend" ]]; then
      pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
      _ensure_nvm
      npm install
      npm run lint
      npm test
      popd >/dev/null || exit
  fi

  while IFS= read -r -d '' file; do
    hadolint "${file}"
  done < <(find . -maxdepth 2 -name "*Dockerfile*" -print0)
}

_pitest() {
  local use_history="${1:-true}"
  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
    _ensure_jenv
    if [[ "$use_history" == true ]]; then
      jenv exec ./gradlew pitest
    else
      rm -rf pitestHistory
      jenv exec ./gradlew clean pitest
    fi
  popd >/dev/null || exit
}

_format_sources() {
  prettier --write "**/*.{json,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  "${SCRIPT_DIR}/backend/gradlew" spotlessJavaApply
  popd >/dev/null || exit

  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm install
  npm run lint:fix
  popd >/dev/null || exit
}

_copy_frontend() {
  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm install
  npm run build
  echo "Copying dist to ../backend/src/main/resources/public"
  cp -r dist/ ../backend/src/main/resources/public
  popd >/dev/null || exit
}

_build_jar() {
  _copy_frontend
  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
    _ensure_jenv
    jenv exec ./gradlew clean bootJar
  popd >/dev/null || exit
}

_run_locally() {
  local with_frontend="${2}"

  #!/bin/sh
  # shellcheck disable=SC2317
  _revert() {
    if [ "${with_frontend}" ]; then
      pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
      echo "Removing backend/src/main/resources/public"
      rm -rf "${SCRIPT_DIR}/backend/src/main/resources/public"
      popd >/dev/null || exit
    fi

    exit 0
  }

  trap _revert SIGTERM SIGINT ERR

  if [ "${with_frontend}" ]; then
    _copy_frontend
  fi

  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  SPRING_PROFILES_ACTIVE=local GITHUB_ACCESS_TOKEN="${1}" jenv exec ./gradlew clean bootRun
  popd >/dev/null || exit
}

_run_frontend_locally() {
  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
    _ensure_nvm
     npm run serve-with-mock-data
  popd >/dev/null || exit
}

_docker_build() {
  _build_jar
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

_trivy_verify() {
  _docker_build
  trivy image "docker.io/library/${SERVICE_NAME}:${REVISION}" --severity HIGH,CRITICAL,MEDIUM --security-checks vuln --ignore-unfixed
}

_bump_version() {
  _revert() {
    rm -rf "backend/.git"
  }

  pushd "backend" >/dev/null || exit
    _ensure_jenv
    trap _revert SIGTERM SIGINT ERR
    jenv exec ./gradlew tag -Prelease -Dmessage="$(git log -1 --format=%s)"
    _revert
  popd >/dev/null || exit
}

_add_contributor() {
  local username="${1}"
  local contributionType="${2}"
  all-contributors add "${username}" "${contributionType}"
}

_generate_contributors_list() {
  all-contributors generate
}

_generate_changelog() {
  git cliff -o CHANGELOG.md

  prettier --write "CHANGELOG.md"
}

_generate_changelog_url() {
  local tag="${1}"
  local date

  date=$(grep "## \[${tag}\]" CHANGELOG.md | sed -n "s/.*${tag}) //p" | sed s/\)// | sed s/\(//)

  echo "https://github.com/otto-de/gitactionboard/blob/main/CHANGELOG.md#${tag//./}-${date}"
}

_frontend_build_for_github_pages(){
  pushd "frontend" >/dev/null || exit
    _ensure_nvm
    npm install
    VITE_PROXY_TARGET="https://otto-de.github.io/gitactionboard/apis" BASE_PATH="/gitactionboard" npm run build
    for route in $(jq -r '.routes[] | select(.method == "get") | @base64' mock-data/data.json); do
      local endpoint
      local content
      endpoint=$(echo "${route}" | base64 --decode | jq -r ".endpoint")
      content=$(echo "${route}" | base64 --decode | jq -r ".responses[0].body")
      mkdir -p "$(dirname dist/apis/"${endpoint}")"
      if [[ ${endpoint} == "config" ]]; then
        echo "${content}" | jq -r '.availableAuths = []' > "dist/apis/${endpoint}"
      else
        echo "${content}" > "dist/apis/${endpoint}"
      fi
    done
  popd >/dev/null || exit
}
