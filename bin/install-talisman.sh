#!/usr/bin/env bash

TALISMAN_VERSION="1.23.0"
TALISMAN_SHA256="639535cb45c110bee36c4c16d59501d263a9c95a98294cf2e6f736dc63abbaaf"

_main(){
 curl -fsSL -o ./talisman "https://github.com/thoughtworks/talisman/releases/download/v${TALISMAN_VERSION}/talisman_linux_amd64" &&
  echo "${TALISMAN_SHA256} ./talisman" | sha256sum -c &&
  chmod a+x ./talisman
}

_main
