<template>
  <div
    v-if="isGithubCodeScanMonitoringEnabled"
    id="container"
  >
    <div class="header">
      <div class="header-title">
        Gitaction Board
      </div>
      <div class="separator" />
    </div>
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
import {getGithubCodeScanMonitoringEnabled} from "@/services/utils";
import {fetchCodeStandardViolations} from "@/services/apiService";
import Dashboard from "@/components/Dashboard";

export default {
  name: "CodeStandardViolationsDashboard",
  components: {Dashboard},
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
div {
  height: 100%;
  margin: 0;
  padding: 0;
}

#container {
  height: 100%;
  width: 95%;
  padding-left: 30px;
  padding-right: 30px;
  overflow: scroll;
  padding-bottom: 1px;
}

.header {
  height: 6%;
  margin-bottom: 35px;
}

.header-title {
  font-family: Snell Roundhand, cursive;
  color: #5b5454;
  font-size: 50px;
  text-align: center;
  font-weight: bold;
}

.separator {
  height: 0;
  border: 1px solid #5b5454;
}

</style>
