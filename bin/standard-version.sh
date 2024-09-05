#!/bin/bash

set -e

# export DOCKER_CONTENT_TRUST=1

git config --local push.followTags true

docker run --rm -i \
	-v $(pwd):/app \
	-e "GIT_AUTHOR_NAME=ci" \
	-e "EMAIL=d1102575_ext@team.dummycompany.com" detouched/standard-version:latest

# Append version to release-info.properties	
version=`git describe --tags --abbrev=0 --match 'v*.*' | cut -c2-`
echo version=${version} >> release-info.properties

cat release-info.properties