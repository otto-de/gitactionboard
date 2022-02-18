# Gitaction Board

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->

[![All Contributors](https://img.shields.io/badge/all_contributors-7-orange.svg?style=flat-square)](#contributors-)

<!-- ALL-CONTRIBUTORS-BADGE:END -->

Gitaction Board - Ultimate Dashboard for GithubActions.

# Table of contents

- [Changelog](#changelog)
- [Usage](#usage)
  - [Pull docker image](#pull-docker-image)
  - [Run docker image](#run-docker-image)
    - [Configurations](#configurations)
      - [Authentication](#authentication)
        - [Basic Authentication](#basic-authentication)
        - [Github OAuth2](#github-oauth2)
    - [UI Dashboard](#ui-dashboard)
      - [UI Dashboard Configurations](#ui-dashboard-configurations)
    - [API](#api)
- [Developers Guide](#developers-guide)
  - [Prerequisites](#prerequisites)
  - [Tests](#tests)
  - [Formatting](#formatting)
  - [Security Checks](#security-checks)
  - [Run application locally](#run-application-locally)
  - [Commits](#commits)
    - [Types](#types)
  - [Build docker image](#build-docker-image)
  - [Generate Changelog](#generate-changelog)
  - [Release a new Docker image](#release-a-new-docker-image)
- [Contributors](#contributors-)

## Changelog

Changelog can be found [here](https://github.com/otto-de/gitactionboard/blob/main/CHANGELOG.md)

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

| Environment variable name         | Descriptions                                                                                                                    | Required |     Default value      |          Example value          |
| :-------------------------------- | :------------------------------------------------------------------------------------------------------------------------------ | :------: | :--------------------: | :-----------------------------: |
| REPO_OWNER_NAME                   | Repository owner name. Generally, its either organization name or username                                                      |   yes    |                        |             webpack             |
| REPO_NAMES                        | List of name of repositories you want to monitor                                                                                |   yes    |                        | webpack-dev-server, webpack-cli |
| GITHUB_ACCESS_TOKEN               | Access token to fetch data from github. This is required to fetch data from a private repository when github oauth2 is disabled |    no    |                        |                                 |
| DOMAIN_NAME                       | Hostname of github                                                                                                              |    no    | https://api.github.com |                                 |
| CACHE_EXPIRES_AFTER               | Duration (in seconds) to cache the fetched data                                                                                 |    no    |           60           |                                 |
| GITHUB_OAUTH2_CLIENT_ID           | Github oauth2 client ID                                                                                                         |    no    |                        |                                 |
| GITHUB_OAUTH2_CLIENT_SECRET       | Gihub oauth2 client secret                                                                                                      |    no    |                        |                                 |
| BASIC_AUTH_USER_DETAILS_FILE_PATH | File location for basic auth user details                                                                                       |    no    |                        |         /src/.htpasswd          |

Note: To create a personal access token follow the instructions present [here](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) and choose **repo** as a scope fot this token.

##### Authentication

From v2.0.0, gitactionboard has out of the box solution for authentication. Currently, there are following authentication mechanism available

###### Basic Authentication

Basic authentication is the simplest form of authentication. In this mechanism we make use of username and password to login.

In gitactionboard, Basic Authentication can be easily setup using `BASIC_AUTH_USER_DETAILS_FILE_PATH` environment variable. This file contains
username and password in `<username>:<encrypted password using bcrypt>` format. We can make use of **Apache htpasswd** to easily create this file.
You can run the following command to create the file using CLI,

```shell
htpasswd -bnBC 10 <username> <password> >> <file location>
```

###### Github OAuth2

In gitactionboard, Github OAuth2 login can be easily setup using `GITHUB_OAUTH2_CLIENT_ID` and `GITHUB_OAUTH2_CLIENT_SECRET` environment variable.
To be able to have a valid client id and client secret from github, we need to create a Github OAuth app first. To create a gihub oauth app, please
follow [this link](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app).

**Note** you need to add _Authorization callback URL_ as `<homepage url>/login/oauth2/code/github`.

:warning: In-case of Github OAuth2 is disabled, gitactionboard will make use of `GITHUB_ACCESS_TOKEN` to fetch data from github for private repositories.

#### UI Dashboard

**Gitaction Board** has in-built UI dashboard to visualize the build status. You can access the following endpoint for the same

- `http://localhost:<host machine port>`

![UI dashboard sample 1](https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/sample1.png)

![preference sample 1](https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/preference-sample1.png)

##### UI Dashboard Configurations

From `v2.0.0` onwards, preference page can be used to manage UI dashboard configuration.

Prior to `v2.0.0`, query params can be used to configure UI dashboard. Please find possible query params below,

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
- [Node.js v16.13.1](https://nodejs.org)
- [nvm](https://github.com/nvm-sh/nvm)
- [all-contributors cli](https://allcontributors.org/docs/en/cli/overview) (only for maintainers)
- [conventional-changelog-cli](https://github.com/conventional-changelog/conventional-changelog/tree/master/packages/conventional-changelog-cli)

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

### Commits

This repository follows [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). Therefor whenever you are
committing the changes make sure use proper **type**.

#### Types

- **feat** for a new feature for the user, not a new feature for build script. Such commit will trigger a release bumping a MINOR version.
- **fix** for a bug fix for the user, not a fix to a build script. Such commit will trigger a release bumping a PATCH version.
- **perf** for performance improvements. Such commit will trigger a release bumping a PATCH version.
- **docs** for changes to the documentation.
- **style** for formatting changes, missing semicolons, etc.
- **refactor** for refactoring production code, e.g. renaming a variable.
- **test** for adding missing tests, refactoring tests; no production code change.
- **build** for updating build configuration, development tools or other changes irrelevant to the user.

> **_NOTE:_** Add ! just after type to indicate breaking changes

### Build docker image

To build docker image run:

```shell script
./run.sh docker-build
```

### Generate changelog

- The changelog can be easily generate the changelog by running following command

```shell
./run.sh generate-changelog
```

- Once changelog is generated, verify the changelog by updating the correct version and push the changes to github

### Release a new Docker image

A new docker image can be published to [docker hub](https://hub.docker.com/repository/docker/ottoopensource/gitactionboard) using CI/CD. To achieve the same we need to follow the following steps:

- Generate the changelog, if it's not done already. Steps can be found [here](#commits).

- Create a new release version by running following command

```shell
./run.sh bump-version <major|minor|patch>
```

- Push the newly created **tag** to github

```shell
git push origin "$(git describe --tags)"
```

> **_NOTE:_** We are following [semantic versioning](https://semver.org/) strategy using [io.alcide:gradle-semantic-build-versioning](https://github.com/alcideio/gradle-semantic-build-versioning) plugin

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/sumanmaity1234"><img src="https://avatars.githubusercontent.com/u/48752670?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Suman Maity</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=sumanmaity1234" title="Code">💻</a> <a href="#maintenance-sumanmaity1234" title="Maintenance">🚧</a> <a href="#ideas-sumanmaity1234" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/otto-de/gitactionboard/commits?author=sumanmaity1234" title="Documentation">📖</a> <a href="#security-sumanmaity1234" title="Security">🛡️</a></td>
    <td align="center"><a href="https://github.com/umeshnebhani733"><img src="https://avatars.githubusercontent.com/u/61968894?v=4?s=100" width="100px;" alt=""/><br /><sub><b>umeshnebhani733</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=umeshnebhani733" title="Code">💻</a> <a href="#maintenance-umeshnebhani733" title="Maintenance">🚧</a> <a href="#ideas-umeshnebhani733" title="Ideas, Planning, & Feedback">🤔</a> <a href="#security-umeshnebhani733" title="Security">🛡️</a></td>
    <td align="center"><a href="https://github.com/sankita15tw"><img src="https://avatars.githubusercontent.com/u/49052731?v=4?s=100" width="100px;" alt=""/><br /><sub><b>sankita15tw</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=sankita15tw" title="Code">💻</a> <a href="#design-sankita15tw" title="Design">🎨</a></td>
    <td align="center"><a href="https://github.com/stdogr"><img src="https://avatars.githubusercontent.com/u/61870228?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Stefan Greis</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=stdogr" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/shashikanthgadgay0804"><img src="https://avatars.githubusercontent.com/u/48748047?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Shashi</b></sub></a><br /><a href="#ideas-shashikanthgadgay0804" title="Ideas, Planning, & Feedback">🤔</a></td>
    <td align="center"><a href="https://github.com/jonasmetzger2000"><img src="https://avatars.githubusercontent.com/u/29103796?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jonas</b></sub></a><br /><a href="#ideas-jonasmetzger2000" title="Ideas, Planning, & Feedback">🤔</a></td>
    <td align="center"><a href="https://github.com/BastianSperrhacke-Otto"><img src="https://avatars.githubusercontent.com/u/62638028?v=4?s=100" width="100px;" alt=""/><br /><sub><b>BastianSperrhacke-Otto</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/issues?q=author%3ABastianSperrhacke-Otto" title="Bug reports">🐛</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
