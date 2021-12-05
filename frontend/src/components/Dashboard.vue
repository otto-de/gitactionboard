<template>
  <div id="container">
    <Header/>
    <Jobs :showHealthyBuilds="shouldShowHealthyBuilds()"/>
  </div>
</template>

<script>
import Jobs from "@/components/Jobs";
import Header from "@/components/Header";
const HIDE_HEALTHY_PARAM = "hide-healthy";
const URL_SEARCH_PARAMS = new URLSearchParams(window.location.search);

export default {
  name: "Dashboard",
  components: { Header, Jobs },
  data() {
    return {
      URL_SEARCH_PARAMS,
      HIDE_HEALTHY_PARAM
    }
  },
  methods: {
    shouldShowHealthyBuilds: function () {
      const hideHealthBuildConfig = URL_SEARCH_PARAMS.get(HIDE_HEALTHY_PARAM);
      return !hideHealthBuildConfig || hideHealthBuildConfig === "false";
    }
  }
}
</script>
