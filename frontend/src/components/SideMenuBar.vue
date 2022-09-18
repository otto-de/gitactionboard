<template>
  <div :class="{'side-menu': true, 'open':isOpened }">
    <div>
      <HamburgerMenuIcon
        :is-clicked="isOpened"
        @clicked="openMenu"
      />
      <MenuItems
        :is-clicked="isOpened"
        :clickable="!activeMap['workflow-jobs']"
        menu-item-name="Workflow Jobs"
        icon-name="workflowJobs"
        :is-active="activeMap['workflow-jobs']"
        @workflow-jobs="redirectToWorkflowJobsPage"
      />
      <MenuItems
        v-if="githubSecretsScanMonitoringEnabled"
        :is-clicked="isOpened"
        :clickable="!activeMap.secrets"
        menu-item-name="Exposed Secrets"
        icon-name="secrets"
        :is-active="activeMap.secrets"
        @secrets="redirectToSecretsPage"
      />
      <MenuItems
        v-if="isGithubCodeScanMonitoringEnabled"
        :is-clicked="isOpened"
        :clickable="!activeMap['code-standard-violations']"
        menu-item-name="Standard Violations"
        icon-name="codeStandardViolations"
        :is-active="activeMap['code-standard-violations']"
        @code-standard-violations="redirectToCodeStandardViolationsPage"
      />
      <MenuItems
        :is-clicked="isOpened"
        :clickable="!activeMap.preferences"
        menu-item-name="Preferences"
        icon-name="settings"
        :is-active="activeMap.preferences"
        @settings="redirectToPreferencesPage"
      />
    </div>
    <div>
      <MenuItems
        :is-clicked="isOpened"
        :clickable="false"
        :menu-item-name="firstName"
        icon-name="profile"
      />
      <MenuItems
        v-if="isAuthenticate"
        :is-clicked="isOpened"
        menu-item-name="Logout"
        icon-name="logout"
        :is-active="activeMap.logout"
        @logout="logout"
      />
    </div>
  </div>
</template>

<script>
import HamburgerMenuIcon from "@/icons/HamburgerMenuIcon";
import MenuItems from "@/components/MenuItems";
import router from "@/router";
import {clearCookies, getName, isAuthenticate} from "@/services/authenticationService";
import {getGithubCodeScanMonitoringEnabled, getGithubSecretsScanMonitoringEnabled} from "@/services/utils";

export default {
  name: "SideMenuBar",
  components: {MenuItems, HamburgerMenuIcon},
  data() {
    return {
      isOpened: false,
    }
  },
  computed: {
    activeMap(){
      const defaultActiveMap = {
        'workflow-jobs': false,
        'preferences': false,
        'secrets': false,
        'logout': false,
        'code-standard-violations': false,
      };
      const currentPath = this.currentPath;

      return Object.keys(this.activeMap || defaultActiveMap)
          .reduce((previousValue, key) => {
            if (currentPath === `/${key}`)
              return {...previousValue, [key]: true}
            return {...previousValue, [key]: false}
          }, {});
    },
    githubSecretsScanMonitoringEnabled(){
      return getGithubSecretsScanMonitoringEnabled()
    },
    isGithubCodeScanMonitoringEnabled(){
      return getGithubCodeScanMonitoringEnabled();
    },
    currentPath() {
      return router.currentRoute.value.path;
    },
    firstName(){
      return getName().split(" ")[0];
    },
    isAuthenticate(){
      return isAuthenticate();
    },
  },
  methods: {
    openMenu() {
      this.isOpened = !this.isOpened
    },
    redirectToPreferencesPage() {
      router.push("/preferences")
    },
    redirectToSecretsPage() {
      router.push("/secrets")
    },
    redirectToCodeStandardViolationsPage() {
      router.push("/code-standard-violations")
    },
    redirectToWorkflowJobsPage() {
      router.push("/workflow-jobs")
    },
    logout() {
      clearCookies();
      window.location.href = './logout'
    }
  }
}


</script>

<style scoped>

.side-menu {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  width: 3%;
  background-color: black;
  transition: width 0.1s ease-in-out 100ms;
}

.side-menu.open {
  width: 15%;
  align-items: flex-start;
}

</style>
