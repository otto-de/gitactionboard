#!/usr/bin/env bash

HADOLINT_VERSION="2.12.0"

_main(){
 curl -fsSL -o ./hadolint "https://github.com/hadolint/hadolint/releases/download/v${HADOLINT_VERSION}/hadolint-Linux-x86_64" &&
  chmod a+x ./hadolint
}

_main
