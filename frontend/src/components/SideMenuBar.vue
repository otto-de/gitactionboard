<template>
  <v-navigation-drawer
    permanent
    :rail="rail"
  >
    <v-list nav>
      <v-list-item
        :prepend-icon="rail ? `$menu`:`$menuOpen`"
        @click="toggleSideBar"
      />
      <v-list-item
        :active="currentPath === '/workflow-jobs'"
        active-class="active"
        prepend-icon="$workflowJobs"
        title="Workflow Jobs"
        value="workflowJobs"
        href="#/workflow-jobs"
      />
      <v-list-item
        v-if="githubSecretsScanMonitoringEnabled"
        :active="currentPath === '/secrets'"
        active-class="active"
        prepend-icon="$secrets"
        title="Exposed Secrets"
        value="secrets"
        href="#/secrets"
      />
      <v-list-item
        v-if="isGithubCodeScanMonitoringEnabled"
        :active="currentPath === '/code-standard-violations'"
        active-class="active"
        prepend-icon="$codeStandards"
        title="Code Standard Violations"
        value="codeStandardViolations"
        href="#/code-standard-violations"
      />
      <v-list-item
        :active="currentPath === '/metrics'"
        active-class="active"
        prepend-icon="$metrics"
        title="Metrics"
        value="metrics"
        href="#/metrics"
      />
      <v-list-item
        :active="currentPath === '/preferences'"
        prepend-icon="$preferences"
        active-class="active"
        title="Preferences"
        value="preferences"
        href="#/preferences"
      />
    </v-list>
    <template #append>
      <v-list nav>
        <v-list-item
          v-if="avatarUrl"
          :prepend-avatar="avatarUrl"
          :title="firstName"
        />
        <v-list-item
          v-if="!avatarUrl"
          prepend-icon="$avatar"
          :title="firstName"
        />
        <v-list-item
          v-if="isAuthenticate"
          prepend-icon="$logout"
          title="Logout"
          @click="logout"
        />
      </v-list>
    </template>
  </v-navigation-drawer>
</template>

<script>
import { clearCookies, getAvatarUrl, getName, isAuthenticate } from '@/services/authenticationService';
import { getGithubCodeScanMonitoringEnabled, getGithubSecretsScanMonitoringEnabled } from '@/services/utils';
import router from '@/router';

export default {
  name: 'SideMenuBar',
  data() {
    return {
      rail: true
    };
  },
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    githubSecretsScanMonitoringEnabled() {
      return getGithubSecretsScanMonitoringEnabled();
    },
    isGithubCodeScanMonitoringEnabled() {
      return getGithubCodeScanMonitoringEnabled();
    },
    avatarUrl() {
      return getAvatarUrl();
    },
    firstName() {
      return getName().split(' ')[0];
    },
    isAuthenticate() {
      return isAuthenticate();
    }
  },
  methods: {
    toggleSideBar() {
      this.rail = !this.rail;
    },
    logout() {
      clearCookies();
      window.location.href = './logout';
    }
  }
};
</script>

<style scoped>
.active {
    background-color: #5f8d77;
}

</style>
