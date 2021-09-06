#!/bin/bash
PROJECT=geo
set -e
mvn clean install 
mvn site
mvn site:stage -DstagingDirectory=`pwd`/target/staging-temp
cd ../davidmoten.github.io
git pull
mkdir -p $PROJECT
cp -r ../$PROJECT/target/staging-temp/* $PROJECT/
git add .
git commit -am "update site reports for $PROJECT"
git push

