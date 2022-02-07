<template>
  <div :class="{'side-menu': true, 'open':isClicked }">
    <div>
      <MenuIcon
        :is-clicked="isClicked"
        @clicked="openMenu"
      />
      <MenuItems
        :is-clicked="isClicked"
        menu-item-name="Dashboard"
        icon-name="dashboard"
        :is-active="activeMap.dashboard"
        @dashboard="redirectDashboardPage"
      />
      <MenuItems
        :is-clicked="isClicked"
        menu-item-name="Preferences"
        icon-name="settings"
        :is-active="activeMap.preferences"
        @settings="redirectProfilePage"
      />
    </div>
    <div>
      <MenuItems
        :is-clicked="isClicked"
        :menu-item-name="name"
        icon-name="profile"
      />
      <MenuItems
        :is-clicked="isClicked"
        menu-item-name="Logout"
        icon-name="logout"
        :is-active="activeMap.logout"
        @logout="logout"
      />
    </div>
  </div>
</template>

<script>
import MenuIcon from "@/components/MenuIcon";
import MenuItems from "@/components/MenuItems";
import router from "@/router";
import authenticationService from "@/services/authenticationService";

export default {
  name: "SideMenu",
  components: {MenuItems, MenuIcon},
  data() {
    return {
      isClicked: false,
      activeMap: {
        'dashboard': false,
        'preferences': false,
        'logout': false
      }
    }
  },
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    name(){
      return authenticationService.getName();
    }
  },
  watch: {
    currentPath() {
      const currentPath = this.currentPath;
      this.activeMap = Object.keys(this.activeMap)
          .reduce((previousValue, key) => {
            if (currentPath === `/${key}`)
              return {...previousValue, [key]: true}
            return {...previousValue, [key]: false}
          }, {})
    }
  },
  methods: {
    openMenu() {
      this.isClicked = !this.isClicked
    },
    redirectProfilePage() {
      router.push("/preferences")
    },
    redirectDashboardPage() {
      router.push("/dashboard")
    },
    logout() {
      window.location.href = '/logout'
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
