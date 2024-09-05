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
    mvn -X deploy:deploy-file \
    -Dfile=target/dummycompany-karate-archetype-0.1.0-SNAPSHOT.jar \
    -DgroupId=com.dummycompany.api.tests \
    -DartifactId=dummycompany-karate-archetype \
    -Dversion=${bamboo.releaseInfo.version} \
    -DgeneratePom=true \
    -Dpackaging=jar \
    -DnexusUser=${bamboo.nexus_deployment_user} \
    -DnexusPassword=${bamboo.nexus_deployment_password} \
    -DrepositoryId=dummycompany-releases \
    -Durl=https://repo1.ae.sda.corp.dummycompany.com/nexus/content/repositories/releases
