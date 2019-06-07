#!/bin/sh

echo "Building App..."
sbt test & sbt fullOptJS::webpack

USER = spoterme-spotbox
APP = spotbox

echo "Copying the App Bundle to public dir..."
cp target/scala-2.12/scalajs-bundler/main/${APP}-opt-bundle.js public/spotbox/js/

echo "Uploading the App to the Solid Server..."
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file index.html https://${USER}.solid.community/index.html
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/${APP}/css/app.css https://${USER}.solid.community/public/${APP}/css/app.css
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/${APP}/images/logo.png https://${USER}.solid.community/public/${APP}/images/logo.png
echo ":-)"
curl --cookie "connect.sid=${SOLID_COOKIE}" --upload-file public/${APP}/js/${APP}-opt-bundle.js https://${USER}.solid.community/public/${APP}/js/${APP}-opt-bundle.js
echo "Done :-)"
