#!/usr/bin/env bash

TALISMAN_VERSION="1.31.0"
TALISMAN_SHA256="0bb3527fb8d6d9e776f6e19b24c9b92ce45998fec8bc75d3140dcbda27bb2874"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
