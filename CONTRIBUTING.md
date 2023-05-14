# Contributing to GitactionBoard

# Table of contents

- [How to start contributing](#how-to-start-contributing)
  - [Prerequisites](#prerequisites)
  - [Tests](#tests)
  - [Formatting](#formatting)
  - [Security Checks](#security-checks)
  - [Run application locally](#run-application-locally)
    - [Run backend with frontend](#run-backend-with-frontend)
    - [Run only backend](#run-only-backend)
    - [Run only frontend with mock data](#run-only-frontend-with-mock-data)
  - [Commits](#commits)
    - [Types](#types)
  - [Build docker image](#build-docker-image)
- [Submitting a Pull Request](#submitting-a-pull-request)
  - [Your PR is merged!](#your-pr-is-merged)
- [Release new version](#release-new-version)
  - [Generate changelog](#generate-changelog)
  - [Release a new Docker image](#release-a-new-docker-image)

## How to start contributing

If you are not sure how to begin contributing to GitactionBoard, have a look at the issues tagged under [good first issue](https://github.com/otto-de/gitactionboard/labels/good%20first%20issue).

### Prerequisites

- [jEnv](https://www.jenv.be/)
- Java 17
- [Docker](https://www.docker.com/)
- [Hadolint](https://github.com/hadolint/hadolint)
- [ShellCheck](https://www.shellcheck.net/)
- [Prettier](https://prettier.io/)
- [talisman](https://github.com/thoughtworks/talisman)
- [Trivy](https://github.com/aquasecurity/trivy)
- [Node.js v18.12.1](https://nodejs.org)
- [nvm](https://github.com/nvm-sh/nvm)
- [all-contributors cli](https://allcontributors.org/docs/en/cli/overview) (only for maintainers)

### Tests

Tests are separated into unit and integration test sets. To denote an integration test it needs to be annotated
as `@IntegrationTest`.

To run only backend unit tests:

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

To run all backend verifications:

```shell script
./run.sh backend-test
```

To run all frontend verifications:

```shell script
./run.sh frontend-test
```

To run the [mutation tests](https://pitest.org/) for backend:

```shell script
./run.sh pitest
```

Test sets are runs in order, with unit tests first, followed by integration tests.
Should there be a failure in the unit tests the task execution stops there, e.g.
a prerequisite for running integration tests will be working unit tests.

### Formatting

This project uses the following tools to follow specific style guide

- [spotless](https://github.com/diffplug/spotless) for **Java** files
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
- [talisman](https://github.com/thoughtworks/talisman) to validate the outgoing changeset for things that look suspicious - such as authorization tokens and private keys
- [Trivy](https://github.com/aquasecurity/trivy) to find vulnerabilities in Docker image

To run OWASP dependency check:

```shell script
./run.sh check
```

To run Trivy check:

```shell
./run.sh trivy-verify
```

### Run application locally

This service can be run locally.

#### Run backend with frontend

To run the application with complete backend and frontend locally, run the following command:

```shell script
./run.sh run-locally <github auth token>
```

#### Run only backend

To run the only backend application, run the following command:

```shell script
./run.sh run-locally <github auth token> false
```

#### Run only frontend with mock data

This application make uses of [mockoon](https://mockoon.com/) to start mock api server needed for local frontend development.

To run the only frontend application with mock api data, run the following command:

```shell script
./run.sh run-frontend-locally
```

### Commits

This repository follows [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). Therefor whenever you are
committing the changes make sure to use proper **type**.

#### Types

- **feat** for a new feature for the user, not a new feature for build script. Such commit will trigger a release bumping a MINOR version.
- **fix** for a bug fix for the user, not a fix to a build script. Such commit will trigger a release bumping a PATCH version.
- **chore** for Update something without impacting the user (ex: bump a dependency in package.json).
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

## Submitting a Pull Request

To send in a pull request

1. Fork the repo.
2. Create a new feature branch based off the main branch.
3. Provide the commit message with the issue number and a proper description as per [commits style](#commits).
4. Ensure that all the tests pass.
5. Submit the pull request.

### Your PR is merged!

Congratulations :tada::tada: The GitactionBoard team thanks you :sparkles:.

Once your PR is merged, your contributions will be publicly visible on the [Readme](/README.md).

## Release new version

### Generate changelog

- The changelog can be easily generate the changelog by running following command

```shell
./run.sh generate-changelog
```

### Release a new Docker image

A new docker image can be published to [docker hub](https://hub.docker.com/repository/docker/ottoopensource/gitactionboard) using CI/CD. To achieve the same we need to follow the following steps:

- Create a new release version by running following command

```shell
./run.sh bump-version
```

- Push the newly created **tag** to GitHub

```shell
git push origin "$(git describe --tags)"
```

> **_NOTE:_** We are following [semantic versioning](https://semver.org/) strategy using [io.alcide:gradle-semantic-build-versioning](https://github.com/alcideio/gradle-semantic-build-versioning) plugin
