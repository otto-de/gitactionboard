<template>
  <div
    v-if="isGithubCodeScanMonitoringEnabled"
    id="container"
  >
    <DashboardHeader sub-header="Code Standard Violations" />
    <Dashboard
      :disable-max-idle-time="disableIdleOptimization"
      :max-idle-time="maxIdleTime"
      content-displayer="GridCell"
      :fetch-contents="fetchContents"
    />
  </div>
</template>

<script>
import router from "@/router";
import preferences from "@/services/preferences";
import {getGithubCodeScanMonitoringEnabled} from "@/services/utils";
import {fetchCodeStandardViolations} from "@/services/apiService";
import Dashboard from "@/components/Dashboard";
import DashboardHeader from "@/components/DashboardHeader";

export default {
  name: "CodeStandardViolationsDashboard",
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
    isGithubCodeScanMonitoringEnabled(){
      return getGithubCodeScanMonitoringEnabled();
    }
  },
  methods: {
    fetchContents() {
      return fetchCodeStandardViolations();
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
