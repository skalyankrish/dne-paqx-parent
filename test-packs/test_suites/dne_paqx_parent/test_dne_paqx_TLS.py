#!/usr/bin/python

# Author: russed5
# Revision: 1.2
# Code Reviewed by:
# Description: Verify that the Dell CPSD DNE Node Expansion Service communicates over TLS to the Rabbitmq AMQP bus

#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information

import pytest
import requests
import json
import time
import af_support_tools


##############################################################################################

@pytest.fixture(scope="module", autouse=True)
def load_test_data():

     import cpsd
     global cpsd

     # Set config ini file name
     global env_file
     env_file = 'env.ini'

     # Test VM Details
     global ipaddress
     ipaddress = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
     global cli_username
     cli_username = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS',
                                                              property='username')
     global cli_password
     cli_password = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS',
                                                              property='password')

##############################################################################################

@pytest.mark.skip(reason='Unable to test using RMQ API due to TLS')
@pytest.mark.dne_paqx_parent_mvp
@pytest.mark.dne_paqx_parent_mvp_extended
def test_dnePAQX_container_using_AMQP_over_TLS():
    """ Verify Dell CPSD DNE Node Expansion Service communicates with rabbitmq over TLS

    this test uses the  processId of the dne paqx container
    to verify that the network connection to rabbitmq for that container
    is using the rabbitmq tls port"""

    try:

        ssl_listen_port = getRabbitmqSSLport(ipaddress)
        assert ssl_listen_port, "Error : It was not possible to retrieve the rabbitmq SSL port "

        # For the dne paqx, use 'docker network inspect' to ascertain its docker network IP address
        # and use this ip address with the 'netstat' command to find the dne-rabbitmq port-pairing.
        # example return text would be :
        # tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp
        connectionPorts = getDockerNetworkConnectionPorts('dell-cpsd-dne-node-expansion-service')

        # shouldn't be more then 1 line returned, but just in case we split them
        connectionsText = connectionPorts.splitlines()

        # if no lines were returned, then there is an error
        assert connectionsText, 'no rabbitmq connection detected for DNE PAQX'

        for line in connectionsText:
            # check that it is the correct TLS port that is listed for the rabbitmq-dne connection
            assert ssl_listen_port in line, "The DNE PAQX is not using the SSL Port"


    except Exception as err:

        # Return code error (e.g. 404, 501, ...)

        print(err)

        print('\n')

        raise Exception(err)

@pytest.mark.skip(reason='Covered by implementation of tls')
@pytest.mark.dne_paqx_parent_mvp
@pytest.mark.dne_paqx_parent_mvp_extended
def test_DNEpaqx_AMQP_data_is_encrypted():
    """ Verify Dell CPSD DNE Node Expansion to Rabbitmq data is encrypted.

    1. get the DNE containerID and associated IP address
    2. Get the Rabbitmq ContainerID and associated IP Address
    3. Install the tcpdump utility if not already installed
    4. Run tcpdump against DNE -> rabbitmq messaging and check if data is marked as amqps (ie. encrypted)"""

    # get the containerID of the DNE container
    DNEcontainerID = getContainerId('dell-cpsd-dne-node-expansion-service')
    # get the ipaddress of the DNE container
    DNEipAddressText = getContainerIPAddress(DNEcontainerID)

    # get the containerID of the rabbitmq container
    RMQcontainerID = getContainerId('amqp')
    # get the ipaddress of the rabbitmq container
    RMQipAddressText = getContainerIPAddress(RMQcontainerID)

    # The 'tcpdump' tool is used to inspect the format of the data on the rabbitmq channel (ie. is it encrypted).
    check_for_and_install_tcpdump()

    # using tcpdump, we inspect the data associated with the DNE to Rabbitmq IP Address's and expect to see output
    # similar to below. We are particularly interested in the 'amqps' text which indicates secure
    # amqp encryption. The data capture will timeout after 60 seconds.
    #
    #   tcpdump: verbose output suppressed, use -v or -vv for full protocol decode
    #   listening on docker0, link-type EN10MB (Ethernet), capture size 65535 bytes
    #   09:44:36.382264 IP 172.17.0.16.37054 > 172.17.0.13.amqps: Flags [P.], seq 2415798712:24158 ....
    #   09:44:36.382405 IP 172.17.0.16.37054 > 172.17.0.13.amqps: Flags [P.], seq 1893:1978, ack 1,  ....

    commandCheckEncryption = "timeout 60 tcpdump src " + DNEipAddressText.rstrip() + " and dst " + RMQipAddressText.rstrip() + " and dst port amqps"

    return_text = af_support_tools.send_ssh_command(
        host=ipaddress,
        username=cli_username,
        password=cli_password,
        command=commandCheckEncryption,
        return_output=True)

    assert 'amqps' in return_text, "Error : no amqps connection was discovered for the dne paqx"

#####################################################################################################

def getRabbitmqSSLport(ip):
    """ Retrieve the rabbitmq ssl port via the rabbitmq http api."""

    apipath = "/api/overview"
    my_url = 'http://' + ipaddress + ':15672' + apipath

    try:
        url_response = requests.get(my_url, auth=(cpsd.props.rmq_username,cpsd.props.rmq_password))
        url_response.raise_for_status()

        # 200
        data = json.loads(url_response.text)
        for i in data['listeners']:
            if i['protocol'] == 'amqp/ssl':
                ssl_port = i['port']

        if ssl_port:
            portToReturn = str(ssl_port)
        else:
            portToReturn = None

        return portToReturn

    except Exception as err:
        # Return code error (e.g. 404, 501, ...)
        print(err)
        print('\n')
        raise Exception(err)


def getPeerPortFromNetstatOutput(netstatText):
    """ A function to extract and return the non-rabbitmq peer port from specific netstat output text.

    the expected format of text is :
    tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp
    In this sample case, '47388' would be returned """

    portstext = netstatText.split(':')
    peer_port_raw = portstext[2].split(' ')
    peer_port = peer_port_raw[0]

    return peer_port


def getDockerNetworkConnectionPorts(containerName, label="ports"):
    """ A function to determine the docker network ipaddress of a PAQX container.

    Docker commands are used to determine the IP Address of the PAQX container.
    'netstat' is then used to return the connection details for that IP Address.
    If a second parameter is  passed to this function, and is equal to 'processId', then the connection details
    are further broken down to return just the processID.
    Eg. from the sample data below, the process ID = 987 would be returned
    tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp"""

    containerId = getContainerId(containerName)
    ipAddressText = getContainerIPAddress(containerId)

    commandGetContainerConnectionPort = "docker exec -i "+containerId.rstrip()+" netstat -tupn | grep " + ipAddressText.rstrip() + ": | grep -v 8500 | grep -v 8071"
    connectionPorts = af_support_tools.send_ssh_command(
                host=ipaddress,
                username=cli_username,
                password=cli_password,
                command=commandGetContainerConnectionPort,
                return_output=True)

    if label == "processId" :
        processtext = connectionPorts.split("ESTABLISHED")
        process_raw = processtext[1].split('/')
        processId = process_raw[0]
        return processId
    else:
        return connectionPorts

#####################################################################################

def getContainerId(containerName):
    """ A function to return the docker container ID, given the name of the container"""

    commandGetContainerId = "docker ps | grep " + containerName + " | awk '{print $1}' "
    containerId = af_support_tools.send_ssh_command(
        host=ipaddress,
        username=cli_username,
        password=cli_password,
        command=commandGetContainerId,
        return_output=True)
    return  containerId

#####################################################################################

def  getContainerIPAddress(containerId):
    """ A function to return the docker IP Address, given the container Id"""

    commandGetIPAddress = "docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + containerId.rstrip()
    ipAddressText = af_support_tools.send_ssh_command(
        host=ipaddress,
        username=cli_username,
        password=cli_password,
        command=commandGetIPAddress,
        return_output=True)

    return ipAddressText


#####################################################################################

def check_for_and_install_tcpdump():
    """ A function to install the tcpdump tool, via yum, if it is not already installed"""

    # check if the tcpdump is installed. this tool is used to inspect the data on the rabbitmq channel
    commandCheckForTCPDUMP = "yum list installed | grep tcpdump"

    tcpdump_Installed=af_support_tools.send_ssh_command(
        host=ipaddress,
        username=cli_username,
        password=cli_password,
        command=commandCheckForTCPDUMP,
        return_output=True)

    # install the tcpdump if it is not already installed
    if not tcpdump_Installed :
        commandInstallTCPDUMP = "yum -y install tcpdump"
        ipmitool_Installed=af_support_tools.send_ssh_command(
            host=ipaddress,
            username=cli_username,
            password=cli_password,
            command=commandInstallTCPDUMP,
            return_output=True)
