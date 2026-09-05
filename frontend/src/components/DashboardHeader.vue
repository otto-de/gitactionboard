<template>
  <v-app-bar
    class="dashboard-header"
    elevation="2"
  >
    <v-row no-gutters>
      <v-col cols="10">
        <v-app-bar-title
          class="header-title text-center font-weight-bold pl-8"
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
          class="sub-header pr-6 text-right font-weight-medium align-center fill-height d-inline-flex flex-row-reverse"
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
.dashboard-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  background: linear-gradient(180deg, rgba(250, 251, 252, 0.98) 0%, rgba(245, 247, 249, 0.98) 100%);
  backdrop-filter: blur(6px);
}

.header-title {
  line-height: 1.1;
  font-size: clamp(1.5rem, 2.5vw, 2.1rem);
  font-family: "Avenir Next", "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  letter-spacing: 0.01em;
}

.version {
  font-size: 0.95rem;
  font-family: inherit;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.58);
}

#sub-header {
  font-size: 1rem;
  font-family: "Avenir Next", "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  letter-spacing: 0.02em;
  color: rgba(0, 0, 0, 0.7);
}

:global(.v-theme--dark) .dashboard-header {
  border-bottom-color: rgba(255, 255, 255, 0.15);
  background: linear-gradient(180deg, rgba(24, 28, 34, 0.96) 0%, rgba(20, 24, 30, 0.96) 100%);
}

:global(.v-theme--dark) .version,
:global(.v-theme--dark) #sub-header {
  color: rgba(255, 255, 255, 0.8);
}

</style>
