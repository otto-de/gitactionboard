<template>
  <v-app>
    <v-main>
      <v-container
        id="git-action-board"
        class="ma-0 pa-0"
        fluid
      >
        <SideMenuBar v-if="renderMenu" />
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>

import SideMenuBar from '@/components/SideMenuBar';
import router from '@/router';

export default {
  name: 'App',
  components: { SideMenuBar },
  computed: {
    renderMenu() {
      const routerPath = router.currentRoute.value.path;

      return !(routerPath === '/login' || routerPath === '/');
    }
  }
};
</script>

<style scoped>

</style>
