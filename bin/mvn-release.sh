#!/bin/bash
echo "Maven release"

# below to exit on error
set -e

# package application using docker container
# change to mvn verify/install to run integration tests
docker run --rm -i \
    -v "$PWD":/usr/src/mymaven \
    -v "$HOME/.m2":/usr/share/maven/ref \
    -w /usr/src/mymaven maven:3.6-jdk-8-slim \
    mvn -B release:clean release:prepare release:perform -Dmaven.test.skip=true