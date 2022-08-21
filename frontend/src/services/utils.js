import storageService from "@/services/storageService";

export const setAvailableAuths = (availableAuths) => {
  storageService.setItem("availableAuths", JSON.stringify(availableAuths));
};

export const setGithubSecretsScanMonitoringEnabled = (
  githubSecretsScanMonitoringEnabled
) => {
  storageService.setItem(
    "githubSecretsScanMonitoringEnabled",
    JSON.stringify(githubSecretsScanMonitoringEnabled)
  );
};

export const getAvailableAuths = () =>
  JSON.parse(storageService.getItem("availableAuths"));

export const getGithubSecretsScanMonitoringEnabled = () =>
  JSON.parse(storageService.getItem("githubSecretsScanMonitoringEnabled")) ||
  false;
