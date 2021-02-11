#!/bin/sh

echo "Building App..."
sbt test & sbt fullOptJS::webpack

echo "Copying the App Bundle to public dir..."
cp target/scala-2.13/scalajs-bundler/main/spot-pod-fastopt-bundle.js public/spotpod/js/

echo "Uploading the App to the Solid Server..."
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file index.html https://spotpod.solidcommunity.net/index.html
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotpod/css/app.css https://spotpod.solidcommunity.net/public/spotpod/css/app.css
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotpod/images/logo.png https://spotpod.solidcommunity.net/public/spotpod/images/logo.png
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotpod/js/spot-pod-opt-bundle.js https://spotpod.solidcommunity.net/public/spotpod/js/spotpod-opt-bundle.js
echo "Done :-)"
