#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#
#  USAGE: (from .. dir)
#    docker build -f dne-paqx-distribution/Dockerfile --tag=dell-cpsd-dne-node-expansion-service dell-cpsd-dne-node-expansion-service
#

# Start from base image with Java 8
FROM openjdk:8-jre-alpine
LABEL License="Eclipse Public License 1.0 (EPL-1.0)"

# Install the service file(s) to run
WORKDIR /opt/dell/cpsd/dne/node-expansion-service

COPY dependency/lib/* ./lib/
COPY conf/* ./conf/
COPY service/run.sh ./bin/

ARG DELL_JAVA_VERSION=unknown
ARG BUILD_NUMBER=unknown
ARG GIT_COMMIT=unknown
ARG BUILD_TYPE=dev
ARG CPSD_SOFTWARE_VERSION=unknown

RUN apk update \
&& apk add ca-certificates \
           java-cacerts \
           curl \
           ca-certificates-doc \
           jq \
           openssl \
&& update-ca-certificates \
&& mkdir ./logs \
&& chmod +x ./bin/run.sh

LABEL   com.dell.cpsd.version=$CPSD_SOFTWARE_VERSION\
        com.dell.cpsd.git_revision=$GIT_COMMIT\
        com.dell.cpsd.build_number=$BUILD_NUMBER\
        com.dell.cpsd.build_type=$BUILD_TYPE\
        com.dell.cpsd.java=$DELL_JAVA_VERSION

EXPOSE 8071

ENTRYPOINT ["./bin/run.sh"]
