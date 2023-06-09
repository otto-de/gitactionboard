# Gitaction Board

[![All Contributors](https://img.shields.io/badge/all_contributors-15-orange.svg?style=flat-square)](#contributors-)
[![Docker Pulls](https://img.shields.io/docker/pulls/ottoopensource/gitactionboard)](https://hub.docker.com/r/ottoopensource/gitactionboard)
[![Version](https://img.shields.io/docker/v/ottoopensource/gitactionboard?sort=semver)](https://hub.docker.com/r/ottoopensource/gitactionboard)
![Docker Image Size](https://img.shields.io/docker/image-size/ottoopensource/gitactionboard?label=docker%20image%20size&sort=semver)
![Release Date](https://img.shields.io/github/release-date/otto-de/gitactionboard)

Gitaction Board - Ultimate Dashboard for GithubActions.

# Table of contents

- [Demo](#demo)
- [Changelog](#changelog)
- [Contributing](#contributing)
- [Features](#features)
- [Usage](#usage)
  - [Pull docker image](#pull-docker-image)
  - [Run docker image](#run-docker-image)
    - [Configurations](#configurations)
      - [Authentication](#authentication)
        - [Basic Authentication](#basic-authentication)
        - [GitHub OAuth2](#github-oauth2)
    - [UI Dashboard](#ui-dashboard)
      - [UI Dashboard Configurations](#ui-dashboard-configurations)
    - [API](#api)
      - [Workflows Data in CCtray XML format](#workflows-data-in-cctray-xml-format)
      - [Workflows Data in CCtray JSON format](#workflows-data-in-cctray-json-format)
      - [Secrets Scan Alerts Data in JSON format](#secrets-scan-alerts-data-in-json-format)
      - [Code Scan Alerts Data in JSON format](#code-scan-alerts-data-in-json-format)
- [Contributors ✨](#contributors-)

## Demo

Check [this link](https://otto-de.github.io/gitactionboard/) to try out **GitactionBoard** online with mock data.

:warning: Due to technical reason authentication is disabled on the demo website.

## Changelog

Changelog can be found [here](https://github.com/otto-de/gitactionboard/blob/main/CHANGELOG.md).

## Contributing

To contribute to GitactionBoard, have a look at our [contributing guidelines](https://github.com/otto-de/gitactionboard/blob/main/CONTRIBUTING.md).

## Features

- Monitor workflow run status across multiple public and private repositories
- Monitor secret scan alerts across multiple repositories
- Monitor code scan alerts across multiple repositories
- Show/hide specific workflow job status from UI
- Send alerts to [MS Teams](https://www.microsoft.com/en/microsoft-teams/group-chat-software) in case of failures
- Get workflow run details across multiple repositories in [CCTray](https://cctray.org/v1/) XML and JSON format
- Get secret scan alerts across multiple repositories in JSON format
- Get code scanning alerts across multiple repositories in JSON format
- Hide all healthy builds from build monitor
- In-built basic auth and GitHub OAuth2 login mechanism
- Cache GitHub API response for configured time to avoid rate limit issue
- Supports Dark and Light theme

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
  -e REPO_NAMES=<repo names as comma separated value> \
  -it ottoopensource/gitactionboard:<docker tag>

# example:
#docker run \
#--env REPO_OWNER_NAME=webpack \
#--env REPO_NAMES=webpack-cli,webpack-dev-server \
#-p 8080:8080 \
#-it ottoopensource/gitactionboard:latest
```

#### Configurations

| Environment variable name                    | Descriptions                                                                                                                                                                                                                                                     | Required |     Default value      |          Example value          |
| :------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------: | :--------------------: | :-----------------------------: |
| REPO_OWNER_NAME                              | Repository owner name. Generally, its either organization name or username                                                                                                                                                                                       |   yes    |                        |             webpack             |
| REPO_NAMES                                   | List of name of repositories you want to monitor                                                                                                                                                                                                                 |   yes    |                        | webpack-dev-server, webpack-cli |
| GITHUB_ACCESS_TOKEN                          | Access token to fetch data from github. This is required to fetch data from a private repository when github oauth2 is disabled                                                                                                                                  |    no    |                        |                                 |
| DOMAIN_NAME                                  | Hostname of github                                                                                                                                                                                                                                               |    no    | https://api.github.com |                                 |
| CACHE_EXPIRES_AFTER                          | Duration (in seconds) to cache the fetched data                                                                                                                                                                                                                  |    no    |           60           |                                 |
| GITHUB_OAUTH2_CLIENT_ID                      | GitHub oauth2 client ID                                                                                                                                                                                                                                          |    no    |                        |                                 |
| GITHUB_OAUTH2_CLIENT_SECRET                  | Github oauth2 client secret                                                                                                                                                                                                                                      |    no    |                        |                                 |
| BASIC_AUTH_USER_DETAILS_FILE_PATH            | File location for basic auth user details                                                                                                                                                                                                                        |    no    |                        |         /src/.htpasswd          |
| MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL          | Web hook url to send build failure notifications on Microsoft Teams (_available from v2.1.0_)                                                                                                                                                                    |    no    |                        |                                 |
| ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING | Display GitHub secret scan alerts on dashboard (_available from v3.0.0_)                                                                                                                                                                                         |    no    |         false          |              true               |
| ENABLE_GITHUB_CODE_SCAN_ALERTS_MONITORING    | Display code standard violations on dashboard (_available from v3.0.0_)                                                                                                                                                                                          |    no    |         false          |              true               |
| PERIODIC_SCAN_CRON_SCHEDULE                  | Enable periodic scan using cron schedule (`<second> <minute> <hour> <day of month> <month> <day of week>`) for enabled feature. (_available from v3.1.0_) </br> :warning: _GITHUB_ACCESS_TOKEN_ is required for private repos even if you are using github login |    no    |                        |       0 \*/30 3-14 \* 1-5       |

###### Notes

- To create a personal access token follow the instructions present [here](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) and choose **repo** as a scope fot this token.
- To create incoming webhook connection for MS Teams channel follow the instructions present [here](https://docs.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook#create-an-incoming-webhook-1).

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

###### GitHub OAuth2

In gitactionboard, GitHub OAuth2 login can be easily setup using `GITHUB_OAUTH2_CLIENT_ID` and `GITHUB_OAUTH2_CLIENT_SECRET` environment variable.
To be able to have a valid client id and client secret from GitHub, we need to create a GitHub OAuth app first. To create a GitHub oauth app, please
follow [this link](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app).

**Note** you need to add _Authorization callback URL_ as `<homepage url>/login/oauth2/code/github`.

:warning: In-case the gitactionboard server is running behind a proxy, you need to set the above _Authorization callback URL_ to `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_REDIRECT-URI` environment variable.

:warning: In-case of GitHub OAuth2 is disabled, gitactionboard will make use of `GITHUB_ACCESS_TOKEN` to fetch data from GitHub for private repositories.

#### UI Dashboard

**Gitaction Board** has in-built UI dashboard to visualize the build status. You can access the following endpoint for the same

- `http://localhost:<host machine port>`

|                                                      Dark Theme                                                       |                                                      Light Theme                                                       |
| :-------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------: |
|          <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/dark-theme/login.png">           |          <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/light-theme/login.png">           |
|         <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/dark-theme/workflow.png">         |         <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/light-theme/workflow.png">         |
| <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/dark-theme/code-standard-violations.png"> | <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/light-theme/code-standard-violations.png"> |
|     <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/dark-theme/exposed-secrets.png">      |     <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/light-theme/exposed-secrets.png">      |
|       <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/dark-theme/preferences.png">        |       <img src="https://raw.githubusercontent.com/otto-de/gitactionboard/main/doc/light-theme/preferences.png">        |

##### UI Dashboard Configurations

From `v2.0.0` onwards, preference page can be used to manage UI dashboard configuration.

Prior to `v2.0.0`, query params can be used to configure UI dashboard. Please find possible query params below,

| Query param name      | Descriptions                                                                                              | Required | Default value | Example value |
| :-------------------- | :-------------------------------------------------------------------------------------------------------- | :------: | :-----------: | :-----------: |
| hide-healthy          | Hide all the healthy builds from the dashboard                                                            |    no    |     false     |     true      |
| max-idle-time         | Configure the max idle time (in minutes), after the given time background polling for dashboard will stop |    no    |       5       |       2       |
| disable-max-idle-time | Disable background polling optimisation configured using **max-idle-time**                                |    no    |     false     |     true      |

#### API

Once server is up, you can access the following endpoints

##### Workflows Data in CCtray XML format

Access `http://localhost:<host machine port>/v1/cctray.xml` to get data in **XML** format

###### Sample response

```xml
<Projects>
    <Project name="hello-world :: hello-world-build-and-deployment :: talisman-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:11:41Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386046" triggeredEvent="push"/>
    <Project name="hello-world :: hello-world-build-and-deployment :: dependency-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127" triggeredEvent="schedule"/>
    <Project name="hello-world :: hello-world-checks :: format" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:11:41Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386046" triggeredEvent="pull_request"/>
    <Project name="hello-world :: hello-world-checks :: test" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127" triggeredEvent="push"/>
</Projects>
```

##### Workflows Data in CCtray JSON format

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
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386046",
    "triggeredEvent": "push"
  },
  {
    "name": "hello-world :: hello-world-build-and-deployment :: dependency-checks",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:14:54.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386127",
    "triggeredEvent": "schedule"
  },
  {
    "name": "hello-world :: hello-world-checks :: format",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:11:41.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386046",
    "triggeredEvent": "pull_request"
  },
  {
    "name": "hello-world :: hello-world-checks :: test",
    "activity": "Sleeping",
    "lastBuildStatus": "Success",
    "lastBuildLabel": "206",
    "lastBuildTime": "2020-09-18T06:14:54.000Z",
    "webUrl": "https://github.com/johndoe/hello-world/runs/1132386127",
    "triggeredEvent": "push"
  }
]
```

##### Secrets Scan Alerts Data in JSON format

Access `http://localhost:<host machine port>/v1/alerts/secrets` to get security scan alerts data in **JSON** format

###### Sample response

```json
[
  {
    "id": "hello-world::Amazon AWS Secret Access Key::3",
    "name": "hello-world :: Amazon AWS Secret Access Key",
    "url": "https://github.com/johndoe/hello-world/security/secret-scanning/3",
    "createdAt": "2022-07-26T11:12:02.000Z"
  },
  {
    "id": "hello-world::Amazon AWS Access Key ID::2",
    "name": "hello-world :: Amazon AWS Access Key ID",
    "url": "https://github.com/johndoe/hello-world/security/secret-scanning/2",
    "createdAt": "2022-07-26T09:39:07.000Z"
  },
  {
    "id": "hello-world::Amazon AWS Secret Access Key::1",
    "name": "hello-world :: Amazon AWS Secret Access Key",
    "url": "https://github.com/johndoe/hello-world/security/secret-scanning/1",
    "createdAt": "2022-07-26T09:39:07.000Z"
  }
]
```

##### Code Scan Alerts Data in JSON format

Access `http://localhost:<host machine port>/v1/alerts/code-standard-violations` to get code standard violation alerts data in **JSON** format

###### Sample response

```json
[
  {
    "id": "hello-world::Arbitrary file write during zip extraction::4",
    "name": "hello-world :: api-session-spec.ts:917-917 :: Arbitrary file write during zip extraction",
    "url": "https://github.com/johndoe/hello-world/code-scanning/4",
    "createdAt": "2020-02-13T12:29:18.000Z"
  },
  {
    "id": "hello-world::Redundant else condition::3",
    "name": "hello-world :: api-session-spec.ts:918-918 :: Redundant else condition",
    "url": "https://github.com/johndoe/hello-world/code-scanning/3",
    "createdAt": "2020-02-12T12:29:18.000Z"
  }
]
```

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/sumanmaity1234"><img src="https://avatars.githubusercontent.com/u/48752670?v=4?s=100" width="100px;" alt="Suman Maity"/><br /><sub><b>Suman Maity</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=sumanmaity1234" title="Code">💻</a> <a href="#maintenance-sumanmaity1234" title="Maintenance">🚧</a> <a href="#ideas-sumanmaity1234" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/otto-de/gitactionboard/commits?author=sumanmaity1234" title="Documentation">📖</a> <a href="#security-sumanmaity1234" title="Security">🛡️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/umeshnebhani733"><img src="https://avatars.githubusercontent.com/u/61968894?v=4?s=100" width="100px;" alt="umeshnebhani733"/><br /><sub><b>umeshnebhani733</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=umeshnebhani733" title="Code">💻</a> <a href="#maintenance-umeshnebhani733" title="Maintenance">🚧</a> <a href="#ideas-umeshnebhani733" title="Ideas, Planning, & Feedback">🤔</a> <a href="#security-umeshnebhani733" title="Security">🛡️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/sankita15tw"><img src="https://avatars.githubusercontent.com/u/49052731?v=4?s=100" width="100px;" alt="sankita15tw"/><br /><sub><b>sankita15tw</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=sankita15tw" title="Code">💻</a> <a href="#design-sankita15tw" title="Design">🎨</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/stdogr"><img src="https://avatars.githubusercontent.com/u/61870228?v=4?s=100" width="100px;" alt="Stefan Greis"/><br /><sub><b>Stefan Greis</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=stdogr" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/shashikanthgadgay0804"><img src="https://avatars.githubusercontent.com/u/48748047?v=4?s=100" width="100px;" alt="Shashi"/><br /><sub><b>Shashi</b></sub></a><br /><a href="#ideas-shashikanthgadgay0804" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jonasmetzger2000"><img src="https://avatars.githubusercontent.com/u/29103796?v=4?s=100" width="100px;" alt="Jonas"/><br /><sub><b>Jonas</b></sub></a><br /><a href="#ideas-jonasmetzger2000" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/BastianSperrhacke-Otto"><img src="https://avatars.githubusercontent.com/u/62638028?v=4?s=100" width="100px;" alt="BastianSperrhacke-Otto"/><br /><sub><b>BastianSperrhacke-Otto</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/issues?q=author%3ABastianSperrhacke-Otto" title="Bug reports">🐛</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/sweiler"><img src="https://avatars.githubusercontent.com/u/9385626?v=4?s=100" width="100px;" alt="Simon Weiler"/><br /><sub><b>Simon Weiler</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=sweiler" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/apps/dependabot"><img src="https://avatars.githubusercontent.com/in/29110?v=4?s=100" width="100px;" alt="dependabot[bot]"/><br /><sub><b>dependabot[bot]</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=dependabot[bot]" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/baztian"><img src="https://avatars.githubusercontent.com/u/2461869?v=4?s=100" width="100px;" alt="baztian"/><br /><sub><b>baztian</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/issues?q=author%3Abaztian" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://blog.ogbes.me/"><img src="https://avatars.githubusercontent.com/u/70475985?v=4?s=100" width="100px;" alt="Ogbeide Godstime Osemenkhian"/><br /><sub><b>Ogbeide Godstime Osemenkhian</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/issues?q=author%3Agtogbes" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/svenfinke"><img src="https://avatars.githubusercontent.com/u/564499?v=4?s=100" width="100px;" alt="Sven Finke"/><br /><sub><b>Sven Finke</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=svenfinke" title="Code">💻</a> <a href="#ideas-svenfinke" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/valentin-krasontovitsch"><img src="https://avatars.githubusercontent.com/u/15932677?v=4?s=100" width="100px;" alt="Valentin Krasontovitsch"/><br /><sub><b>Valentin Krasontovitsch</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/commits?author=valentin-krasontovitsch" title="Code">💻</a> <a href="https://github.com/otto-de/gitactionboard/issues?q=author%3Avalentin-krasontovitsch" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://bennet-schulz.com/"><img src="https://avatars.githubusercontent.com/u/8372856?v=4?s=100" width="100px;" alt="Bennet Schulz"/><br /><sub><b>Bennet Schulz</b></sub></a><br /><a href="#ideas-bennetelli" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/pbonner-1"><img src="https://avatars.githubusercontent.com/u/71049471?v=4?s=100" width="100px;" alt="Phillip Bonner"/><br /><sub><b>Phillip Bonner</b></sub></a><br /><a href="https://github.com/otto-de/gitactionboard/issues?q=author%3Apbonner-1" title="Bug reports">🐛</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
