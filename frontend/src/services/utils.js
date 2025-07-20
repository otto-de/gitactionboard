import storageService from '@/services/storageService';
import preferences from '@/services/preferences';

const ALL_POSSIBLE_EVENTS = {
  branch_protection_rule: {
    name: 'Branch protection rule',
    description: 'When a branch protection rule is created, edited, or deleted.',
    category: 'Repository & Git Events'
  },
  check_run: {
    name: 'Check run',
    description: 'When check run activity occurs (e.g., created, completed, rerequested).',
    category: 'Workflow, CI/CD & Status Events'
  },
  check_suite: {
    name: 'Check suite',
    description: 'When check suite activity occurs (e.g., requested, completed).',
    category: 'Workflow, CI/CD & Status Events'
  },
  create: {
    name: 'Create',
    description: 'When a Git branch or tag is created.',
    category: 'Repository & Git Events'
  },
  delete: {
    name: 'Delete',
    description: 'When a Git branch or tag is deleted.',
    category: 'Repository & Git Events'
  },
  deployment: {
    name: 'Deployment',
    description: 'When a deployment is created.',
    category: 'Workflow, CI/CD & Status Events'
  },
  deployment_status: {
    name: 'Deployment status',
    description: 'When a deployment status changes.',
    category: 'Workflow, CI/CD & Status Events'
  },
  discussion: {
    name: 'Discussion',
    description: 'When a discussion is created, edited, deleted, etc. (activities vary).',
    category: 'Issue & Discussion Events'
  },
  discussion_comment: {
    name: 'Discussion comment',
    description: 'When a comment on a discussion is created, edited, or deleted.',
    category: 'Issue & Discussion Events'
  },
  fork: {
    name: 'Fork',
    description: 'When a repository is forked.',
    category: 'Repository & Git Events'
  },
  gollum: {
    name: 'Gollum',
    description: 'When a Wiki page is created or updated.',
    category: 'Repository & Git Events'
  },
  issue_comment: {
    name: 'Issue comment',
    description: 'When a comment on an issue or pull request is created, edited, or deleted.',
    category: 'Issue & Discussion Events'
  },
  issues: {
    name: 'Issues',
    description: 'When an issue is opened, edited, deleted, labeled, etc. (activities vary).',
    category: 'Issue & Discussion Events'
  },
  label: {
    name: 'Label',
    description: 'When a label is created, edited, or deleted.',
    category: 'Issue & Discussion Events'
  },
  member: {
    name: 'Member',
    description: 'When a user is added, removed, or has their privileges changed in a repository.',
    category: 'Repository Administration & Integrations'
  },
  merge_group: {
    name: 'Merge Group',
    description: 'When a pull request is added to, or removed from, a merge queue.',
    category: 'Repository & Git Events'
  },
  milestone: {
    name: 'Milestone',
    description: 'When a milestone is created, closed, opened, or deleted.',
    category: 'Issue & Discussion Events'
  },
  page_build: {
    name: 'Page build',
    description: 'When a GitHub Pages build is requested or fails.',
    category: 'Repository & Git Events'
  },
  project: {
    name: 'Project',
    description: 'When a project is created, closed, reopened, or edited.',
    category: 'Repository Administration & Integrations'
  },
  project_card: {
    name: 'Project card',
    description: 'When a project card is created, moved, edited, or deleted.',
    category: 'Repository Administration & Integrations'
  },
  project_column: {
    name: 'Project column',
    description: 'When a project column is created, moved, edited, or deleted.',
    category: 'Repository Administration & Integrations'
  },
  public: {
    name: 'Public',
    description: 'When a repository changes from private to public.',
    category: 'Repository & Git Events'
  },
  pull_request: {
    name: 'Pull request',
    description: 'When a pull request is opened, synchronized, closed, reopened, etc. (activities vary).',
    category: 'Pull Request Events'
  },
  pull_request_review: {
    name: 'Pull request review',
    description: 'When a pull request review is submitted, edited, or dismissed.',
    category: 'Pull Request Events'
  },
  pull_request_review_comment: {
    name: 'Pull request review comment',
    description: 'When a comment on a pull request review is created, edited, or deleted.',
    category: 'Pull Request Events'
  },
  pull_request_target: {
    name: 'Pull request target',
    description:
        'Similar to pull_request, but runs in the context of the base repository and branch, suitable for forks.',
    category: 'Pull Request Events'
  },
  push: {
    name: 'Push',
    description: 'When one or more commits are pushed to a repository branch or tag.',
    category: 'Repository & Git Events'
  },
  registry_package: {
    name: 'Registry package',
    description: 'When a package is published or updated in GitHub Packages.',
    category: 'Repository Administration & Integrations'
  },
  release: {
    name: 'Release',
    description: 'When a release is published, edited, deleted, or prereleased.',
    category: 'Repository Administration & Integrations'
  },
  status: {
    name: 'Status',
    description: 'When the status of a Git commit changes.',
    category: 'Workflow, CI/CD & Status Events'
  },
  watch: {
    name: 'Watch',
    description: 'When a user stars (watches) a repository.',
    category: 'Repository Administration & Integrations'
  },
  workflow_call: {
    name: 'Workflow call',
    description: 'Allows a workflow to be called from another workflow.',
    category: 'Workflow, CI/CD & Status Events'
  },
  workflow_dispatch: {
    name: 'Workflow dispatch',
    description: 'Allows you to manually trigger a workflow from the GitHub UI, GitHub CLI, or REST API.',
    category: 'Workflow, CI/CD & Status Events'
  },
  workflow_run: {
    name: 'Workflow run',
    description: 'When a workflow run is requested or completed.',
    category: 'Workflow, CI/CD & Status Events'
  },
  repository_dispatch: {
    name: 'Repository dispatch',
    description: 'Allows you to trigger a workflow manually with a custom webhook event from outside GitHub.',
    category: 'Repository Administration & Integrations'
  },
  schedule: {
    name: 'Schedule',
    description: 'Allows you to schedule a workflow to run at specific UTC times using cron syntax.',
    category: 'Workflow, CI/CD & Status Events'
  }
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

export const getAllPossibleTriggeredEvents = () => Object.keys(ALL_POSSIBLE_EVENTS);

export const getCategorisedAllPossibleTriggeredEvents = () =>
  Object.entries(ALL_POSSIBLE_EVENTS)
    .reduce((acc, [key, value]) =>
      ({ ...acc, [value.category]: { ...(acc[value.category] || {}), [key]: value } }),
    {});

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
