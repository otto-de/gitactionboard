<template>
  <v-container
    v-if="isGithubSecretsScanMonitoringEnabled"
    id="secrets-dashboard"
    fluid
  >
    <DashboardHeader sub-header="Exposed Secrets" />
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
import { getGithubSecretsScanMonitoringEnabled } from '@/services/utils';
import { fetchSecretAlerts } from '@/services/apiService';
import Dashboard from '@/components/Dashboard';
import DashboardHeader from '@/components/DashboardHeader';

export default {
  name: 'SecretsDashboard',
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
    isGithubSecretsScanMonitoringEnabled() {
      return getGithubSecretsScanMonitoringEnabled();
    }
  },
  methods: {
    fetchContents() {
      return fetchSecretAlerts();
    }
  }
};
</script>

<style scoped>

</style>
