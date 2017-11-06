[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![Build Status](https://travis-ci.org/dellemc-symphony/dne-paqx-parent.svg?branch=master)](https://travis-ci.org/dellemc-symphony/dne-paqx-parent)
# dne-paqx-parent
## Description
This repository provides the service to add a new server to an existing system in the scope of the Flex Node Expansion functionality of Project Symphony.
## Documentation
You can find additional documentation for Project Symphony at [dellemc-symphony.readthedocs.io](https://dellemc-symphony.readthedocs.io).

## API overview
The REST API documentation may be accessed using the following link. http://[symphony-ova]:10000/dne
## Before you begin
Make sure the following is installed:
```
Apache Maven 3.0.5+
Docker 1.12+
Docker Compose 1.8.0+
Java Development Kit(version 8)
RabbitMQ  3.6.6
```
## Building
Run the following command to build this project:
```bash
mvn clean install
```
## Deploying
The output of running the build step is a tagged Docker image.
You can run this locally with the following command:
```bash
docker run -it --net="host" <docker_image_hash>
```
## Contributing
Project Symphony is a collection of services and libraries housed at [GitHub][github].
Contribute code and make submissions at the relevant GitHub repository level.
See our documentation for details on how to [contribute][contributing].
## Community
Reach out to us on Slack [#symphony][slack] channel. Request an invite at [{code}Community][codecommunity]
You can also join [Google Groups][googlegroups] and start a discussion.
 
[slack]: https://codecommunity.slack.com/messages/symphony
[googlegroups]: https://groups.google.com/forum/#!forum/dellemc-symphony
[codecommunity]: http://community.codedellemc.com/
[contributing]: http://dellemc-symphony.readthedocs.io/en/latest/contributingtosymphony.html
[github]: https://github.com/dellemc-symphony
[documentation]: https://dellemc-symphony.readthedocs.io/en/latest/







