<template>
  <v-row
    align="center"
    justify="space-between"
  >
    <v-col cols="auto">
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

</style>
