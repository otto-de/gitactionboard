<template>
  <div id="container">
    <DashboardHeader sub-header="Workflow Jobs" />
    <Dashboard
      :disable-max-idle-time="disableIdleOptimization"
      :max-idle-time="maxIdleTime"
      content-displayer="Job"
      :fetch-contents="fetchContents"
      :hide-by-key="'name'"
      :name-of-items="'job(s)'"
    />
  </div>
</template>

<script>
import router from "@/router";
import preferences from "@/services/preferences";
import Dashboard from "@/components/Dashboard";
import {fetchCctrayJson} from "@/services/apiService";
import DashboardHeader from "@/components/DashboardHeader";

export default {
  el: '#app',
  name: "WorkflowDashboard",
  components: {DashboardHeader, Dashboard},
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
  methods: {
    fetchContents() {
      return fetchCctrayJson().then(this.marshalData)
    },
    isIdleHealthyBuild(lastBuildStatus, activity) {
      return lastBuildStatus === "Success" && activity === "Sleeping"
    },
    marshalData(data) {
      return data.filter(({lastBuildStatus, activity}) => {
            return this.showHealthyBuilds ? true : !this.isIdleHealthyBuild(lastBuildStatus, activity);
          }
      );
    },
  }
}
</script>

<style scoped>
#container {
  height: 100%;
  width: 95%;
  padding-left: 30px;
  padding-right: 30px;
  overflow: scroll;
  padding-bottom: 1px;
}

</style>
