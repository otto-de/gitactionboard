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
  npm audit --production --audit-level=high
  popd >/dev/null || exit

}

_license_report() {
  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean generateLicenseReport
  popd >/dev/null || exit

}

_test() {
  prettier --check "**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  jenv exec ./gradlew clean check
  popd >/dev/null || exit

  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm install
  npm run lint
  popd >/dev/null || exit

  while IFS= read -r -d '' file; do
    hadolint "${file}"
  done < <(find . -name "*Dockerfile*" -print0)
}

_format_sources() {
  prettier --write "**/*.{json,js,css,html}" "**/*.md"

  # shellcheck disable=SC2035
  shellcheck -x **/*.sh

  pushd "${SCRIPT_DIR}/backend" >/dev/null || exit
  _ensure_jenv
  "${SCRIPT_DIR}/backend/gradlew" goJF
  popd >/dev/null || exit

  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm install
  npm run lint:fix
  popd >/dev/null || exit
}

_copy_frontend(){
  pushd "${SCRIPT_DIR}/frontend" >/dev/null || exit
  _ensure_nvm
  npm install
  npm run build
  echo "Copying dist to ../backend/src/main/resources/public"
  cp -r dist/ ../backend/src/main/resources/public
  popd >/dev/null || exit
}

_run_locally() {
  local with_frontend="${2}"

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

_bump_version(){
  local bump_component="${1}"
  pushd "backend" >/dev/null || exit
    _ensure_jenv
    jenv exec ./gradlew tag -Prelease -PbumpComponent="${bump_component}" -Dmessage="$(git log -1 --format=%s)"
  popd >/dev/null || exit
}
