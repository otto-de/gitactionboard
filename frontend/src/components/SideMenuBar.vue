<template>
  <v-navigation-drawer
    permanent
    :rail="rail"
  >
    <v-list nav>
      <v-list-item
        :prepend-icon="rail ? `mdi-menu`:`mdi-menu-open`"
        @click="toggleSideBar"
      />
      <v-list-item
        :active="currentPath === '/workflow-jobs'"
        active-class="active"
        prepend-icon="mdi-sitemap"
        title="Workflow Jobs"
        value="workflowJobs"
        href="#/workflow-jobs"
      />
      <v-list-item
        v-if="githubSecretsScanMonitoringEnabled"
        :active="currentPath === '/secrets'"
        active-class="active"
        prepend-icon="mdi-shield-lock-outline"
        title="Exposed Secrets"
        value="secrets"
        href="#/secrets"
      />
      <v-list-item
        v-if="isGithubCodeScanMonitoringEnabled"
        :active="currentPath === '/code-standard-violations'"
        active-class="active"
        prepend-icon="mdi-code-braces-box"
        title="Code Standard Violations"
        value="codeStandardViolations"
        href="#/code-standard-violations"
      />
      <v-list-item
        :active="currentPath === '/preferences'"
        prepend-icon="mdi-cog-outline"
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
          prepend-icon="mdi-account-circle"
          :title="firstName"
        />
        <v-list-item
          v-if="isAuthenticate"
          prepend-icon="mdi-logout"
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
