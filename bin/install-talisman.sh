#!/usr/bin/env bash

TALISMAN_VERSION="1.29.1"
TALISMAN_SHA256="f0beadec8603e668110739c19bc01a1877b0d3e73fb5171a83b0a4c08a091c6d"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
