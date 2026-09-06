<template>
  <v-navigation-drawer
    v-model="navigationState.drawerOpen"
    :permanent="!mobile"
    :rail="!mobile && navigationState.rail"
  >
    <v-list
      nav
      color="primary"
    >
      <v-list-item
        :prepend-icon="(mobile ? !navigationState.drawerOpen : navigationState.rail) ? `$menu`:`$menuOpen`"
        @click="toggleSideBar"
      />
      <v-list-item
        :active="currentPath === '/workflow-jobs'"
        prepend-icon="$workflowJobs"
        title="Workflow Jobs"
        value="workflowJobs"
        href="#/workflow-jobs"
      />
      <v-list-item
        v-if="githubSecretsScanMonitoringEnabled"
        :active="currentPath === '/secrets'"
        prepend-icon="$secrets"
        title="Exposed Secrets"
        value="secrets"
        href="#/secrets"
      />
      <v-list-item
        v-if="isGithubCodeScanMonitoringEnabled"
        :active="currentPath === '/code-standard-violations'"
        prepend-icon="$codeStandards"
        title="Code Standard Violations"
        value="codeStandardViolations"
        href="#/code-standard-violations"
      />
      <v-list-item
        :active="currentPath === '/metrics'"
        prepend-icon="$metrics"
        title="Metrics"
        value="metrics"
        href="#/metrics"
      />
      <v-list-item
        :active="currentPath === '/preferences'"
        prepend-icon="$preferences"
        title="Preferences"
        value="preferences"
        href="#/preferences"
      />
    </v-list>
    <template #append>
      <v-list
        nav
        color="primary"
      >
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
import navigationState from '@/services/navigationState';

export default {
  name: 'SideMenuBar',
  data() {
    return {
      navigationState
    };
  },
  computed: {
    mobile() {
      return this.$vuetify.display.xs;
    },
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
  created() {
    navigationState.drawerOpen = !this.mobile;
  },
  methods: {
    toggleSideBar() {
      if (this.mobile) {
        navigationState.drawerOpen = !navigationState.drawerOpen;
      } else {
        navigationState.rail = !navigationState.rail;
      }
    },
    logout() {
      clearCookies();
      window.location.href = './logout';
    }
  }
};
</script>
