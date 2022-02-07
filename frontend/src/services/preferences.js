class Preferences {
  __get__(configKey) {
    return localStorage.getItem(configKey);
  }

  __set__(configKey, value) {
    localStorage.setItem(configKey, value);
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
}

export default new Preferences();
