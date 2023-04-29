# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## [v3.2.1](https://github.com/otto-de/gitactionboard/compare/v3.2.0...v3.2.1) (2023-04-29)

### Bug Fixes

- Hide password on login page for basic authentication ([5a30dbc](https://github.com/otto-de/gitactionboard/commit/5a30dbce0e084d54d01780c8f5fdad31afdc50e3))

## [v3.2.0](https://github.com/otto-de/gitactionboard/compare/v3.1.1...v3.2.0) (2023-04-27)

### Features

- Introduce long-awaited dark theme ([ae0fbc1](https://github.com/otto-de/gitactionboard/commit/ae0fbc19e035630843e6a2b2a5afc0f13c09e8cb))

### Bug Fixes

- Explicitly upgrade libssl3 and libcrypto3 to fix CVE-2023-1255 ([53b5b77](https://github.com/otto-de/gitactionboard/commit/53b5b7731bd1fd835260f67423ed5f6bcac40607))

### Performance Improvements

- Enable response compression for fast page load ([da8a7e9](https://github.com/otto-de/gitactionboard/commit/da8a7e945012660b2b494bf3923a107d43c208db))

## [v3.1.1](https://github.com/otto-de/gitactionboard/compare/v3.1.0...v3.1.1) (2023-04-17)

### Bug Fixes

- Add startup failure state to run conclusion ([8b8ac04](https://github.com/otto-de/gitactionboard/commit/8b8ac046faa0b9df12e2954eea76de7679e84328)), closes [#271](https://github.com/otto-de/gitactionboard/issues/271)

## [v3.1.0](https://github.com/otto-de/gitactionboard/compare/v3.0.7...v3.1.0) (2023-04-01)

### Features

- ([#162](https://github.com/otto-de/gitactionboard/issues/162)) Display gitactionboard version on dashboard ([ad8df47](https://github.com/otto-de/gitactionboard/commit/ad8df47715e951c61f2f61215754775cd1f38d06))
- Add support to periodic scan for enabled features ([3694887](https://github.com/otto-de/gitactionboard/commit/36948878be0b3fc3afc40b8ebbc9612e63de05cc))

### Bug Fixes

- Fix CVE-2022-1471 related to snakeyaml ([1a68791](https://github.com/otto-de/gitactionboard/commit/1a687912c15593ebd6fc8e4d1c741b6fc6f131b5))
- Fix CVE-2023-0464 related to libcrypto3 and libssl3 ([03bb494](https://github.com/otto-de/gitactionboard/commit/03bb494f251d1201a01eedc9380b44fa737e817b))

## [v3.0.7](https://github.com/otto-de/gitactionboard/compare/v3.0.6...v3.0.7) (2023-03-24)

### Bug Fixes

- Fix CVE-2023-1370, CVE-2023-20860

## [v3.0.6](https://github.com/otto-de/gitactionboard/compare/v3.0.5...v3.0.6) (2023-02-15)

### Bug Fixes

- Enable CSRF protection for APIs ([204df4e](https://github.com/otto-de/gitactionboard/commit/204df4e66286af23a585f371c504feae245ba4e5))
- Fix cve related to libcrypto3 and libssl3 ([2eb1a47](https://github.com/otto-de/gitactionboard/commit/2eb1a47215f8305a49089b6c229dcce9bf4dee1c))
- Provide correct error message when REPO_OWNER_NAME config is missing ([94515a9](https://github.com/otto-de/gitactionboard/commit/94515a9074d49e4ef280cf4cbde31de4b75e5784))

## [v3.0.5](https://github.com/otto-de/gitactionboard/compare/v3.0.4...v3.0.5) (2023-01-07)

### Bug Fixes

- Upgrade libssl3 and libcrypto3 to fix CVE-2022-3996 ([a211f90](https://github.com/otto-de/gitactionboard/commit/a211f90f2191a5f6ee93bc6c3b8cd7aca5997cae))

## [v3.0.4](https://github.com/otto-de/gitactionboard/compare/v3.0.3...v3.0.4) (2022-12-01)

### Bug Fixes

- Allow everyone to access resources from /assets ([64c1e3f](https://github.com/otto-de/gitactionboard/commit/64c1e3f680e56335da8d4dc828fa0d5166a4d48a))

## [v3.0.3](https://github.com/otto-de/gitactionboard/compare/v3.0.2...v3.0.3) (2022-11-20)

### Performance Improvements

- Use custom build JRE to reduce the docker image size ([c404c21](https://github.com/otto-de/gitactionboard/commit/c404c2161e17bbbc49864a164cfe992775499aa0))

## [v3.0.2](https://github.com/otto-de/gitactionboard/compare/v3.0.1...v3.0.2) (2022-11-03)

### Bug Fixes

- Update spring security version to fix CVE-2022-31690, CVE-2022-31692 ([29177d0](https://github.com/otto-de/gitactionboard/commit/29177d059ebc2b989fe01e608e31a770fc0daa71))

## [v3.0.1](https://github.com/otto-de/gitactionboard/compare/v3.0.0...v3.0.1) (2022-10-08)

### Bug Fixes

- Update docker base image to fix CVE-2022-2097 and CVE-2022-37434 ([3b839b0](https://github.com/otto-de/gitactionboard/commit/3b839b0cb0834b8ccc9444b37e70952763bbd9ca))
- Update jackson version to fix CVE-2022-42003 ([67e5d04](https://github.com/otto-de/gitactionboard/commit/67e5d0426e7e1eb7bcb14c6c94df301f34622014))

## [v3.0.0](https://github.com/otto-de/gitactionboard/compare/v2.1.0...v3.0.0) (2022-09-21)

### ⚠ BREAKING CHANGES

- Rename /available-auths to /config endpoint for future use case
- Use /workflow-jobs as default dashboard path instead of /dashboard

### Features

- Add button to hide single workflow jobs ([#10](https://github.com/otto-de/gitactionboard/issues/10)) ([d5471db](https://github.com/otto-de/gitactionboard/commit/d5471db798db2c008d04e2b3aca775f3e32bf371))
- Display code standard violations on dashboard ([19e279d](https://github.com/otto-de/gitactionboard/commit/19e279d41d9f381875e92e93aad9d4e7d5bb8ad9))
- Display page title on the top of dashboard ([939c0ac](https://github.com/otto-de/gitactionboard/commit/939c0aca3990de0f1ac8b3d3f571e10f292a739f))
- Display secrets scan alerts on dashboard ([34e339d](https://github.com/otto-de/gitactionboard/commit/34e339d7ebda231f81de5c4f6c08080c700c4316))
- Send notification for code standard violations ([032f12a](https://github.com/otto-de/gitactionboard/commit/032f12af437b6874e4aca0fa40a6dce0fbea414a))
- Send notifications for exposed secrets ([20896e8](https://github.com/otto-de/gitactionboard/commit/20896e8008f99e7087afeb32c3655a5c1119380d))

### Bug Fixes

- Fix alignment issue with menu items ([bbb48a3](https://github.com/otto-de/gitactionboard/commit/bbb48a3d0d19b581b8b354654e0a60e7e8cd82b0))
- Update snakeyaml version to fix CVE-2022-25857 ([c803bfb](https://github.com/otto-de/gitactionboard/commit/c803bfb244ec6d55a720ea6060237d5aefdfd9c8))

### Performance Improvements

- Share sever cache acorss users ([293c043](https://github.com/otto-de/gitactionboard/commit/293c04337096922e5ec154f73e7c2631a4379bd8))

### Code Refactoring

- Rename /available-auths to /config endpoint for future use case ([96552fc](https://github.com/otto-de/gitactionboard/commit/96552fc09d385d4c0a304a43cc90b63fc0a91109))
- Use /workflow-jobs as default dashboard path instead of /dashboard ([d290650](https://github.com/otto-de/gitactionboard/commit/d290650eef9d116d041ab943e07a67e90e147191))

## [v2.1.0](https://github.com/otto-de/gitactionboard/compare/v2.0.7...v2.1.0) (2022-06-28)

### Features

- Send notification on MS Teams in case of build failure ([2f3e004](https://github.com/otto-de/gitactionboard/commit/2f3e0046cdf7df6ee19a802cf8ced2e5c607447a))

## [v2.0.7](https://github.com/otto-de/gitactionboard/compare/v2.0.6...v2.0.7) (2022-05-25)

### Bug Fixes

- Update spring dependency to fix CVE-2022-22970, CVE-2022-22978 and CVE-2022-29885 ([c632e04](https://github.com/otto-de/gitactionboard/commit/c632e0415ddb2dc9f968c6e1204feb9fa1ae48e3))

## [v2.0.6](https://github.com/otto-de/gitactionboard/compare/v2.0.5...v2.0.6) (2022-05-17)

### Bug Fixes

- Log correct information when authentication is disabled ([e492ce0](https://github.com/otto-de/gitactionboard/commit/e492ce0f7bd9b34141129fe012eeea6e0d750abb))
- Make page scrollable so that side bar is always accessible ([d5cf429](https://github.com/otto-de/gitactionboard/commit/d5cf4298bf3b10c5200d2fb230f1895e263f5faa))
- Move to amazoncorretto docker base image from openjdk to fix security vulnerabilities ([d1f7cf5](https://github.com/otto-de/gitactionboard/commit/d1f7cf5e133d514f6b64453a485851a26e4109d5))
- Update npm dependencies to fix security vulnerabilities ([534ce21](https://github.com/otto-de/gitactionboard/commit/534ce210555f3ad4b86dd5da0107e32aba8791dd))

## [v2.0.5](https://github.com/otto-de/gitactionboard/compare/v2.0.4...v2.0.5) (2022-04-25)

### Bug Fixes

- Update spring dependencies to fix CVE-2022-22968 ([0aeb21f](https://github.com/otto-de/gitactionboard/commit/0aeb21f9c0e99ea6308d9493c3f2b4a39d930f4c))

## [v2.0.4](https://github.com/otto-de/gitactionboard/compare/v2.0.3...v2.0.4) (2022-04-01)

### Bug Fixes

- Update spring boot version to fix CVE-222-22965 ([359147d](https://github.com/otto-de/gitactionboard/commit/359147dcbe97a83727ee3f9ccf16eb9b901f8434))

## [v2.0.3](https://github.com/otto-de/gitactionboard/compare/v2.0.2...v2.0.3) (2022-03-31)

### Bug Fixes

- Intermittent solution to prevent RCE with Spring Core ([52c17b5](https://github.com/otto-de/gitactionboard/commit/52c17b5220769b721058967ee723140067d9f708))
- Update dependency to fix CVE-2022-23181 ([5140721](https://github.com/otto-de/gitactionboard/commit/514072123190105fab84cbbbd30dc5b04a182744))

## [v2.0.2](https://github.com/otto-de/gitactionboard/compare/v2.0.1...v2.0.2) (2022-03-31)

### Bug Fixes

- Update dependency to fix CVE-2020-36518 ([3926e50](https://github.com/otto-de/gitactionboard/commit/3926e505b027b0bc5677b84ae1cab54bbdbe8bc0))

## [v2.0.1](https://github.com/otto-de/gitactionboard/compare/v2.0.0...v2.0.1) (2022-02-22)

### Bug Fixes

- Filter out headers with undefined value ([4800400](https://github.com/otto-de/gitactionboard/commit/4800400e284094744a9a0c124b1aa568b19fe5c4))
- Use correct title for dashboard ([abb3957](https://github.com/otto-de/gitactionboard/commit/abb3957ea389f9783cec8839684c70156a66b79a))

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
