import storageService from '@/services/storageService';
import preferences from '@/services/preferences';

const allPossibleEvents = {
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
  repository_dispatch: 'Repository dispatch'
};

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
    title: allPossibleEvents[key],
    value: key
  }));
};

export const getAllPossibleTriggeredEvents = () => buildTriggeredEvents(Object.keys(allPossibleEvents));

export const getShowBuildsDueToTriggeredEvents = () =>
  preferences.showBuildsDueToTriggeredEvents.length === 0
    ? getAllPossibleTriggeredEvents()
    : buildTriggeredEvents(preferences.showBuildsDueToTriggeredEvents);
