import storageService from '@/services/storageService';

class Preferences {
  __get__(configKey) {
    return storageService.getItem(configKey);
  }

  __set__(configKey, value) {
    storageService.setItem(configKey, value);
  }

  __getSystemTheme() {
    return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
  }

  get showHealthyBuilds() {
    const showHealthBuildConfig = this.__get__('show-healthy-builds');
    return !(showHealthBuildConfig && showHealthBuildConfig === 'false');
  }

  set showHealthyBuilds(value) {
    this.__set__('show-healthy-builds', value);
  }

  disableIdleOptimization() {
    return this.__get__('disable-max-idle-time') === 'true';
  }

  get enableMaxIdleTimeOptimization() {
    const enableIdleTimeRestrictionConfig = this.__get__('enable-max-idle-time-optimization');
    if (enableIdleTimeRestrictionConfig === undefined || enableIdleTimeRestrictionConfig === null) {
      return !this.disableIdleOptimization();
    }
    return enableIdleTimeRestrictionConfig === 'true';
  }

  set enableMaxIdleTimeOptimization(value) {
    this.__set__('enable-max-idle-time-optimization', value);
  }

  get maxIdleTime() {
    const maxIdleTimeConfig = this.__get__('max-idle-time');
    return maxIdleTimeConfig ? parseInt(maxIdleTimeConfig) : 5;
  }

  set maxIdleTime(value) {
    this.__set__('max-idle-time', value);
  }

  get hiddenElements() {
    return JSON.parse(this.__get__('hidden-elements') || '{}');
  }

  set hiddenElements(value) {
    this.__set__('hidden-elements', JSON.stringify(value));
  }

  get theme() {
    return this.__get__('theme') || this.__getSystemTheme();
  }

  set theme(value) {
    this.__set__('theme', value);
  }

  get showBuildsDueToTriggeredEvents() {
    return JSON.parse(this.__get__('show-builds-due-to-triggered-events') || '[]');
  }

  set showBuildsDueToTriggeredEvents(events) {
    this.__set__('show-builds-due-to-triggered-events', JSON.stringify(events));
  }
}

export default new Preferences();
