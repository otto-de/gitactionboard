import storageService from "@/services/storageService";

class Preferences {
  __get__(configKey) {
    return storageService.getItem(configKey);
  }

  __set__(configKey, value) {
    storageService.setItem(configKey, value);
  }

  get showHealthyBuilds() {
    const showHealthBuildConfig = this.__get__("show-healthy-builds");
    return !(showHealthBuildConfig && showHealthBuildConfig === "false");
  }

  set showHealthyBuilds(value) {
    this.__set__("show-healthy-builds", value);
  }

  get disableIdleOptimization() {
    return this.__get__("disable-max-idle-time") === "true";
  }

  set disableIdleOptimization(value) {
    this.__set__("disable-max-idle-time", value);
  }

  get maxIdleTime() {
    const maxIdleTimeConfig = this.__get__("max-idle-time");
    return maxIdleTimeConfig ? parseInt(maxIdleTimeConfig) : 5;
  }

  set maxIdleTime(value) {
    this.__set__("max-idle-time", value);
  }

  toggleVisibility(jobName) {
    let hiddenJobs = JSON.parse(this.__get__("hidden-jobs") || "[]");
    const indexOfThisJobInList = hiddenJobs.indexOf(jobName);
    if (indexOfThisJobInList !== -1) {
      hiddenJobs.splice(indexOfThisJobInList, 1);
    } else {
      hiddenJobs.push(jobName);
    }
    this.__set__("hidden-jobs", JSON.stringify(hiddenJobs));
  }

  isJobHidden(jobName) {
    const hiddenJobs = JSON.parse(this.__get__("hidden-jobs") || "[]");
    return hiddenJobs.indexOf(jobName) !== -1;
  }
}

export default new Preferences();
