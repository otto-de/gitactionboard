<template>
  <GridCell
    :last-executed-time="content.createdAt"
    :url="content.url"
    :name="content.name"
    append-class-names="failure"
    show-relative-time
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
    }
  }
};
</script>

<style scoped>

</style>
