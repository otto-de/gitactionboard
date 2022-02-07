<template>
  <div id="app">
    <SideMenu v-if="renderMenu" />
    <router-view />
  </div>
</template>

<script>

import SideMenu from "@/components/SideMenuBar";
import router from "@/router";

export default {
  name: 'App',
  components: { SideMenu },
  computed: {
    renderMenu(){
      const routerPath = router.currentRoute.value.path

      return !(routerPath === '/login' || routerPath === '/')
    }
  }
}
</script>

<style scoped>

#app {
  display: flex;
  flex-direction: row;
  height: 100vh;
  width: 100vw;
}

</style>
