<template>
  <div
    v-if="isGithubSecretsScanMonitoringEnabled"
    id="container"
  >
    <DashboardHeader sub-header="Exposed Secrets" />
    <Dashboard
      :disable-max-idle-time="disableIdleOptimization"
      :max-idle-time="maxIdleTime"
      content-displayer="Secret"
      :fetch-contents="fetchContents"
    />
  </div>
</template>

<script>
import router from "@/router";
import preferences from "@/services/preferences";
import {getGithubSecretsScanMonitoringEnabled} from "@/services/utils";
import {fetchSecretAlerts} from "@/services/apiService";
import Dashboard from "@/components/Dashboard";
import DashboardHeader from "@/components/DashboardHeader";

export default {
  name: "SecretsDashboard",
  components: {DashboardHeader, Dashboard},
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    disableIdleOptimization(){
      return preferences.disableIdleOptimization;
    },
    maxIdleTime(){
      return preferences.maxIdleTime;
    },
    isGithubSecretsScanMonitoringEnabled(){
      return getGithubSecretsScanMonitoringEnabled();
    }
  },
  methods: {
    fetchContents() {
      return fetchSecretAlerts();
    },
  }
}
</script>

<style scoped>

#container {
  height: 100%;
  width: 95%;
  padding-left: 30px;
  padding-right: 30px;
  overflow: scroll;
  padding-bottom: 1px;
}

</style>
