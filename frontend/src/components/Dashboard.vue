<template>
  <div id="container">
    <Jobs
      v-if="currentPath === '/dashboard'"
      :show-healthy-builds="showHealthyBuilds"
      :disable-max-idle-time="disableIdleOptimization"
      :max-idle-time="maxIdleTime"
    />
  </div>
</template>

<script>
import Jobs from "@/components/Jobs";
import router from "@/router";
import preferences from "@/services/preferences";

export default {
  el: '#app',
  name: "Dashboard",
  components: {Jobs},
  computed: {
    currentPath() {
      return router.currentRoute.value.path;
    },
    showHealthyBuilds(){
     return preferences.showHealthyBuilds;
    },
    disableIdleOptimization(){
      return preferences.disableIdleOptimization;
    },
    maxIdleTime(){
      return preferences.maxIdleTime;
    }
  },
  methods: {}
}
</script>

<style scoped>

div {
  height: 100%;
  margin: 0;
  padding: 0;
}

#container {
  height: 100%;
  width: 95%;
}

</style>
