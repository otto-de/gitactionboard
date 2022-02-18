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
