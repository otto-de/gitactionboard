<template>
  <v-app-bar
    elevation="5"
  >
    <v-row no-gutters>
      <v-col cols="10">
        <v-app-bar-title
          class="header-title text-center font-weight-bold pl-10"
        >
          <span>Gitaction Board</span> <span class="version">(v{{ version }})</span>
        </v-app-bar-title>
      </v-col>
      <v-col
        cols="2"
        class="d-inline-flex"
      >
        <v-app-bar-title
          id="sub-header"
          class="pr-4 text-right font-weight-bold align-center fill-height d-inline-flex flex-row-reverse mt-3"
        >
          {{ subHeader }}
        </v-app-bar-title>
      </v-col>
    </v-row>
  </v-app-bar>
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
.header-title {
    line-height: normal;
    font-size: 50px;
    font-family: "Snell Roundhand", cursive;
}

.version {
    font-size: 15px;
    font-family: "American Typewriter", serif;
}

#sub-header {
    font-size: 20px;
    font-family: "American Typewriter", serif;
}

</style>
