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
import preferences from '@/services/preferences';
import { useTheme } from 'vuetify';

export default {
  name: 'App',
  components: { SideMenuBar },
  data() {
    const themeInstance = useTheme();
    themeInstance.global.name.value = preferences.theme;
    return {
      theme: themeInstance
    };
  },
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
