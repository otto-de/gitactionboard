<template>
  <v-container
    v-if="isGithubCodeScanMonitoringEnabled"
    id="code-standard-violations-dashboard"
    fluid
  >
    <DashboardHeader sub-header="Code Standard Violations" />
    <Dashboard
      :enable-max-idle-time-optimization="enableMaxIdleTimeOptimization"
      :max-idle-time="maxIdleTime"
      content-displayer="FailureGridCell"
      :fetch-contents="fetchContents"
    />
  </v-container>
</template>

<script>
import router from '@/router';
import preferences from '@/services/preferences';
import { getGithubCodeScanMonitoringEnabled } from '@/services/utils';
import { fetchCodeStandardViolations } from '@/services/apiService';
import Dashboard from '@/components/Dashboard';
import DashboardHeader from '@/components/DashboardHeader';

export default {
  name: 'CodeStandardViolationsDashboard',
  components: { DashboardHeader, Dashboard },
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    enableMaxIdleTimeOptimization() {
      return preferences.enableMaxIdleTimeOptimization;
    },
    maxIdleTime() {
      return preferences.maxIdleTime;
    },
    isGithubCodeScanMonitoringEnabled() {
      return getGithubCodeScanMonitoringEnabled();
    }
  },
  methods: {
    fetchContents() {
      return fetchCodeStandardViolations();
    }
  }
};
</script>

<style scoped>
</style>
