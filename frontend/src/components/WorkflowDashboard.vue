<template>
  <v-container
    id="workflows-dashboard"
    fluid
  >
    <DashboardHeader sub-header="Workflow Jobs" />
    <Dashboard
      :enable-max-idle-time-optimization="enableMaxIdleTimeOptimization"
      :max-idle-time="maxIdleTime"
      content-displayer="Job"
      :fetch-contents="fetchContents"
      :hide-by-key="'name'"
      :name-of-items="'job(s)'"
    />
  </v-container>
</template>

<script>
import router from '@/router';
import preferences from '@/services/preferences';
import Dashboard from '@/components/Dashboard';
import { fetchCctrayJson } from '@/services/apiService';
import DashboardHeader from '@/components/DashboardHeader';

export default {
  el: '#app',
  name: 'WorkflowDashboard',
  components: { DashboardHeader, Dashboard },
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    showHealthyBuilds() {
      return preferences.showHealthyBuilds;
    },
    enableMaxIdleTimeOptimization() {
      return preferences.enableMaxIdleTimeOptimization;
    },
    maxIdleTime() {
      return preferences.maxIdleTime;
    },
    hasPreferredTriggeredEvents() {
      return preferences.showBuildsDueToTriggeredEvents.length > 0;
    }
  },
  methods: {
    fetchContents() {
      return fetchCctrayJson().then(this.marshalData);
    },
    isIdleHealthyBuild(lastBuildStatus, activity) {
      return lastBuildStatus === 'Success' && activity === 'Sleeping';
    },
    marshalData(data) {
      return data
        .filter(({ lastBuildStatus, activity }) =>
          this.showHealthyBuilds ? true : !this.isIdleHealthyBuild(lastBuildStatus, activity))
        .filter(({ triggeredEvent }) =>
          !this.hasPreferredTriggeredEvents ||
            preferences.showBuildsDueToTriggeredEvents.indexOf(triggeredEvent) !== -1);
    }
  }
};
</script>

<style scoped>

</style>
