#!/usr/bin/env bash

TALISMAN_VERSION="1.29.4"
TALISMAN_SHA256="87b32e5fd48ab710b4d63fe0e5098612df54e9a8afdb9c57968ee63fb1a0449d"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
