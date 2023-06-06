<template>
  <GridCell
    :last-executed-time="content.lastBuildTime"
    :url="content.webUrl"
    :name="content.name"
    display-toggle-visibility
    :show-relative-time="showRelativeTime"
    :append-class-names="classNames"
    :hidden="hidden"
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
    }
  },
  emits: ['toggleVisibility'],
  computed: {
    showRelativeTime() {
      const { activity, lastBuildStatus } = this.content;
      return activity !== 'Building' && lastBuildStatus !== 'Success';
    },
    classNames() {
      return this.content.activity === 'Building'
        ? `${this.buildStatusIndicator} building`
        : `${this.buildStatusIndicator}`;
    },
    buildStatusIndicator() {
      switch (this.content.lastBuildStatus) {
        case 'Success':
          return 'success';
        case 'Unknown':
          return 'unknown';
        default:
          return 'failure';
      }
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
