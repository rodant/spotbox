#!/bin/sh

set -o errexit -o nounset

echo "Building App..."
sbt test & sbt fullOptJS::webpack

echo "Copying the App Bundle to public dir..."
cp target/scala-2.12/scalajs-bundler/main/spotbox-opt-bundle.js public/spotbox/js/

echo "Uploading the App to the Solid Server..."
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file index.html https://spotbox.solid.community/index.html
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotbox/css/app.css https://spotbox.solid.community/public/spotbox/css/app.css
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotbox/images/logo.png https://spotbox.solid.community/public/spotbox/images/logo.png
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/spotbox/js/spotbox-opt-bundle.js https://spotbox.solid.community/public/spotbox/js/spotbox-opt-bundle.js
echo "Done :-)"
