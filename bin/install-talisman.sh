#!/usr/bin/env bash

TALISMAN_VERSION="1.29.4"
TALISMAN_SHA256="93aa6f36505a890f97883908dd4078e69a5d74b1f7d00410b7e1ad682c9f2c96"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
