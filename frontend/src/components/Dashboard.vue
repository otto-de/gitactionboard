<template>
  <div id="container">
    <Header />
    <Jobs
      :show-healthy-builds="shouldShowHealthyBuilds()"
      :disable-max-idle-time="shouldDisableIdleOptimization()"
      :max-idle-time="getMaxIdleTime()"
    />
  </div>
</template>

<script>
import Jobs from "@/components/Jobs";
import Header from "@/components/Header";
const HIDE_HEALTHY_PARAM = "hide-healthy";
const MAX_IDLE_TIME_PARAM = "max-idle-time";
const DISABLE_MAX_IDLE_TIME_PARAM = "disable-max-idle-time";
const URL_SEARCH_PARAMS = new URLSearchParams(window.location.search);

export default {
  name: "Dashboard",
  components: { Header, Jobs },
  data() {
    return {
      URL_SEARCH_PARAMS,
      HIDE_HEALTHY_PARAM,
      MAX_IDLE_TIME_PARAM,
      DISABLE_MAX_IDLE_TIME_PARAM
    }
  },
  methods: {
    shouldShowHealthyBuilds() {
      const hideHealthBuildConfig = URL_SEARCH_PARAMS.get(HIDE_HEALTHY_PARAM);
      return !hideHealthBuildConfig || hideHealthBuildConfig === "false";
    },
    shouldDisableIdleOptimization() {
      return URL_SEARCH_PARAMS.get(DISABLE_MAX_IDLE_TIME_PARAM) === "true";
    },
    getMaxIdleTime() {
      const maxIdleTimeConfig = URL_SEARCH_PARAMS.get(MAX_IDLE_TIME_PARAM);
      return maxIdleTimeConfig ? parseInt(maxIdleTimeConfig) : 5;
    }
  }
}
</script>
