#!/usr/bin/env bash

TALISMAN_VERSION="1.11.0"
TALISMAN_SHA256="8fbab0ab3dbbb1f64ba2b0040f2b4ef10e66eb0e2553c4fd80c3ba65063736ab"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
