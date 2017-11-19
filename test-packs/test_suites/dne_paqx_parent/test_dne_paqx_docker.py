import af_support_tools
import os
import pytest
@pytest.fixture(scope="module", autouse=True)
def load_test_data():
    # Set config ini file name
    global env_file
    env_file = 'env.ini'

    # Set Vars
    global ipaddress
    ipaddress = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
    global username
    username = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='username')
    global password
    password = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='password')
#####################################################################
# These are the main tests.
#####################################################################
@pytest.mark.dne_paqx_parent_mvp
@pytest.mark.dne_paqx_parent_mvp_extended
@pytest.mark.parametrize("container_name", [("dell-cpsd-dne-node-expansion-service")])
def test_verify_docker_container(container_name):
    """
    Description        :       This method tests docker service for a container
    it will asserts if :
    Docker service is not running for the container
    Container is not having docker0 
    Parameters  	:       1. container_name       -       Name of the container(STRING)
    Returns             :       None
    """
    assert verify_docker_network(container_name), 'test failed: ' + container_name + ' docker service is not running'
def verify_docker_network(container_name):
    """
    Description 	:       This method will check docker network inspect bridge and host commands to verify if the container is using docker networking service
    Parameters  	:       1. container_name       -       Name of the container(STRING)
    Returns             :       0 or 1 (Boolean)
    """
    test_status = "pass"
    cmd = 'docker network inspect bridge |  grep ' + str(container_name)
    output = af_support_tools.send_ssh_command(host=ipaddress, username=username, password=password, command=cmd, return_output=True).strip()
    if (container_name not in output):
        test_status = "fail"

    if (test_status == "fail"):
        return 0
    else:
        return 1
