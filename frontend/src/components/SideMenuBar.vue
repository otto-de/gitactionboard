<template>
  <div :class="{'side-menu': true, 'open':isOpened }">
    <div>
      <HamburgerMenuIcon
        :is-clicked="isOpened"
        @clicked="openMenu"
      />
      <MenuItems
        :is-clicked="isOpened"
        :clickable="!activeMap.dashboard"
        menu-item-name="Dashboard"
        icon-name="dashboard"
        :is-active="activeMap.dashboard"
        @dashboard="redirectDashboardPage"
      />
      <MenuItems
        :is-clicked="isOpened"
        :clickable="!activeMap.preferences"
        menu-item-name="Preferences"
        icon-name="settings"
        :is-active="activeMap.preferences"
        @settings="redirectProfilePage"
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
        'dashboard': false,
        'preferences': false,
        'logout': false
      };
      const currentPath = this.currentPath;

      return Object.keys(this.activeMap || defaultActiveMap)
          .reduce((previousValue, key) => {
            if (currentPath === `/${key}`)
              return {...previousValue, [key]: true}
            return {...previousValue, [key]: false}
          }, {});
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
    redirectProfilePage() {
      router.push("/preferences")
    },
    redirectDashboardPage() {
      router.push("/dashboard")
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
