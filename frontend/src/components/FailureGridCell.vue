<template>
  <GridCell
    :last-executed-time="content.createdAt"
    :url="content.url"
    :name="content.name"
    status="failure"
    show-relative-time
    :build-monitor-view-enabled="buildMonitorViewEnabled"
  />
</template>

<script>
import GridCell from '@/components/GridCell.vue';

export default {
  name: 'FailureGridCell',
  components: { GridCell },
  props: {
    content: {
      type: Object,
      required: true
    },
    buildMonitorViewEnabled: {
      type: Boolean,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
