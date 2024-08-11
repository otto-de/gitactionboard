<template>
  <GridCell
    :last-executed-time="content.lastBuildTime"
    :url="content.webUrl"
    :name="content.name"
    display-toggle-visibility
    :show-relative-time="showRelativeTime"
    :hidden="hidden"
    :in-progress="isInProgress"
    :status="content.lastBuildStatus"
    :build-monitor-view-enabled="buildMonitorViewEnabled"
    @toggle-visibility="toggleVisibility"
  />
</template>

<script>

import GridCell from '@/components/GridCell.vue';

export default {
  name: 'Job',
  components: { GridCell },
  props: {
    content: {
      type: Object,
      required: true
    },
    hidden: {
      type: Boolean,
      required: true
    },
    buildMonitorViewEnabled: {
      type: Boolean,
      required: true
    }
  },
  emits: ['toggleVisibility'],
  computed: {
    isInProgress() {
      return this.content.activity === 'Building';
    },
    showRelativeTime() {
      return !this.isInProgress && this.content.lastBuildStatus !== 'Success';
    }
  },
  methods: {
    toggleVisibility(key) {
      this.$emit('toggleVisibility', key);
    }
  }
};
</script>

<style scoped>
</style>
