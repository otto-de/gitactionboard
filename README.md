# Gitaction Board

Gitaction Board - Ultimate Dashboard for GithubActions.

# Table of contents

- [Usage](#usage)
  - [Pull docker image](#pull-docker-image)
  - [Run docker image](#run-docker-image)
    - [Configurations](#configurations)
    - [UI Dashboard](#ui-dashboard)
      - [UI Dashboard Configurations](#ui-dashboard-configurations)
    - [API](#api)
- [Developers Guide](#developers-guide)
  - [Prerequisites](#prerequisites)
  - [Tests](#tests)
  - [Formatting](#formatting)
  - [Security Checks](#security-checks)
  - [Run application locally](#run-application-locally)
  - [Build docker image](#build-docker-image)

## Usage

### Pull docker image

```shell script
docker pull ottoopensource/gitactionboard:<docker tag>
```

### Run docker image

```shell script
docker run \
  -p <host machine port>:8080 \
  -e REPO_OWNER_NAME=<organization/username> \
  -e REPO_NAMES=<repo names> \
  -it ottoopensource/gitactionboard:<docker tag>
```

#### Configurations

| Environment variable name | Descriptions                                                                                     | Required |     Default value      |          Example value          |
| :------------------------ | :----------------------------------------------------------------------------------------------- | :------: | :--------------------: | :-----------------------------: |
| REPO_OWNER_NAME           | Repository owner name. Generally, its either organization name or username                       |   yes    |                        |             webpack             |
| REPO_NAMES                | List of name of repositories you want to monitor                                                 |   yes    |                        | webpack-dev-server, webpack-cli |
| GITHUB_ACCESS_TOKEN       | Access token to fetch data from github. This is required to fetch data from a private repository |    no    |                        |                                 |
| DOMAIN_NAME               | Hostname of github                                                                               |    no    | https://api.github.com |                                 |
| CACHE_EXPIRES_AFTER       | Duration (in seconds) to cache the fetched data                                                  |    no    |           60           |                                 |

Note: To create a personal access token follow the instructions present [here](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) and choose **repo** as a scope fot this token.

#### UI Dashboard

**Gitaction Board** has in-built UI dashboard to visualize the build status. You can access the following endpoint for the same

- `http://localhost:<host machine port>`

![UI dashboard sample 1](https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/sample1.png)

![UI dashboard sample 2](https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/sample1.png)

![UI dashboard sample 3](https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/sample1.png)

##### UI Dashboard Configurations

You can use the following query params to configure UI dashboard

| Query param name      | Descriptions                                                                                              | Required | Default value | Example value |
| :-------------------- | :-------------------------------------------------------------------------------------------------------- | :------: | :-----------: | :-----------: |
| hide-healthy          | Hide all the healthy builds from the dashboard                                                            |    no    |     false     |     true      |
| max-idle-time         | Configure the max idle time (in minutes), after the given time background polling for dashboard will stop |    no    |       5       |       2       |
| disable-max-idle-time | Disable background polling optimisation configured using **max-idle-time**                                |    no    |     false     |     true      |

#### API

Once server is up, you can access the following endpoints to get the CCtray data.

##### Data in XML format

Access `http://localhost:<host machine port>/v1/cctray.xml` to get data in **XML** format

###### Sample response

```xml
<Projects>
    <Project name="hello-world :: hello-world-build-and-deployment :: talisman-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:11:41Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386046"/>
    <Project name="hello-world :: hello-world-build-and-deployment :: dependency-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127"/>
    <Project name="hello-world :: hello-world-checks :: format" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:11:41Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386046"/>
    <Project name="hello-world :: hello-world-checks :: test" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127"/>
</Projects>
```

##### Data in JSON format

Access `http://localhost:<host machine port>/v1/cctray` to get data in **JSON** format

###### Sample response

```json
[
  {
    "name": "hello-world :: hello-world-build-and-deployment :: talisman-checks",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:11:41.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386046"
  },
  {
    "name": "hello-world :: hello-world-build-and-deployment :: dependency-checks",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:14:54.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386127"
  },
  {
    "name": "hello-world :: hello-world-checks :: format",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:11:41.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386046"
  },
  {
    "name": "hello-world :: hello-world-checks :: test",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:14:54.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386127"
  }
]
```

## Developers Guide

### Prerequisites

- [jEnv](https://www.jenv.be/)
- Java 11
- [Docker](https://www.docker.com/)
- [Hadolint](https://github.com/hadolint/hadolint)
- [ShellCheck](https://www.shellcheck.net/)
- [Prettier](https://prettier.io/)
- [talisman](https://github.com/thoughtworks/talisman)
- [jq](https://stedolan.github.io/jq/)

### Tests

Tests are separated into unit and integration test sets. To denote an integration test it needs to be annotated
as `@IntegrationTest`.

To run only unit tests:

```shell script
./gradlew test
```

To run integration tests:

```shell script
./gradlew integrationTest
```

To run all the verifications:

```shell script
./run.sh test
```

Test sets are runs in order, with unit tests first, followed by integration tests.
Should there be a failure in the unit tests the task execution stops there, e.g.
a prerequisite for running integration tests will be working unit tests.

### Formatting

This project uses the following tools to follow specific style guide

- [google-java-format-gradle-plugin](https://github.com/sherter/google-java-format-gradle-plugin) for **Java** files
- [Prettier](https://prettier.io/) for **json**, **js**, **css**, **html** and **md** files
- [Hadolint](https://github.com/hadolint/hadolint) for **Dockerfile**
- [ShellCheck](https://www.shellcheck.net/) for **sh** files

To run format:

```shell script
./run.sh format
```

### Security Checks

This project uses the following tools to find security vulnerabilities

- [org.owasp.dependencycheck](https://plugins.gradle.org/plugin/org.owasp.dependencycheck) to find vulnerable dependencies
- [talisman](https://github.com/thoughtworks/talisman) to validate the outgoing changeset for things that look suspicious - such as authorization tokens and private keys.

To run OWASP dependency check:

```shell script
./run.sh check
```

### Run application locally

This service can be run locally. To run it locally, run the following command:

```shell script
./run.sh run-locally <github auth token>
```

### Build docker image

To build docker image run:

```shell script
./run.sh docker-build
```

### Release a new Docker image

To release a new docker image to docker hub using CI/CD, follow the following steps:

- Create a new git **tag**, named as `v*.*.*`

```shell
  git tag v<major>.<minor>.<patch>
```

- Push the **tag** to github

```shell
git push origin v<major>.<minor>.<patch>
```
