<template>
  <v-row
    align="center"
    justify="space-between"
    no-gutters
  >
    <v-col class="metric-card-title">
      {{ title }}
    </v-col>
    <v-col cols="auto">
      <v-btn
        :icon="icon"
        :aria-label="iconLabel"
        @click="onButtonClick"
      />
    </v-col>
  </v-row>
</template>

<script>

export default {
  name: 'MetricCardTitle',
  props: {
    title: {
      type: String,
      required: true
    },
    onButtonClick: {
      type: Function,
      required: true
    },
    expanded: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  computed: {
    icon() {
      return this.expanded ? '$close' : '$expand';
    },
    iconLabel() {
      return this.expanded ? 'Close' : 'Expand';
    }
  }
};
</script>

<style scoped>
.metric-card-title {
  min-width: 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.3;
  white-space: normal;
}
</style>
