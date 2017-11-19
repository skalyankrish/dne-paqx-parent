#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#

%define _source %_name-%_version.tgz

Summary: Dell Inc. DNE PAQX
Name:    %_name
Version: %_version
Release: %_revision
Source:  %_source
License: Commercial
Vendor: Dell Inc.
Group: System Environment/Dell Inc. Applications
URL: http://www.dell.com

BuildArch: %(uname -m)
Requires: dell-cpsd-core-services
Requires: dell-cpsd-core-adapters
Requires: dell-cpsd-dne-node-discovery-service
Requires: dell-cpsd-dne-engineering-standards-service

%description
Dell Inc. DNE PAQX

%define  debug_package %{nil}

%prep
%setup -q

%build

%pre

getent group dell >/dev/null || /usr/sbin/groupadd -f -r dell
getent passwd core >/dev/null || /usr/sbin/useradd -r -g dell -s /sbin/nologin -M core
exit 0

%install

install -D dell-cpsd-dne-node-expansion-service.tar %{buildroot}/opt/dell/cpsd/dne/node-expansion-service/image/dell-cpsd-dne-node-expansion-service.tar
install -D install/docker-compose.yml %{buildroot}/opt/dell/cpsd/dne/node-expansion-service/install/docker-compose.yml

%post

if [ $1 -eq 1 ];then
    RETVAL=0

    SERVICE_BASE=/opt/dell/cpsd/dne/node-expansion-service

    echo "Installing Dell Inc.DNE PAQX components"

    if [ ! -d "$SERVICE_BASE" ]; then
        echo "Could not find directory - [$SERVICE_BASE] does not exist."
        exit 1
    fi

    usermod -aG docker core

    docker load -i ${SERVICE_BASE}/image/dell-cpsd-dne-node-expansion-service.tar

    (
        cd ${SERVICE_BASE}/install/
        docker-compose create
    )
    docker start dell-cpsd-dne-node-expansion-service

    echo "Dell Inc. DNE PAQX components install has completed successfully."

    exit 0
elif [ $1 -eq 2 ];then
    echo "Upgrade is not possible with this rpm"
    exit 1
else
    echo "Unexpected argument passed to RPM %post script: [$1]"
    exit 1
fi
exit 0

%preun

if [ $1 -eq 0 ];then
    SERVICE_BASE=/opt/dell/cpsd/dne/node-expansion-service

    echo "Removing Dell Inc. DNE PAQX components"
    echo "This will take a few minutes and several services will be restarted on the system. This is normal and please let the script run to completion"


    echo "Stopping Dell Inc. DNE PAQX components"
    /usr/bin/docker stop dell-cpsd-dne-node-expansion-service
    /usr/bin/docker rm --volumes dell-cpsd-dne-node-expansion-service

    echo "Deleting the DNE PAQX components for Dell Inc."
    /usr/bin/docker rmi $(docker images -q dell-cpsd-dne-node-expansion-service)

    echo "Dell Inc. DNE PAQX components removal has completed successfully"
fi
exit 0

%files

%attr(0754,core,dell) %dir /opt/dell/cpsd/dne/node-expansion-service
%attr(0755,core,dell) %dir /opt/dell/cpsd/dne/node-expansion-service/image
%attr(0755,core,dell) %dir /opt/dell/cpsd/dne/node-expansion-service/install
%attr(0644,root,root) /opt/dell/cpsd/dne/node-expansion-service/install/docker-compose.yml
%attr(0644,root,root) /opt/dell/cpsd/dne/node-expansion-service/image/dell-cpsd-dne-node-expansion-service.tar
