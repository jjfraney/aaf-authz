#!/bin/bash
# 
# Build AAF Cass Docker Script locally
#
# Pull in AAF Env Variables from AAF install
if [ -e ../../docker/d.props ]; then
  . ../../docker/d.props
else
  . ../../docker/d.props.init
fi

echo "Building aaf_cass Container for aaf_cass:$VERSION"

DIR=$(pwd)
cd ..
sed -e 's/${AAF_VERSION}/'${VERSION}'/g' $DIR/Dockerfile.cass > Dockerfile
cd ..
cp -Rf sample/cass_data auth-cass/cass_data
cp sample/data/sample.identities.dat auth-cass

docker build -t ${ORG}/${PROJECT}/aaf_cass:${VERSION} auth-cass
docker tag ${ORG}/${PROJECT}/aaf_cass:${VERSION} ${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/aaf_cass:${VERSION}

cd -
rm Dockerfile
rm -Rf cass_data
rm sample.identities.dat
cd $DIR

