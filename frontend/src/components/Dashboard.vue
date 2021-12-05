<template>
  <div id="container">
    <div id="header-container">
      <span id="header">Gitaction Board</span>
    </div>
    <Jobs/>
  </div>
</template>

<script>

import Jobs from "@/components/Jobs";

export default {
  name: "Dashboard",
  components: { Jobs }
}
</script>

<style scoped>
#header-container {
  text-align: center;
  margin-bottom: 40px;
}

#header {
  font-family: "OpenSans", sans-serif;
  text-decoration: underline;
  font-size: 40px;
}
</style>
