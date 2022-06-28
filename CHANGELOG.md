# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## [v2.0.0](https://github.com/otto-de/gitactionboard/compare/v1.1.2...v2.0.0) (2022-02-18)

### ⚠ BREAKING CHANGES

- Remove ability to configure dashboard using query params

### Features

- Allow guest user to fetch files from /img folder ([9e759ff](https://github.com/otto-de/gitactionboard/commit/9e759ffa74d1933b1fd3425995b052124d2a8393))
- Allow only authenticate user to access private pages when authentication is enabled or unknown ([990ccb9](https://github.com/otto-de/gitactionboard/commit/990ccb9e74c286fc6106c7c2a9971ab74c3004a4))
- Clear cookies when user click on logout ([9b7b0ee](https://github.com/otto-de/gitactionboard/commit/9b7b0ee19289e2c4fd6fb798fe7758e4809477ca))
- Display error message if login credentials are wrong ([e6cf144](https://github.com/otto-de/gitactionboard/commit/e6cf144af403e3681afcd490cd01ea9f2150dee3))
- Display happy octopus when there is no failed build and user doesn't want to see healthy build ([670034b](https://github.com/otto-de/gitactionboard/commit/670034b5a865a640a0cb14f51e327f6fcacbdeac))
- Display spinner till page is fully loaded ([4ebd072](https://github.com/otto-de/gitactionboard/commit/4ebd0726c80b276b7d4b991c61ca7841f6d4697b))
- Explicitly delete access_token cookie on logout ([b52caa3](https://github.com/otto-de/gitactionboard/commit/b52caa3e7b0c2bbdee4d300cfca8b20eeffda10c))
- Hide logout button for guest users ([a7e1c22](https://github.com/otto-de/gitactionboard/commit/a7e1c22c13b1de30bfa1046bbea637534079458a))
- Introduce basic authentication ([abfdeab](https://github.com/otto-de/gitactionboard/commit/abfdeabdae2caff3544748716cdc4e30c14abe8b))
- Introduce login page ([6fe267f](https://github.com/otto-de/gitactionboard/commit/6fe267f26689415d4eec8293251680e44813feca))
- Remove ability to configure dashboard using query params ([ea53408](https://github.com/otto-de/gitactionboard/commit/ea5340810820205648f60d0a4c305c6f1577e259))
- Respect servlet context config while serving resources ([3a0a215](https://github.com/otto-de/gitactionboard/commit/3a0a21589f6bd6f8e848d534e0289beed7bbaa5b))
- Use client token to fetch workflow details ([e769e77](https://github.com/otto-de/gitactionboard/commit/e769e77bbf9e0e3e4e812f8a6bf1d80616f587d3))

### Bug Fixes

- Clear interval timer when user moves away from dashboard page ([2dea455](https://github.com/otto-de/gitactionboard/commit/2dea455a0db46305a7bf0ab5aab89495713f03e9))

## [v2.0.1](https://github.com/otto-de/gitactionboard/compare/v2.0.0...v2.0.1) (2022-02-22)

### Bug Fixes

- Filter out headers with undefined value ([4800400](https://github.com/otto-de/gitactionboard/commit/4800400e284094744a9a0c124b1aa568b19fe5c4))
- Use correct title for dashboard ([abb3957](https://github.com/otto-de/gitactionboard/commit/abb3957ea389f9783cec8839684c70156a66b79a))

## [v2.0.2](https://github.com/otto-de/gitactionboard/compare/v2.0.1...v2.0.2) (2022-03-31)

### Bug Fixes

- Update dependency to fix CVE-2020-36518 ([3926e50](https://github.com/otto-de/gitactionboard/commit/3926e505b027b0bc5677b84ae1cab54bbdbe8bc0))

## [v2.0.3](https://github.com/otto-de/gitactionboard/compare/v2.0.2...v2.0.3) (2022-03-31)

### Bug Fixes

- Intermittent solution to prevent RCE with Spring Core ([52c17b5](https://github.com/otto-de/gitactionboard/commit/52c17b5220769b721058967ee723140067d9f708))
- Update dependency to fix CVE-2022-23181 ([5140721](https://github.com/otto-de/gitactionboard/commit/514072123190105fab84cbbbd30dc5b04a182744))

## [v2.0.4](https://github.com/otto-de/gitactionboard/compare/v2.0.3...v2.0.4) (2022-04-01)

### Bug Fixes

- Update spring boot version to fix CVE-222-22965 ([359147d](https://github.com/otto-de/gitactionboard/commit/359147dcbe97a83727ee3f9ccf16eb9b901f8434))

## [v2.0.5](https://github.com/otto-de/gitactionboard/compare/v2.0.4...v2.0.5) (2022-04-25)

### Bug Fixes

- Update spring dependencies to fix CVE-2022-22968 ([0aeb21f](https://github.com/otto-de/gitactionboard/commit/0aeb21f9c0e99ea6308d9493c3f2b4a39d930f4c))

## [v2.0.6](https://github.com/otto-de/gitactionboard/compare/v2.0.5...v2.0.6) (2022-05-17)

### Bug Fixes

- Log correct information when authentication is disabled ([e492ce0](https://github.com/otto-de/gitactionboard/commit/e492ce0f7bd9b34141129fe012eeea6e0d750abb))
- Make page scrollable so that side bar is always accessible ([d5cf429](https://github.com/otto-de/gitactionboard/commit/d5cf4298bf3b10c5200d2fb230f1895e263f5faa))
- Move to amazoncorretto docker base image from openjdk to fix security vulnerabilities ([d1f7cf5](https://github.com/otto-de/gitactionboard/commit/d1f7cf5e133d514f6b64453a485851a26e4109d5))
- Update npm dependencies to fix security vulnerabilities ([534ce21](https://github.com/otto-de/gitactionboard/commit/534ce210555f3ad4b86dd5da0107e32aba8791dd))

## [v2.0.7](https://github.com/otto-de/gitactionboard/compare/v2.0.6...v2.0.7) (2022-05-25)

### Bug Fixes

- Update spring dependency to fix CVE-2022-22970, CVE-2022-22978 and CVE-2022-29885 ([c632e04](https://github.com/otto-de/gitactionboard/commit/c632e0415ddb2dc9f968c6e1204feb9fa1ae48e3))

## [v2.1.0](https://github.com/otto-de/gitactionboard/compare/v2.0.7...v2.1.0) (2022-06-28)

### Features

- Send notification on MS Teams in case of build failure ([2f3e004](https://github.com/otto-de/gitactionboard/commit/2f3e0046cdf7df6ee19a802cf8ced2e5c607447a))
