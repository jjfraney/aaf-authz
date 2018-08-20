#!/bin/bash
# Docker push Script.  Reads all the components generated by install, on per-version basis
#
# Pull in Variables from d.props
. ./d.props

AAF_COMPONENTS=$(cat components)

for AAF_COMPONENT in ${AAF_COMPONENTS}; do
        # docker push ${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/aaf_${AAF_COMPONENT}:${OLD_VERSION}
        docker push ${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/aaf_${AAF_COMPONENT}:${VERSION}
        docker push ${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/aaf_${AAF_COMPONENT}:latest
        # docker push ${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/aaf_${AAF_COMPONENT}:${NEW_VERSION}
done
