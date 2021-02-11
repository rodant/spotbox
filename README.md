# spot.POD
spot.POD App for spoter.ME.

## Requirements

  * Java 8
  * SBT 1.4.7
  * Scala 2.13.3
  * NodeJS (11.6.0),  NPM, WS (node web server: npm install -g local-web-server)

## Building the App

1. `sbt fastOptJS::webpack` - builds the application in dev mode
2. `./dev-run.sh` - starts a local web server