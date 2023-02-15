#!/usr/bin/env bash

TALISMAN_VERSION="1.30.0"
TALISMAN_SHA256="4b416431264bfe079f12faf71426ae7fbdd36b5c499c9b80b274b61d75f82059"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
