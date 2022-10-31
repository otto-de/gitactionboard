import storageService from '@/services/storageService';

export const setAvailableAuths = (availableAuths) => {
  storageService.setItem('availableAuths', JSON.stringify(availableAuths));
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
