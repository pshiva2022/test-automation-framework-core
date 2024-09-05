#!/bin/bash
set -e
echo "inside standard-version.sh"
git config --local push.followTags true

echo "after git config"
docker pull harbor.tools.dummycompany.com/yantra/detouched/standard-version:latest

docker run --rm -i \
    -v $(pwd):/app \
    -e "GIT_AUTHOR_NAME=ci" \
    -e "EMAIL=d1102575_ext@team.dummycompany.com" harbor.tools.dummycompany.com/yantra/detouched/standard-version:latest --skip.commit

version=`git describe --tags --abbrev=0 --match 'v*.*' | cut -c2-`

echo " version : ${version}"
echo "ran standard-version container"

# Append version to release-info.properties
echo version=${version} >> release-info.properties

cat release-info.properties

echo "finished standard-version.sh"