# Changelog

All notable changes to this project will be documented in this file.

## [v3.3.2](https://github.com/otto-de/gitactionboard/compare/v3.3.1...v3.3.2) (2023-06-10)

### 🐛 Bug Fixes

- Build filter on preference page while updating other configs ([ff7f352](https://github.com/otto-de/gitactionboard/commit/ff7f352bdc53fa292ad00de5e3aa77c40860cd7e))

- Fix typo for automatic page refresh interval ([d2ccca4](https://github.com/otto-de/gitactionboard/commit/d2ccca4e2ac9216b1d80a8e917144c70571a61ee))

### 🧪 Testing

- ([#9](https://github.com/otto-de/gitactionboard/issues/9)) Add missing tests for LoginPage, Job, NoFailures and MaxIdleTimeoutOverlay component ([66cba1f](https://github.com/otto-de/gitactionboard/commit/66cba1f309ac2f660fb6005b97cf862907a32377))

### 👷 Build

- Generate changelog url correctly ([f12fdfa](https://github.com/otto-de/gitactionboard/commit/f12fdfa3eb70723f7a91c75a056a91090e312da5))

### 📚 Documentation

- Update changelog ([367d53d](https://github.com/otto-de/gitactionboard/commit/367d53d89bda88e7a88966a1a00f5450e46b6a35))

## [v3.3.1](https://github.com/otto-de/gitactionboard/compare/v3.3.0...v3.3.1) (2023-06-09)

### 🐛 Bug Fixes

- ([#344](https://github.com/otto-de/gitactionboard/issues/344)) Build filter on preference page ([6dcf5a2](https://github.com/otto-de/gitactionboard/commit/6dcf5a23a441cdbf4910b21e61cc0d295e9a67eb))

### 🛡️ Security

- Explicitly upgrade libssl3 and libcrypto3 for fix [CVE-2023-2650](https://nvd.nist.gov/vuln/detail/CVE-2023-2650) ([1d2ddbd](https://github.com/otto-de/gitactionboard/commit/1d2ddbdadc3b7272f38b3eab1ab0c7d3bf18f31f))

### 🚜 Refactor

- Generalise grid cell ([cbf7105](https://github.com/otto-de/gitactionboard/commit/cbf710581729108890e976d12ed5c92cd0dc5c6c))

### 🎨 Styling

- Display relative time hint and hide icon in white color for light theme ([003de60](https://github.com/otto-de/gitactionboard/commit/003de60f6e8cb0a8294c687275a41a6e1295110d))

### ⚙️ Miscellaneous Tasks

- Bump dependabot/fetch-metadata from 1.4.0 to 1.5.1 ([aac47a3](https://github.com/otto-de/gitactionboard/commit/aac47a3fd295492372e0d40467c3a03244e23be2))

### 📚 Documentation

- Update changelog ([0af1ac1](https://github.com/otto-de/gitactionboard/commit/0af1ac1bd671c142bc27e78ebca55a89adaaa60c))

- Update sample screenshots ([f0b99e9](https://github.com/otto-de/gitactionboard/commit/f0b99e911e0e730448d390d57e181893b77f6939))

- Update sample screenshots ([05b57fa](https://github.com/otto-de/gitactionboard/commit/05b57fa67afff12bfe5bdfd04a696d8fd14b9249))

- Add @pbonner-1 to contributors list ([a3bbcb6](https://github.com/otto-de/gitactionboard/commit/a3bbcb6fdcfb7676dfe6dcb67e78c81695574dcd))

## [v3.3.0](https://github.com/otto-de/gitactionboard/compare/v3.2.1...v3.3.0) (2023-05-20)

### ⛰️ Features

- ([#216](https://github.com/otto-de/gitactionboard/issues/216)) Enrich in cctray json and xml response with triggeredEvent ([6ae5f0d](https://github.com/otto-de/gitactionboard/commit/6ae5f0daae455066f1b8e62679dcffb1f1502c3c))

- ([#216](https://github.com/otto-de/gitactionboard/issues/216)) Introduce possibility to filter builds based on events ([58d4595](https://github.com/otto-de/gitactionboard/commit/58d4595bc634974eb23c2c6e022f26612b18a6c5))

- Display relative time difference for failed workflow jobs ([d70573f](https://github.com/otto-de/gitactionboard/commit/d70573f2ad14b7731fef47b90622a53365202770))

- Display relative time difference for exposed secrets and code stadard violation dashboard ([73dda74](https://github.com/otto-de/gitactionboard/commit/73dda748538b411493694dd566b698a391b6f7ea))

### 🐛 Bug Fixes

- Add missed GitHub merge_group and schedule event ([a49ddc1](https://github.com/otto-de/gitactionboard/commit/a49ddc1bbd2c6e3cbe49fbc96b69ce1ba9334fd1))

### 🛡️ Security

- Use alpine 3.18.0 to fix cves related to libssl and libcrypto ([74f8a23](https://github.com/otto-de/gitactionboard/commit/74f8a23bddcf7f4ca5da5654576d1503cc7b83dc))

- Provide link for CVE on changelog file ([3f220bb](https://github.com/otto-de/gitactionboard/commit/3f220bba7c709ab55d259f4d79857bde61cb837e))

### ⚙️ Miscellaneous Tasks

- Deploy demo website to GitHub pages ([86b239f](https://github.com/otto-de/gitactionboard/commit/86b239f1694ac899cac3b2da0b378cddb6a05935))

- Update vuetify and jsdom to latest version ([6a800ec](https://github.com/otto-de/gitactionboard/commit/6a800eca69205a54a53265c12522b221c49c1e4e))

- Cleanup unnecessary fixtures ([9427137](https://github.com/otto-de/gitactionboard/commit/94271379535b5ea1f35b590885cd628a7543c58f))

### 👷 Build

- Deploy to GitHub pages only when new version is released on docker ([e719661](https://github.com/otto-de/gitactionboard/commit/e7196615d46c1d1ef1da5f20d9fcba97120e9c3b))

- Use git-cliff to generate changelog as part of CI process ([8d367d5](https://github.com/otto-de/gitactionboard/commit/8d367d5d14b9ed5801873d0accb87fbf796ea933))

- Update git cliff config to skip talisman suppression commits ([c8aed12](https://github.com/otto-de/gitactionboard/commit/c8aed12401ef10e61cd0d8fa1c903506f45fb6b2))

- Add write permission for update_changelog job ([ac7ef43](https://github.com/otto-de/gitactionboard/commit/ac7ef43f7641e64333d131bd9d283a69add196b9))

- Clone main branch with all git history while generating changelog ([0dead73](https://github.com/otto-de/gitactionboard/commit/0dead73a2de1a9d31eed0f1429c3477e24b6a75b))

### 📚 Documentation

- Add demo link on Readme ([a89d77a](https://github.com/otto-de/gitactionboard/commit/a89d77aaf040f4f90669ee38696864bf35c73b8d))

- Add @bennetelli to contributors list for ideas ([a36fc32](https://github.com/otto-de/gitactionboard/commit/a36fc32418b0f6dfcd06603527ffb207e9e6d034))

## [v3.2.1](https://github.com/otto-de/gitactionboard/compare/v3.2.0...v3.2.1) (2023-04-29)

### 🐛 Bug Fixes

- Hide password on login page for basic authentication ([5a30dbc](https://github.com/otto-de/gitactionboard/commit/5a30dbce0e084d54d01780c8f5fdad31afdc50e3))

### ⚙️ Miscellaneous Tasks

- Update @mockoon/cli and vuetify to latest version ([67501a7](https://github.com/otto-de/gitactionboard/commit/67501a7f7f7d838dc71ca80610c816418f5523eb))

### 📚 Documentation

- Update sample image for docs ([54c8f67](https://github.com/otto-de/gitactionboard/commit/54c8f677366ba59484325f3a5e7d9a9d244f6a44))

- Update changelog for v3.2.1 ([56d0c95](https://github.com/otto-de/gitactionboard/commit/56d0c9530ff4dc7048dc743d60fb993d378728e6))

## [v3.2.0](https://github.com/otto-de/gitactionboard/compare/v3.1.1...v3.2.0) (2023-04-27)

### ⛰️ Features

- Introduce long-awaited dark theme ([ae0fbc1](https://github.com/otto-de/gitactionboard/commit/ae0fbc19e035630843e6a2b2a5afc0f13c09e8cb))

### 🛡️ Security

- Remove [CVE-2022-1471](https://nvd.nist.gov/vuln/detail/CVE-2022-1471) from dependency check suppression file ([c865114](https://github.com/otto-de/gitactionboard/commit/c865114d9bb64c1760981da95209a56a95acebc2))

- Explicitly upgrade libssl3 and libcrypto3 for fix [CVE-2023-1255](https://nvd.nist.gov/vuln/detail/CVE-2023-1255) ([53b5b77](https://github.com/otto-de/gitactionboard/commit/53b5b7731bd1fd835260f67423ed5f6bcac40607))

### ⚡ Performance

- Enable response compression for fast page load ([da8a7e9](https://github.com/otto-de/gitactionboard/commit/da8a7e945012660b2b494bf3923a107d43c208db))

### 🎨 Styling

- Revamp UI ([257c09d](https://github.com/otto-de/gitactionboard/commit/257c09d901fdc0e67cd4d425a8e25a82d7b2eae8))

### 🧪 Testing

- Mock authentication with mockoon when running only frontend locally ([1efa6df](https://github.com/otto-de/gitactionboard/commit/1efa6dfb5eb6d687c2d8cbce31056a5fe157c72c))

### ⚙️ Miscellaneous Tasks

- Bump dependabot/fetch-metadata from 1.3.6 to 1.4.0 ([b877470](https://github.com/otto-de/gitactionboard/commit/b877470c37e07aeef121b1494c2410a422219454))

- Upgrade gradle to v8.1.1 ([b3330d6](https://github.com/otto-de/gitactionboard/commit/b3330d629b56e887f20fe6bc9dd07dac8628fac1))

### 📚 Documentation

- Add @svenfinke for ideas ([f459c20](https://github.com/otto-de/gitactionboard/commit/f459c20af182f9e8343d94466401a4633210c1ce))

- Update changelog for v3.2.0 ([8bffbc1](https://github.com/otto-de/gitactionboard/commit/8bffbc152b55b686a24ed0b534c2195baac16e68))

## [v3.1.1](https://github.com/otto-de/gitactionboard/compare/v3.1.0...v3.1.1) (2023-04-17)

### 🐛 Bug Fixes

- Add startup failure state to run conclusion ([8b8ac04](https://github.com/otto-de/gitactionboard/commit/8b8ac046faa0b9df12e2954eea76de7679e84328))

### 📚 Documentation

- Add @valentin-krasontovitsch to contributors list ([44e9772](https://github.com/otto-de/gitactionboard/commit/44e9772b876ce7493c19ec2f058e5a1e6c2df8c6))

- Update changelog for v3.1.1 ([b5269ad](https://github.com/otto-de/gitactionboard/commit/b5269ad02304f730367d1780262994e9d5c8466f))

## [v3.1.0](https://github.com/otto-de/gitactionboard/compare/v3.0.7...v3.1.0) (2023-04-01)

### ⛰️ Features

- Add support to periodic scan for enabled features ([3694887](https://github.com/otto-de/gitactionboard/commit/36948878be0b3fc3afc40b8ebbc9612e63de05cc))

- ([#162](https://github.com/otto-de/gitactionboard/issues/162)) Display gitactionboard version on dashboard ([ad8df47](https://github.com/otto-de/gitactionboard/commit/ad8df47715e951c61f2f61215754775cd1f38d06))

### 🛡️ Security

- Fix [CVE-2023-0464](https://nvd.nist.gov/vuln/detail/CVE-2023-0464) related to libcrypto3 and libssl3 ([03bb494](https://github.com/otto-de/gitactionboard/commit/03bb494f251d1201a01eedc9380b44fa737e817b))

- Fix [CVE-2022-1471](https://nvd.nist.gov/vuln/detail/CVE-2022-1471) related to snakeyaml ([1a68791](https://github.com/otto-de/gitactionboard/commit/1a687912c15593ebd6fc8e4d1c741b6fc6f131b5))

### ⚙️ Miscellaneous Tasks

- Bump io.freefair.lombok from 6.6.3 to 8.0.1 ([6cfe5af](https://github.com/otto-de/gitactionboard/commit/6cfe5af03ba700b773e2d85d71baccc14480b29c))

- Bump gradle version to v8.0.2 ([dca0980](https://github.com/otto-de/gitactionboard/commit/dca09804eb941da568b447310e214075cd48e921))

### 📚 Documentation

- Update sample dashboard screenshots ([f74c1b8](https://github.com/otto-de/gitactionboard/commit/f74c1b809ade8f46377dbf295ac27f587faa0815))

- Update changelog for v3.1.0 ([b9e3451](https://github.com/otto-de/gitactionboard/commit/b9e3451a732e4b9f76160d7514bc0b35f4a8700a))

## [v3.0.7](https://github.com/otto-de/gitactionboard/compare/v3.0.6...v3.0.7) (2023-03-24)

### 📚 Documentation

- Update changelog for v3.0.7 ([5b48cfc](https://github.com/otto-de/gitactionboard/commit/5b48cfc31d8094ec526c502ef135dded4cfc88b2))

## [v3.0.6](https://github.com/otto-de/gitactionboard/compare/v3.0.5...v3.0.6) (2023-02-15)

### 🐛 Bug Fixes

- Enable CSRF protection for APIs ([204df4e](https://github.com/otto-de/gitactionboard/commit/204df4e66286af23a585f371c504feae245ba4e5))

- Provide correct error message when REPO_OWNER_NAME config is missing ([94515a9](https://github.com/otto-de/gitactionboard/commit/94515a9074d49e4ef280cf4cbde31de4b75e5784))

### 🛡️ Security

- Suppress [CVE-2022-3064](https://nvd.nist.gov/vuln/detail/CVE-2022-3064) ([8e1174c](https://github.com/otto-de/gitactionboard/commit/8e1174c667539f6d4fadeb6658153b51b89b35d7))

- Remove explicit libssl3 libcrypto3 upgrade from dockerfile ([2427914](https://github.com/otto-de/gitactionboard/commit/2427914687455996c2da116062aaa2ccc2decadb))

- Remove [CVE-2022-3064](https://nvd.nist.gov/vuln/detail/CVE-2022-3064) from suppression list ([5675197](https://github.com/otto-de/gitactionboard/commit/56751970d157aec42e9c60e05ba8e4a0c60317c6))

- Fix cve related to libcrypto3 and libssl3 ([2eb1a47](https://github.com/otto-de/gitactionboard/commit/2eb1a47215f8305a49089b6c229dcce9bf4dee1c))

### ⚙️ Miscellaneous Tasks

- Manual patch management for frontend ([7c2a2b2](https://github.com/otto-de/gitactionboard/commit/7c2a2b2098403cd12d033cd4599209697afc4f9f))

- Bump dependabot/fetch-metadata from 1.3.5 to 1.3.6 ([e27c20f](https://github.com/otto-de/gitactionboard/commit/e27c20fb74981317c7881b7d1525b1df93c4d69c))

- Bump docker/build-push-action from 3 to 4 ([c85b856](https://github.com/otto-de/gitactionboard/commit/c85b856f9aec5bf7c7607af57dbc8c0afa0b4708))

### 👷 Build

- Fine tune paths for codeql analysis ([7ef98e8](https://github.com/otto-de/gitactionboard/commit/7ef98e83bbc825de7aad4ff971846f9af59f2960))

- Run dependency checks for pull request created by dependabot ([3575ae3](https://github.com/otto-de/gitactionboard/commit/3575ae3338cb48da576106be83e0df10be4d4b17))

- Remove explicit toolVersion for checkstyle and pmd plugin ([c16775f](https://github.com/otto-de/gitactionboard/commit/c16775fa16d5af8dd43be86580fa871c3bad0a6d))

- Suppress shellcheck rule SC2317 due to false positivity ([ccbc785](https://github.com/otto-de/gitactionboard/commit/ccbc78535df6bf103b0978c18bfb54a6d5d45fbb))

- Refer only major version for node in nvmrc ([6818368](https://github.com/otto-de/gitactionboard/commit/68183688e8bc53bf50d290bf340db2fd326a8b16))

- Skip talisman verification for scheduled checks ([4c6031d](https://github.com/otto-de/gitactionboard/commit/4c6031d929cf6bc8042952ae15ca7c9c1b4457fb))

- Suppress talisman warnings ([bc599f2](https://github.com/otto-de/gitactionboard/commit/bc599f20f66a21ea53e36be6f9d0c10e1410604e))

- Update talisman version ([686f0af](https://github.com/otto-de/gitactionboard/commit/686f0af8f1f935b1490cbb19a56b8bf09e95e7cc))

### 📚 Documentation

- Update changelog for v3.0.6 ([920b07c](https://github.com/otto-de/gitactionboard/commit/920b07cb28e2092b26577a77191505cfbcca9a9f))

## [v3.0.5](https://github.com/otto-de/gitactionboard/compare/v3.0.4...v3.0.5) (2023-01-07)

### 🛡️ Security

- Suppress [CVE-2022-1471](https://nvd.nist.gov/vuln/detail/CVE-2022-1471) ([fe3fc21](https://github.com/otto-de/gitactionboard/commit/fe3fc21caccaaa610115e26d3d77ec06ff18087a))

- Upgrade libssl3 and libcrypto3 to fix [CVE-2022-3996](https://nvd.nist.gov/vuln/detail/CVE-2022-3996) ([a211f90](https://github.com/otto-de/gitactionboard/commit/a211f90f2191a5f6ee93bc6c3b8cd7aca5997cae))

### 🚜 Refactor

- Refactor java code ([4fd6792](https://github.com/otto-de/gitactionboard/commit/4fd67925231a84141421d7588e64aba9eee25b0a))

### ⚙️ Miscellaneous Tasks

- Update frontend dependencies version ([80b3aae](https://github.com/otto-de/gitactionboard/commit/80b3aae413c91aafdc0721c356dde51746532ae3))

- Use node v18.21 instead of v16.17 ([15945df](https://github.com/otto-de/gitactionboard/commit/15945df7586646be14f955e5833d57a17350211f))

- Update vite version to v4 ([a0c576c](https://github.com/otto-de/gitactionboard/commit/a0c576c6b191b494c0fa0696eb390f53b67d6476))

### 👷 Build

- Configure boring cyborg ([5961013](https://github.com/otto-de/gitactionboard/commit/5961013bd640c8b215d251e443689ffa5144d79d))

- Merge backend and frontend PR automerge workflow ([775714e](https://github.com/otto-de/gitactionboard/commit/775714e01942108f7f682d19bd396153fa5cc2bc))

- Suppress talisman warnings ([064aa16](https://github.com/otto-de/gitactionboard/commit/064aa164686ef99a476273f5c8540549059d6ee8))

- Use checks workflow to verify talisman for PR ([ec4e08b](https://github.com/otto-de/gitactionboard/commit/ec4e08b69150070bad403aa43d6f37ab9b11b63f))

- Run checks for PR review_requested, edited and ready_for_review type ([f15e89d](https://github.com/otto-de/gitactionboard/commit/f15e89d0b292fe4d61232b4dcd034771c38b72d2))

- Update GitHub runner to use ubuntu-22.04 ([5c93889](https://github.com/otto-de/gitactionboard/commit/5c938892ea85dbbbf4c83d48917e474d311173e7))

- Remove uses of deprecated properties ([5cd4dfc](https://github.com/otto-de/gitactionboard/commit/5cd4dfc994d99a32bd97222f17c2c53d274ef28f))

- Update hadolint version to v2.12.0 ([6ce2626](https://github.com/otto-de/gitactionboard/commit/6ce2626c2eac2437cffd711239a94d65f98e8f34))

- Enable CodeQL analysis ([60934ce](https://github.com/otto-de/gitactionboard/commit/60934cee7cbce398237c32f6642695746590004c))

- Update talisman version ([8bb18e5](https://github.com/otto-de/gitactionboard/commit/8bb18e5d5686df79dd1d24bb0bf20f27df8f0312))

- Update talisman checksum ([8482710](https://github.com/otto-de/gitactionboard/commit/848271083d009761d3602385b5aa972f78b2ff51))

- Delombok backend java before codeql analysis ([2b6eaf3](https://github.com/otto-de/gitactionboard/commit/2b6eaf32bf657814c7760fb0f4d2036d36abf459))

- Suppress talisman warnings ([0eae03b](https://github.com/otto-de/gitactionboard/commit/0eae03be157a76455b7b76a7ff97830e20808499))

### 📚 Documentation

- Update documentation ([c9576cb](https://github.com/otto-de/gitactionboard/commit/c9576cb9ae28d21228b00b55e0485e9443a00e45))

- Update contributors list style ([70fd2e6](https://github.com/otto-de/gitactionboard/commit/70fd2e64a9b7d564ebb8436d5d0f993b862c03f4))

- Update license copyright year ([b1338d3](https://github.com/otto-de/gitactionboard/commit/b1338d35f114d5cfc2ced3df6fcaecdf098a3c2c))

- Update changelog for v3.0.5 ([46eea77](https://github.com/otto-de/gitactionboard/commit/46eea7729e93dbdc9f9ed761d5fa4450f4904755))

## [v3.0.4](https://github.com/otto-de/gitactionboard/compare/v3.0.3...v3.0.4) (2022-12-01)

### 🐛 Bug Fixes

- Allow everyone to access resources from /assets ([64c1e3f](https://github.com/otto-de/gitactionboard/commit/64c1e3f680e56335da8d4dc828fa0d5166a4d48a))

### 🛡️ Security

- Remove unnecessary whitelisted endpoint from security config ([2d7d4a4](https://github.com/otto-de/gitactionboard/commit/2d7d4a4b0a4511500deb592373d786d3216f4d07))

### ⚙️ Miscellaneous Tasks

- Bump gradle version to v7.6 ([10f0be1](https://github.com/otto-de/gitactionboard/commit/10f0be16879f522358074c40ba9eccc9912b6c5b))

- Migrate to mockserver from wiremock ([661d10f](https://github.com/otto-de/gitactionboard/commit/661d10f3efca8cc1fca0654cb75f062c69825259))

- Upgrade spring boot to v3 ([124bdbb](https://github.com/otto-de/gitactionboard/commit/124bdbb36adc7d555a57b7e72a7b9ac45d1a78b2))

- Manual patch management ([668a795](https://github.com/otto-de/gitactionboard/commit/668a795e59583ed58a1ba74018020d58aa8b3a71))

### 👷 Build

- Suppress talisman warnings ([3490e27](https://github.com/otto-de/gitactionboard/commit/3490e2776028728f31c9175146fd021ac5cf2827))

### 📚 Documentation

- Fix typo ([698134d](https://github.com/otto-de/gitactionboard/commit/698134d92d141e3d3916acd4b2a9b288999c523a))

- Add @svenfinke to contributors list ([f1a634a](https://github.com/otto-de/gitactionboard/commit/f1a634a1065c009ba2a12d6e02f0d3e271191652))

- Update changelog for v3.0.4 ([b05a932](https://github.com/otto-de/gitactionboard/commit/b05a9322565e61423a47c4ebb1f57feb8fa8ee40))

## [v3.0.3](https://github.com/otto-de/gitactionboard/compare/v3.0.2...v3.0.3) (2022-11-20)

### 🛡️ Security

- Suppress [CVE-2022-42920](https://nvd.nist.gov/vuln/detail/CVE-2022-42920) ([fcd4ed8](https://github.com/otto-de/gitactionboard/commit/fcd4ed8f13ea6a3f4d1824053cc692e7a3c8fd1d))

### ⚡ Performance

- Use custom build JRE to reduce the docker image size ([c404c21](https://github.com/otto-de/gitactionboard/commit/c404c2161e17bbbc49864a164cfe992775499aa0))

### ⚙️ Miscellaneous Tasks

- Bump dependabot/fetch-metadata from 1.3.4 to 1.3.5 ([5496c1b](https://github.com/otto-de/gitactionboard/commit/5496c1bbc07d3cc4f969c943997e0b425735e216))

### 👷 Build

- Suppress talisman warnings ([b0a003c](https://github.com/otto-de/gitactionboard/commit/b0a003ca065d1ed323b4c785f920ee5d4e37431f))

- Start frontend development server on static 8081 port ([f07293a](https://github.com/otto-de/gitactionboard/commit/f07293ace4f4dac81eeba3a75463e6f1279e34b3))

- Ignore dist and coverage folder during eslint check ([47e8ffa](https://github.com/otto-de/gitactionboard/commit/47e8ffa2f22cd0aacc9fe03c62ba4e27ffdc0694))

### 📚 Documentation

- Display docker image size on Readme ([9faf1b3](https://github.com/otto-de/gitactionboard/commit/9faf1b347c65b6504f949f0a7d72ec552400ce68))

- Update changelog for v3.0.3 ([22a9e1f](https://github.com/otto-de/gitactionboard/commit/22a9e1f04eff4c93a2ec4cc64918672f4f98ae00))

## [v3.0.2](https://github.com/otto-de/gitactionboard/compare/v3.0.1...v3.0.2) (2022-11-03)

### 🛡️ Security

- Update spring security version to fix [CVE-2022-31690](https://nvd.nist.gov/vuln/detail/CVE-2022-31690), [CVE-2022-31692](https://nvd.nist.gov/vuln/detail/CVE-2022-31692) ([29177d0](https://github.com/otto-de/gitactionboard/commit/29177d059ebc2b989fe01e608e31a770fc0daa71))

### 🧪 Testing

- Add initial test setup using vitest and add tests for icons ([#9](https://github.com/otto-de/gitactionboard/issues/9)) ([275015d](https://github.com/otto-de/gitactionboard/commit/275015d2878fcc3178647c326d38b781ba0da708))

### ⚙️ Miscellaneous Tasks

- Update lombok, pmd, checkstyle and spotbugs gradle plugin ([262c943](https://github.com/otto-de/gitactionboard/commit/262c9430754b2e5eb2b51f37def17a5cbb8ee1f0))

- Bump pat-s/always-upload-cache from 3.0.1 to 3.0.11 ([af1f71f](https://github.com/otto-de/gitactionboard/commit/af1f71fa8bfd82956d6b5deff3b8679aab43b6e3))

- Add issue templates ([cfd0c1b](https://github.com/otto-de/gitactionboard/commit/cfd0c1bdaa6911ac66d5c859d8e1cb5216eb0123))

- Enforce eslint standard ruleset for frontend code ([fc80f8c](https://github.com/otto-de/gitactionboard/commit/fc80f8c7966245d98f965a9cfc3ab6c36fbd896d))

### 👷 Build

- Bump @vue/compiler-sfc in /frontend ([9f70e8f](https://github.com/otto-de/gitactionboard/commit/9f70e8fb7456d22d62fd15f475f2f6c27aff66c2))

- Bump stylelint from 14.13.0 to 14.14.0 in /frontend ([d1becc0](https://github.com/otto-de/gitactionboard/commit/d1becc0d0d3ab133f21195593a05df53909078a9))

- Bump vue from 3.2.40 to 3.2.41 in /frontend ([381cf3d](https://github.com/otto-de/gitactionboard/commit/381cf3dedcddc1d14658bf9702bdb82f77f37c47))

- Bump stylelint-config-standard in /frontend ([3ab85f2](https://github.com/otto-de/gitactionboard/commit/3ab85f26ecf80ea05cca5631920ba7a2607f51f7))

- Bump com.github.spotbugs from 5.0.8 to 5.0.12 in /backend ([086654a](https://github.com/otto-de/gitactionboard/commit/086654a648782d7438423e96633656da2545e1c4))

- Integrate trivy for docker image scan ([76d7dce](https://github.com/otto-de/gitactionboard/commit/76d7dce45668df6cba2ee96f1066f351c00687be))

- Bump com.github.ben-manes.versions in /backend ([97aa44a](https://github.com/otto-de/gitactionboard/commit/97aa44ace1507226625a5e54ff795dbcbd554fd2))

- Change default commit message type for dependabot ([647876c](https://github.com/otto-de/gitactionboard/commit/647876c6a9eb5ab7f4cb028dd6170772c31603c6))

- Add name for trivy workflow ([d6e4026](https://github.com/otto-de/gitactionboard/commit/d6e4026c1d798ac29caf775270ecf40da4226d7e))

- Suppress talisman warnings ([494a1fd](https://github.com/otto-de/gitactionboard/commit/494a1fd39825b1f4da12dae5528276caa279e9c3))

- Remove explicit tool version customisation for spotbugs ([e2b8695](https://github.com/otto-de/gitactionboard/commit/e2b86955f2eb3c1ae69d29622328403f8b85b5fd))

- Remove uses of deprecated set-output command ([beee882](https://github.com/otto-de/gitactionboard/commit/beee8826ac357879698c8074be9ab2a8afda20d4))

- Fix typo ([d557402](https://github.com/otto-de/gitactionboard/commit/d5574027a863a6232bc29a50faba23b81cf90762))

- Migrate to vite from vue-cli-service for frontend ([3422bb3](https://github.com/otto-de/gitactionboard/commit/3422bb3bc8ae18462fd3c1e53925f4e3156123cb))

- Suppress talisman warnings ([42c16da](https://github.com/otto-de/gitactionboard/commit/42c16da782afa40397481e0c689e5a785b96034b))

### 📚 Documentation

- Add contributions guide ([75089fd](https://github.com/otto-de/gitactionboard/commit/75089fd4e644e04aee903e9cc2f76f693827e568))

- Add available features on Readme ([c99f136](https://github.com/otto-de/gitactionboard/commit/c99f1367f8b9bdf78ebe1a2355268dacd6e434de))

- Add @gtogbes and @baztian to contributors list ([8dd8dcc](https://github.com/otto-de/gitactionboard/commit/8dd8dcca8b80bc1fe13f79c5f1aab201fd90baac))

- Add different shields badges on Readme ([d57d352](https://github.com/otto-de/gitactionboard/commit/d57d3529039b1c32f6e429a58dcc3aabdf59ccee))

- Update changelog for v3.0.2 ([d73b2cc](https://github.com/otto-de/gitactionboard/commit/d73b2cc5973857d4f7b4c82ac25f3b2ca573faea))

## [v3.0.1](https://github.com/otto-de/gitactionboard/compare/v3.0.0...v3.0.1) (2022-10-08)

### 🛡️ Security

- Update docker base image to fix [CVE-2022-2097](https://nvd.nist.gov/vuln/detail/CVE-2022-2097) and [CVE-2022-37434](https://nvd.nist.gov/vuln/detail/CVE-2022-37434) ([3b839b0](https://github.com/otto-de/gitactionboard/commit/3b839b0cb0834b8ccc9444b37e70952763bbd9ca))

- Update jackson version to fix [CVE-2022-42003](https://nvd.nist.gov/vuln/detail/CVE-2022-42003) ([67e5d04](https://github.com/otto-de/gitactionboard/commit/67e5d0426e7e1eb7bcb14c6c94df301f34622014))

### 👷 Build

- Bump stylelint from 14.12.0 to 14.12.1 in /frontend ([68aca49](https://github.com/otto-de/gitactionboard/commit/68aca4978854e665728e4f29d1af460df74fac16))

- Bump io.spring.dependency-management in /backend ([e32edb0](https://github.com/otto-de/gitactionboard/commit/e32edb09904b8c6a382fb449e165f211e4082658))

- Bump org.springframework.boot in /backend ([aabd325](https://github.com/otto-de/gitactionboard/commit/aabd3255c5b9fc8e6d2c7ce9cb9716412e154d8e))

- Suppress talisman warnings ([17dda00](https://github.com/otto-de/gitactionboard/commit/17dda0000b4222e2310c059f28536d4df1c54eb9))

- Fix path issue for nvd cache ([c063b72](https://github.com/otto-de/gitactionboard/commit/c063b721b3c7baccea291f26b245f3e6f6804e7f))

- Bump core-js from 3.25.2 to 3.25.3 in /frontend ([4a67d1d](https://github.com/otto-de/gitactionboard/commit/4a67d1dae26a0ba64709aa0d135a5340365076e6))

- Bump snakeyaml from 1.32 to 1.33 in /backend ([cc97932](https://github.com/otto-de/gitactionboard/commit/cc97932a10eda6b319f91dbf398974287dd43d15))

- Bump pitest-junit5-plugin from 1.0.0 to 1.1.0 in /backend ([b7ba8d3](https://github.com/otto-de/gitactionboard/commit/b7ba8d3531854b16a596442ebedb50884302c485))

- Bump vue from 3.2.39 to 3.2.40 in /frontend ([660ad2c](https://github.com/otto-de/gitactionboard/commit/660ad2cf112e09babebaac05303b2cc2f0c4a7f6))

- Bump vm2 from 3.9.10 to 3.9.11 in /frontend ([082965d](https://github.com/otto-de/gitactionboard/commit/082965d12e32b24647c99298416848b4d07db671))

- Bump stylelint from 14.12.1 to 14.13.0 in /frontend ([d162802](https://github.com/otto-de/gitactionboard/commit/d16280201d8922ba28932e34fe647e40dc71fe74))

- Suppress talisman warnings ([659747a](https://github.com/otto-de/gitactionboard/commit/659747a1445e49ba06939684280908ff62b1b466))

- Update talisman and handolint version ([09eac8c](https://github.com/otto-de/gitactionboard/commit/09eac8c50e5dd68ac6b8982a2b0e2a9a2135850f))

- Update talisman checksum ([04e799d](https://github.com/otto-de/gitactionboard/commit/04e799db1e9c49824c13d5a102af902ebec84ea1))

- Bump core-js from 3.25.3 to 3.25.4 in /frontend ([dc93911](https://github.com/otto-de/gitactionboard/commit/dc9391166dccbce585d08304e2f353f81c9b50e5))

- Bump core-js from 3.25.4 to 3.25.5 in /frontend ([fcbc037](https://github.com/otto-de/gitactionboard/commit/fcbc0370cdd78c9dd28bd839bdfebe9b61e3878a))

- Bump eslint-plugin-vue from 9.5.1 to 9.6.0 in /frontend ([f89384e](https://github.com/otto-de/gitactionboard/commit/f89384e31ed228a0b65e3e64f71c79aa9937a6f5))

- Bump dependabot/fetch-metadata from 1.3.3 to 1.3.4 ([42ea04d](https://github.com/otto-de/gitactionboard/commit/42ea04dd234e1a70cbbb3872c11b99e34461de89))

- Bump archunit-junit5 from 0.23.1 to 1.0.0 in /backend ([7cc9dc6](https://github.com/otto-de/gitactionboard/commit/7cc9dc6bccbe93e6f74dcf75f7a0b34ce70b9dcf))

- Always add current changes on the top of changelog ([e0222f5](https://github.com/otto-de/gitactionboard/commit/e0222f5dc6d169a61d33dd28afc38690b99b2689))

- Configure semantic-build-versioning plugin to auto update version based on commit message ([1d2baed](https://github.com/otto-de/gitactionboard/commit/1d2baed7f07c1666d1b687c4fea813e70bd0ddbd))

- Fine tune changelog generator script ([92fa491](https://github.com/otto-de/gitactionboard/commit/92fa49136e252debd1cd9e6c0e3fe71a228f79b1))

- Suppress talisman warnings ([9dd75e0](https://github.com/otto-de/gitactionboard/commit/9dd75e037963f7bbe1d1b0cc54b93481194ac63b))

### 📚 Documentation

- Update readme documentation ([#52](https://github.com/otto-de/gitactionboard/issues/52)) ([eec0b40](https://github.com/otto-de/gitactionboard/commit/eec0b40c254ae80fa432aa1ae9639b648b14c4d9))

- Restructure changelog ([d18ff1c](https://github.com/otto-de/gitactionboard/commit/d18ff1cda5b6ba7c9ceec0be7736f9ec91f3bcf9))

- Update changelog for v3.0.1 ([6b0160c](https://github.com/otto-de/gitactionboard/commit/6b0160ce333c43d89bafff7fdc0d9d444ecc4d17))

## [v3.0.0](https://github.com/otto-de/gitactionboard/compare/v2.1.0...v3.0.0) (2022-09-21)

### ⚠ BREAKING CHANGES

- Rename /available-auths to /config endpoint for future use case ([96552fc](https://github.com/otto-de/gitactionboard/commit/96552fc09d385d4c0a304a43cc90b63fc0a91109))

- Use /workflow-jobs as default dashboard path instead of /dashboard ([d290650](https://github.com/otto-de/gitactionboard/commit/d290650eef9d116d041ab943e07a67e90e147191))

### ⛰️ Features

- Display secrets scan alerts on dashboard ([34e339d](https://github.com/otto-de/gitactionboard/commit/34e339d7ebda231f81de5c4f6c08080c700c4316))

- Send notifications for exposed secrets ([20896e8](https://github.com/otto-de/gitactionboard/commit/20896e8008f99e7087afeb32c3655a5c1119380d))

- Send notification for code standard violations ([032f12a](https://github.com/otto-de/gitactionboard/commit/032f12af437b6874e4aca0fa40a6dce0fbea414a))

- Display code standard violations on dashboard ([19e279d](https://github.com/otto-de/gitactionboard/commit/19e279d41d9f381875e92e93aad9d4e7d5bb8ad9))

- Add button to hide single workflow jobs ([#10](https://github.com/otto-de/gitactionboard/issues/10)) ([d5471db](https://github.com/otto-de/gitactionboard/commit/d5471db798db2c008d04e2b3aca775f3e32bf371))

- Display page title on the top of dashboard ([939c0ac](https://github.com/otto-de/gitactionboard/commit/939c0aca3990de0f1ac8b3d3f571e10f292a739f))

### 🐛 Bug Fixes

- Fix alignment issue with menu items ([bbb48a3](https://github.com/otto-de/gitactionboard/commit/bbb48a3d0d19b581b8b354654e0a60e7e8cd82b0))

### 🛡️ Security

- Update snakeyaml version to fix [CVE-2022-25857](https://nvd.nist.gov/vuln/detail/CVE-2022-25857) ([c803bfb](https://github.com/otto-de/gitactionboard/commit/c803bfb244ec6d55a720ea6060237d5aefdfd9c8))

### 🚜 Refactor

- Display GitHub action icon for workflow jobs dashboard ([cb7aa3b](https://github.com/otto-de/gitactionboard/commit/cb7aa3ba52ebc00361a565a42ce3c3a2beac7384))

- Rename Secret vue component ([e645aa3](https://github.com/otto-de/gitactionboard/commit/e645aa3cae51c6637a225b6b7d25e615578e3fb1))

### ⚡ Performance

- Share sever cache acorss users ([293c043](https://github.com/otto-de/gitactionboard/commit/293c04337096922e5ec154f73e7c2631a4379bd8))

### 🎨 Styling

- Introduce stylelint to format css styles with in vue component ([05de1c3](https://github.com/otto-de/gitactionboard/commit/05de1c3bcfbf6d001648bfa1c857022fd8a8e4ca))

- Improve styling of show/hide button ([8c75fc3](https://github.com/otto-de/gitactionboard/commit/8c75fc35c7c4810d1f66d374a2755f05a3255b58))

### 🧪 Testing

- Enable parallel executions for junit tests ([e94b315](https://github.com/otto-de/gitactionboard/commit/e94b315088d7f7ae65604659ed9f4533dc6e52c3))

### ⚙️ Miscellaneous Tasks

- Update frontend dependencies ([abd749c](https://github.com/otto-de/gitactionboard/commit/abd749c7c1755010c346a366c742eb915228de11))

- Use mockoon to run local mock api server for ease frontend development ([1be671e](https://github.com/otto-de/gitactionboard/commit/1be671e28d5a551f0a85c19387878f29da3c1801))

### 👷 Build

- Bump terser from 5.13.1 to 5.14.2 in /frontend ([#8](https://github.com/otto-de/gitactionboard/issues/8)) ([e509e9a](https://github.com/otto-de/gitactionboard/commit/e509e9aebbf7e398473c43e81f2b22e268823158))

- Add changelog url for GitHub release ([b0f5e89](https://github.com/otto-de/gitactionboard/commit/b0f5e8982e99da7e0690f0a99dc9e611b830f6c4))

- Enable dependabot for automatic patch management ([3be168f](https://github.com/otto-de/gitactionboard/commit/3be168fb2165cdd9d1df53646f4d08631e867b37))

- Allow dependabot to auto merge PR ([6d37a00](https://github.com/otto-de/gitactionboard/commit/6d37a00a9baf48fd38a1093070dc32413a85bd49))

- Bump io.spring.dependency-management in /backend ([9d68892](https://github.com/otto-de/gitactionboard/commit/9d68892f51b6dd876b22e7d7369f14ded158c143))

- Fix talisman issue ([708bd03](https://github.com/otto-de/gitactionboard/commit/708bd03e92afb3cea41d095e61fae306b0e7b7d5))

- Fail build incase of lint warnings related to frontend code ([0e88b17](https://github.com/otto-de/gitactionboard/commit/0e88b172af942375706ddf3fae8a79c3d76432da))

- Bump vue from 3.2.37 to 3.2.39 in /frontend ([ad3e224](https://github.com/otto-de/gitactionboard/commit/ad3e224f76e3758c5ac76f7047d48d7c7360d536))

- Fix talisman warnings ([9ac45b3](https://github.com/otto-de/gitactionboard/commit/9ac45b3303890bc464215fcfc0d5ef565f3fa356))

- Bump snakeyaml from 1.31 to 1.32 in /backend ([08c83a4](https://github.com/otto-de/gitactionboard/commit/08c83a4cb39ad023a589ba698e43820f9caa585a))

- Bump com.diffplug.spotless from 6.7.2 to 6.11.0 in /backend ([4452763](https://github.com/otto-de/gitactionboard/commit/44527639e1e8543f3a9ba412de76bd2da41750f7))

- Introduce PI Mutation test for backend ([63d77f5](https://github.com/otto-de/gitactionboard/commit/63d77f5f25849d1fdb39cf0cc3f218158389682b))

- Fix build workflow ([4a60781](https://github.com/otto-de/gitactionboard/commit/4a6078128a60a61acf949621b20ea608e467b683))

- Bump org.owasp.dependencycheck in /backend ([398e130](https://github.com/otto-de/gitactionboard/commit/398e130f46c58404042dc7f55d4f809afbf433c7))

- Bump eslint-plugin-vue from 9.3.0 to 9.5.1 in /frontend ([dd679d4](https://github.com/otto-de/gitactionboard/commit/dd679d456fc534f12555674fc559b8ee31bdcb32))

- Bump core-js from 3.24.0 to 3.25.2 in /frontend ([8b8b57b](https://github.com/otto-de/gitactionboard/commit/8b8b57bf8e2b8cfe2f3ebfee7121086981144e7c))

- Suppress talisman warnings ([c125be2](https://github.com/otto-de/gitactionboard/commit/c125be27506c6ec46d5d23c65d029895d3ba520a))

- Split pull request workflow for backend and frontend ([fb62004](https://github.com/otto-de/gitactionboard/commit/fb620041166c3dd4cd984b3d569cef1cd0bf82ae))

- Update test data for local frontend ([690213d](https://github.com/otto-de/gitactionboard/commit/690213d8e510b016a805c6912f7ece2caa83fb5f))

- Update dependency check suppression list ([8b38cf9](https://github.com/otto-de/gitactionboard/commit/8b38cf967f492a3120cbc8cdceef70ca5b35b65b))

- Update node version v16.17.0 ([6dcfdc0](https://github.com/otto-de/gitactionboard/commit/6dcfdc0588f6eb4b0c9ed6403b7100d996e059cc))

- Update gradle version to v7.5.1 ([a6d3978](https://github.com/otto-de/gitactionboard/commit/a6d39785b1665d2a5bceb22f47fbed493e4a6799))

- Enable dependabot for GitHub actions and docker dependencies ([5662baa](https://github.com/otto-de/gitactionboard/commit/5662baacb7d0faa322a3c952e101b3fa99b46f73))

- Bump org.owasp.dependencycheck in /backend ([915370b](https://github.com/otto-de/gitactionboard/commit/915370b9ad5b39eb6b658df43a42403ed6288120))

- Bump actions/checkout from 2 to 3 ([#38](https://github.com/otto-de/gitactionboard/issues/38)) ([5752740](https://github.com/otto-de/gitactionboard/commit/5752740497806dd09f52e395f292b2cb3104a8cd))

- Bump pat-s/always-upload-cache from 2.1.5 to 3.0.1 ([#39](https://github.com/otto-de/gitactionboard/issues/39)) ([e640969](https://github.com/otto-de/gitactionboard/commit/e640969bb919479ee6a279e19e77184292363182))

- Update GitHub action dependencies ([937e12e](https://github.com/otto-de/gitactionboard/commit/937e12e1f8f6943f18d4c06393a379873ed39e4c))

### 📚 Documentation

- Update TOC on readme and fix typos ([8040610](https://github.com/otto-de/gitactionboard/commit/8040610b13e08434207dd27ab92caa4d4a800351))

- Add sweiler and dependabot to contributors list ([c7fbdd1](https://github.com/otto-de/gitactionboard/commit/c7fbdd1c1ae9479cd324390fdd8004dd34c1cdf6))

- Update sample screenshots ([1f91d60](https://github.com/otto-de/gitactionboard/commit/1f91d6039317441462942616a1c57e7b481d1d1c))

- Remove explicit width from images on readme ([fd91a33](https://github.com/otto-de/gitactionboard/commit/fd91a337ceac18877c5e9fbfa38be26fe8703552))

- Update changelog for v3.0.0 ([1e28b07](https://github.com/otto-de/gitactionboard/commit/1e28b0772179656eacf369dbcb795fcb6a433e5c))

## [v2.1.0](https://github.com/otto-de/gitactionboard/compare/v2.0.7...v2.1.0) (2022-06-28)

### ⛰️ Features

- Send notification on MS Teams in case of build failure ([2f3e004](https://github.com/otto-de/gitactionboard/commit/2f3e0046cdf7df6ee19a802cf8ced2e5c607447a))

### 🚜 Refactor

- Remove usage of depcretated WebSecurityConfigurerAdapter ([5555d18](https://github.com/otto-de/gitactionboard/commit/5555d18e629bf092a27b696e111ff87acaacaf7a))

- Use java 17 feature, syntax ([e0a7a10](https://github.com/otto-de/gitactionboard/commit/e0a7a10a1919f310c20122fa2b635f43bb7fd343))

### ⚙️ Miscellaneous Tasks

- Update dependencies ([6c31714](https://github.com/otto-de/gitactionboard/commit/6c31714a0cbb0aa20481ad5d0a231543d1f12999))

- Update jdk to v17 ([c9dde43](https://github.com/otto-de/gitactionboard/commit/c9dde432c15563cfc94ab7803548ac27996bafc6))

- Update frontend dependencies ([90018a2](https://github.com/otto-de/gitactionboard/commit/90018a277848e9145b0429acb7502cac296483e7))

### 👷 Build

- Use spotless to lint java code instead of sherter.google-java-format ([5a85ca7](https://github.com/otto-de/gitactionboard/commit/5a85ca719485739485659025927eba24f7ca4091))

- Fix path for java 17 for build task ([721f6f6](https://github.com/otto-de/gitactionboard/commit/721f6f61b22ccd70777dfb1e803c9b3a46dafc6d))

### 📚 Documentation

- Accomodate chore as a commit type ([e4382ec](https://github.com/otto-de/gitactionboard/commit/e4382ec0c492b4a6fed2700084d7fb180ecdd230))

- Update Readme ([728e429](https://github.com/otto-de/gitactionboard/commit/728e4290d8c819fb14228f6c656d164b7b682ebe))

- Add instruction link to create incoming webhook connection for MS teams ([6036bdd](https://github.com/otto-de/gitactionboard/commit/6036bddbce9144a65b1b7a3dc72ea3b80f897470))

- Update changelog ([3b09ef7](https://github.com/otto-de/gitactionboard/commit/3b09ef74a027834e4924cf4bb4149a2a2bb43747))

## [v2.0.7](https://github.com/otto-de/gitactionboard/compare/v2.0.6...v2.0.7) (2022-05-25)

### 🛡️ Security

- Update spring dependency to fix [CVE-2022-22970](https://nvd.nist.gov/vuln/detail/CVE-2022-22970), [CVE-2022-22978](https://nvd.nist.gov/vuln/detail/CVE-2022-22978) and [CVE-2022-29885](https://nvd.nist.gov/vuln/detail/CVE-2022-29885) ([c632e04](https://github.com/otto-de/gitactionboard/commit/c632e0415ddb2dc9f968c6e1204feb9fa1ae48e3))

### 📚 Documentation

- Update changelog ([e4a9879](https://github.com/otto-de/gitactionboard/commit/e4a98794f69bb9380a4550fb938cc956b26a48c5))

## [v2.0.6](https://github.com/otto-de/gitactionboard/compare/v2.0.5...v2.0.6) (2022-05-17)

### 🐛 Bug Fixes

- Make page scrollable so that side bar is always accessible ([d5cf429](https://github.com/otto-de/gitactionboard/commit/d5cf4298bf3b10c5200d2fb230f1895e263f5faa))

- Log correct information when authentication is disabled ([e492ce0](https://github.com/otto-de/gitactionboard/commit/e492ce0f7bd9b34141129fe012eeea6e0d750abb))

### 🛡️ Security

- Move to amazoncorretto docker base image from openjdk to fix security vulnerabilities ([d1f7cf5](https://github.com/otto-de/gitactionboard/commit/d1f7cf5e133d514f6b64453a485851a26e4109d5))

- Update npm dependencies to fix security vulnerabilities ([534ce21](https://github.com/otto-de/gitactionboard/commit/534ce210555f3ad4b86dd5da0107e32aba8791dd))

### 👷 Build

- Create docker tag with minor version ([65be76c](https://github.com/otto-de/gitactionboard/commit/65be76c2d14802f251b6dca298c670d33fdeb9ee))

### 📚 Documentation

- Use correct link for genarate changelog ([a1b0ee2](https://github.com/otto-de/gitactionboard/commit/a1b0ee29fca13f7a81867d039968be0d9f2b1475))

- Update changelog ([9e1ee72](https://github.com/otto-de/gitactionboard/commit/9e1ee72f7eacb75116ed6ca0b3260c5065c03cea))

## [v2.0.5](https://github.com/otto-de/gitactionboard/compare/v2.0.4...v2.0.5) (2022-04-25)

### 🛡️ Security

- Update spring dependencies to fix [CVE-2022-22968](https://nvd.nist.gov/vuln/detail/CVE-2022-22968) ([0aeb21f](https://github.com/otto-de/gitactionboard/commit/0aeb21f9c0e99ea6308d9493c3f2b4a39d930f4c))

### 👷 Build

- Update java dependencies ([98d1de8](https://github.com/otto-de/gitactionboard/commit/98d1de8bc3b93087bcb2f33a25c523e1e49dd16e))

- Update other java dependencies ([edf41dc](https://github.com/otto-de/gitactionboard/commit/edf41dc4b8b410afb174b831c52baf647e6ff4fa))

- Update docker metadata GitHub action version ([a5334c8](https://github.com/otto-de/gitactionboard/commit/a5334c82124121c8ecbc36c3d529dcf67eb2e6cc))

### 📚 Documentation

- Update changelog ([351c759](https://github.com/otto-de/gitactionboard/commit/351c759d9c4dd93bcf7194394416b96f681b4eed))

## [v2.0.4](https://github.com/otto-de/gitactionboard/compare/v2.0.3...v2.0.4) (2022-04-01)

### 🛡️ Security

- Update spring boot version to fix [CVE-222-22965](https://nvd.nist.gov/vuln/detail/CVE-222-22965) ([359147d](https://github.com/otto-de/gitactionboard/commit/359147dcbe97a83727ee3f9ccf16eb9b901f8434))

### 📚 Documentation

- Update changelog ([8a7ff6a](https://github.com/otto-de/gitactionboard/commit/8a7ff6ae0e4f4ccceb9e36a2faa677c5220e823d))

## [v2.0.3](https://github.com/otto-de/gitactionboard/compare/v2.0.2...v2.0.3) (2022-03-31)

### 🐛 Bug Fixes

- Intermittent solution to prevent RCE with Spring Core ([52c17b5](https://github.com/otto-de/gitactionboard/commit/52c17b5220769b721058967ee723140067d9f708))

### 🛡️ Security

- Update dependency to fix [CVE-2022-23181](https://nvd.nist.gov/vuln/detail/CVE-2022-23181) ([5140721](https://github.com/otto-de/gitactionboard/commit/514072123190105fab84cbbbd30dc5b04a182744))

### 👷 Build

- Update cache key for nvd nist on GitHub action ([36c97a2](https://github.com/otto-de/gitactionboard/commit/36c97a29cad111ae994c906592da131a2faf3614))

### 📚 Documentation

- Update changelog ([e498f8a](https://github.com/otto-de/gitactionboard/commit/e498f8ae6143954bd549430c8996218a48f1a50a))

## [v2.0.2](https://github.com/otto-de/gitactionboard/compare/v2.0.1...v2.0.2) (2022-03-31)

### 🛡️ Security

- Update dependency to fix [CVE-2020-36518](https://nvd.nist.gov/vuln/detail/CVE-2020-36518) ([3926e50](https://github.com/otto-de/gitactionboard/commit/3926e505b027b0bc5677b84ae1cab54bbdbe8bc0))

### 📚 Documentation

- Update changelog ([62012e8](https://github.com/otto-de/gitactionboard/commit/62012e86953dec8972116bc51bae7a1a2314acdc))

## [v2.0.1](https://github.com/otto-de/gitactionboard/compare/v2.0.0...v2.0.1) (2022-02-22)

### 🐛 Bug Fixes

- Filter out headers with undefined value ([4800400](https://github.com/otto-de/gitactionboard/commit/4800400e284094744a9a0c124b1aa568b19fe5c4))

- Use correct title for dashboard ([abb3957](https://github.com/otto-de/gitactionboard/commit/abb3957ea389f9783cec8839684c70156a66b79a))

### 👷 Build

- Use correct syntax for release tag name ([b99793d](https://github.com/otto-de/gitactionboard/commit/b99793d51afc90b060966576020ff4ada59f0f98))

- Format changelog as part of generation command ([bad8dea](https://github.com/otto-de/gitactionboard/commit/bad8dea47a699d97ed5f84c364a943fc028dba6d))

### 📚 Documentation

- Update readme file ([b687187](https://github.com/otto-de/gitactionboard/commit/b687187a5cf7af183e99ab0338102878a9d9309e))

- Add changelog for v2.0.1 ([90b198a](https://github.com/otto-de/gitactionboard/commit/90b198a238a1ceb9ff80f3ff4b8b2fc15adecc69))

## [v2.0.0](https://github.com/otto-de/gitactionboard/compare/v1.1.2...v2.0.0) (2022-02-18)

### ⚠ BREAKING CHANGES

- Remove ability to configure dashboard using query params ([ea53408](https://github.com/otto-de/gitactionboard/commit/ea5340810820205648f60d0a4c305c6f1577e259))

### ⛰️ Features

- Introduce basic authentication ([abfdeab](https://github.com/otto-de/gitactionboard/commit/abfdeabdae2caff3544748716cdc4e30c14abe8b))

- Use client token to fetch workflow details ([e769e77](https://github.com/otto-de/gitactionboard/commit/e769e77bbf9e0e3e4e812f8a6bf1d80616f587d3))

- Introduce login page ([6fe267f](https://github.com/otto-de/gitactionboard/commit/6fe267f26689415d4eec8293251680e44813feca))

- Explicitly delete access_token cookie on logout ([b52caa3](https://github.com/otto-de/gitactionboard/commit/b52caa3e7b0c2bbdee4d300cfca8b20eeffda10c))

- Hide logout button for guest users ([a7e1c22](https://github.com/otto-de/gitactionboard/commit/a7e1c22c13b1de30bfa1046bbea637534079458a))

- Allow only authenticate user to access private pages when authentication is enabled or unknown ([990ccb9](https://github.com/otto-de/gitactionboard/commit/990ccb9e74c286fc6106c7c2a9971ab74c3004a4))

- Display spinner till page is fully loaded ([4ebd072](https://github.com/otto-de/gitactionboard/commit/4ebd0726c80b276b7d4b991c61ca7841f6d4697b))

- Clear cookies when user click on logout ([9b7b0ee](https://github.com/otto-de/gitactionboard/commit/9b7b0ee19289e2c4fd6fb798fe7758e4809477ca))

- Display error message if login credentials are wrong ([e6cf144](https://github.com/otto-de/gitactionboard/commit/e6cf144af403e3681afcd490cd01ea9f2150dee3))

- Display happy octopus when there is no failed build and user doesn't want to see healthy build ([670034b](https://github.com/otto-de/gitactionboard/commit/670034b5a865a640a0cb14f51e327f6fcacbdeac))

- Allow guest user to fetch files from /img folder ([9e759ff](https://github.com/otto-de/gitactionboard/commit/9e759ffa74d1933b1fd3425995b052124d2a8393))

- Respect servlet context config while serving resources ([3a0a215](https://github.com/otto-de/gitactionboard/commit/3a0a21589f6bd6f8e848d534e0289beed7bbaa5b))

### 🐛 Bug Fixes

- Clear interval timer when user moves away from dashboard page ([2dea455](https://github.com/otto-de/gitactionboard/commit/2dea455a0db46305a7bf0ab5aab89495713f03e9))

### 🚜 Refactor

- Remove global variables ([d9762b4](https://github.com/otto-de/gitactionboard/commit/d9762b49fda5edf04b0300b7de504051c0781152))

### 👷 Build

- Patch management ([1fc4e17](https://github.com/otto-de/gitactionboard/commit/1fc4e170d3410823f38618740b8cde730d412e65))

- Add script to generate changelog ([74391ea](https://github.com/otto-de/gitactionboard/commit/74391ea1d9491b6e230c8aa187c4ec67db240d32))

- Generate GitHub release as part of release process ([cd842b3](https://github.com/otto-de/gitactionboard/commit/cd842b382ee70348e449cc1da5078816167a8d69))

- Fix bump-version command ([3097bfa](https://github.com/otto-de/gitactionboard/commit/3097bfa14101ea7d00e29cdc1c69277aec21c014))

- Append latest changelog instead of prepend ([83e322c](https://github.com/otto-de/gitactionboard/commit/83e322c36da4858f8977bdf7f5cb412c62244697))

### 📚 Documentation

- Update readme documentation ([a5fd0a9](https://github.com/otto-de/gitactionboard/commit/a5fd0a9a68ff3fcb0b9f9b25ba4c07eccd7b8f1a))

- Add contributors to readme file ([896f573](https://github.com/otto-de/gitactionboard/commit/896f5733f21823f4f4ff66b279ad014e0f6dcb15))

- Update readme file with new sample for v2.0.0 ([cce50b2](https://github.com/otto-de/gitactionboard/commit/cce50b21676d230cbf1eb22bac5f378ba90b9cd5))

- Add changelog ([39f16bf](https://github.com/otto-de/gitactionboard/commit/39f16bf4a927f177814c992715004152c2d8a794))

- Link changelog on readme file ([1546a10](https://github.com/otto-de/gitactionboard/commit/1546a10776946cb79c3c481e8d5f858962fec162))

<!-- generated by git-cliff -->
