import storageService from '@/services/storageService';
import preferences from '@/services/preferences';

const ALL_POSSIBLE_EVENTS = {
  branch_protection_rule: 'Branch protection rule',
  check_run: 'Check run',
  check_suite: 'Check suite',
  create: 'Create',
  delete: 'Delete',
  deployment: 'Deployment',
  deployment_status: 'Deployment status',
  discussion: 'Discussion',
  discussion_comment: 'Discussion comment',
  fork: 'Fork',
  gollum: 'Gollum',
  issue_comment: 'Issue comment',
  issues: 'Issues',
  label: 'Label',
  member: 'Member',
  merge_group: 'Merge Group',
  milestone: 'Milestone',
  page_build: 'Page build',
  project: 'Project',
  project_card: 'Project card',
  project_column: 'Project column',
  public: 'Public',
  pull_request: 'Pull request',
  pull_request_review: 'Pull request review',
  pull_request_review_comment: 'Pull request review comment',
  pull_request_target: 'Pull request target',
  push: 'Push',
  registry_package: 'Registry package',
  release: 'Release',
  status: 'Status',
  watch: 'Watch',
  workflow_call: 'Workflow call',
  workflow_dispatch: 'Workflow dispatch',
  workflow_run: 'Workflow run',
  repository_dispatch: 'Repository dispatch',
  schedule: 'Schedule'
};

const SECOND = 1000;
const MINUTE = 60 * SECOND;
const HOUR = 60 * MINUTE;
const DAY = 24 * HOUR;
const WEEK = 7 * DAY;
const MONTH = 30 * DAY;
const YEAR = 12 * MONTH;

const RELATIVE_FORMAT_INFO = [
  { cutoffs: MINUTE, unit: 'seconds', amount: SECOND },
  { cutoffs: HOUR, unit: 'minutes', amount: MINUTE },
  { cutoffs: DAY, unit: 'hours', amount: HOUR },
  { cutoffs: WEEK, unit: 'days', amount: DAY },
  { cutoffs: MONTH, unit: 'weeks', amount: WEEK },
  { cutoffs: YEAR, unit: 'months', amount: MONTH },
  { cutoffs: Number.POSITIVE_INFINITY, unit: 'years', amount: YEAR }
];

const RELATIVE_TIME_FORMAT = new Intl.RelativeTimeFormat('en', {
  numeric: 'auto',
  style: 'long'
});

export const setAvailableAuths = (availableAuths) => {
  storageService.setItem('availableAuths', JSON.stringify(availableAuths));
};

export const setVersion = (version) => {
  storageService.setItem('version', version);
};

export const setGithubSecretsScanMonitoringEnabled = (
  githubSecretsScanMonitoringEnabled
) => {
  storageService.setItem(
    'githubSecretsScanMonitoringEnabled',
    JSON.stringify(githubSecretsScanMonitoringEnabled)
  );
};

export const setGithubCodeScanMonitoringEnabled = (
  githubSecretsScanMonitoringEnabled
) => {
  storageService.setItem(
    'githubCodeScanMonitoringEnabled',
    JSON.stringify(githubSecretsScanMonitoringEnabled)
  );
};

export const getAvailableAuths = () =>
  JSON.parse(storageService.getItem('availableAuths'));

export const getGithubSecretsScanMonitoringEnabled = () =>
  JSON.parse(storageService.getItem('githubSecretsScanMonitoringEnabled')) ||
  false;

export const getGithubCodeScanMonitoringEnabled = () =>
  JSON.parse(storageService.getItem('githubCodeScanMonitoringEnabled')) ||
  false;

export const getVersion = () => storageService.getItem('version');

const buildTriggeredEvents = (keys) => {
  return keys.map(key => ({
    title: ALL_POSSIBLE_EVENTS[key],
    value: key
  }));
};

export const getAllPossibleTriggeredEvents = () => buildTriggeredEvents(Object.keys(ALL_POSSIBLE_EVENTS));

export const getShowBuildsDueToTriggeredEvents = () =>
  preferences.showBuildsDueToTriggeredEvents.length === 0
    ? Object.keys(ALL_POSSIBLE_EVENTS)
    : preferences.showBuildsDueToTriggeredEvents;

export const getRelativeTime = timestamp => {
  const timeDifference = new Date(timestamp) - new Date();

  const { amount, unit } =
      RELATIVE_FORMAT_INFO.find(({ cutoffs }) => cutoffs > Math.abs(timeDifference)) ||
      RELATIVE_FORMAT_INFO[0];

  return RELATIVE_TIME_FORMAT.format(Math.round(timeDifference / amount), unit);
};
