#!/bin/bash

# This script is only necessary for CI, 
# as CI agents may not have maven repo cache 
echo "Building Builder Image for $1 if necessary"

hash=($(md5sum pom.xml))
builder_image=$1-builder:$hash

# Check if can pull the image
docker pull ${builder_image} --disable-content-trust

if [ $? = 1 ] || [ "${bamboo_REBUILD_BUILDER}" = "true" ]; then
  echo "builder cache ${builder_image} does not exist"
  echo "Building image ${builder_image}"
  docker build -t ${builder_image} -f Dockerfile.cache .
  docker push ${builder_image}
fi

# copy maven repo from builder image to host directory
id=$(docker create $builder_image)
docker cp $id:/usr/share/maven/ref/ $HOME/.m2
docker rm -v $id
echo "Maven repo cache copied to $HOME/.m2 on the host"
ls -lrt $HOME/.m2/repository