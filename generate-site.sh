#!/bin/bash
set -e
mvn site
cd ../davidmoten.github.io
git pull
mkdir -p geo
cp -r ../geo/target/site/* geo/
git add .
git commit -am "update site reports"
git push
