<template>
  <div class="header">
    <div class="header-title">
      <span>Gitaction Board</span> <span class="version">(v{{ version }})</span>
    </div>
    <div id="sub-header">
      {{ subHeader }}
    </div>
  </div>
  <div class="separator" />
</template>

<script>
import { getVersion } from '@/services/utils';

export default {
  name: 'DashboardHeader',
  props: {
    subHeader: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      version: getVersion()
    };
  }
};
</script>

<style scoped>
.header {
  height: 6%;
  color: #5b5454;
}

.header-title {
  font-size: 50px;
  text-align: center;
  font-family: "Snell Roundhand", cursive;
  font-weight: bold;
}

.version {
    font-size: 15px;
    font-family: "American Typewriter", serif;
}

#sub-header {
  z-index: auto;
  text-align: end;
  margin-top: -2%;
  font-size: 20px;
  font-weight: bold;
  font-family: "American Typewriter", serif;
}

.separator {
  height: 0;
  border: 1px solid #5b5454;
  margin-bottom: 35px;
}

</style>
