<template>
  <div id="container">
    <div class="header">
      <div class="header-title">
        Gitaction Board
      </div>
      <div class="separator" />
    </div>
    <Jobs
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
  padding-left: 30px;
  padding-right: 30px;
}

.header {
  height: 6%;
  margin-bottom: 35px;
}

.header-title {
  font-family: Snell Roundhand, cursive;
  color: #5b5454;
  font-size: 50px;
  text-align: center;
  font-weight: bold;
}

.separator {
  height: 0;
  border: 1px solid #5b5454;
}

</style>
