<template>
  <v-app-bar
    elevation="0"
    class="border-b"
  >
    <v-app-bar-nav-icon
      v-if="mobile"
      @click="navigationState.drawerOpen = !navigationState.drawerOpen"
    />
    <v-app-bar-title class="brand-row pl-4">
      <v-img
        src="/favicon.ico"
        alt="GitactionBoard"
        class="brand-mark"
        width="38"
        height="28"
      />
      <span
        v-if="!mobile"
        class="brand-name"
      >GitactionBoard</span>
      <span
        v-if="!mobile"
        class="version font-mono"
      >v{{ version }}</span>
    </v-app-bar-title>
    <v-spacer />
    <v-app-bar-title
      id="sub-header"
      class="page-title pr-4"
    >
      {{ subHeader }}
    </v-app-bar-title>
  </v-app-bar>
</template>

<script>
import { getVersion } from '@/services/utils';
import navigationState from '@/services/navigationState';

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
      version: getVersion(),
      navigationState
    };
  },
  computed: {
    mobile() {
      return this.$vuetify.display.xs;
    }
  }
};
</script>

<style scoped>
.brand-row {
  flex: 0 1 auto;
  align-items: center;
  gap: 10px;
}

.brand-mark {
  border-radius: 6px;
  flex: 0 0 auto;
}

.brand-name {
  font-weight: 700;
  font-size: 15px;
  letter-spacing: -0.01em;
  white-space: nowrap;
}

.version {
  font-size: 11px;
  opacity: var(--v-medium-emphasis-opacity);
  margin-left: 2px;
}

.page-title {
  flex: 0 1 auto;
  justify-content: flex-end;
  font-weight: 700;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
