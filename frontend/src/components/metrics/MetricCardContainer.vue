<template>
  <MetricCard
    :start-date="startDate"
    :end-date="endDate"
    :data-set="dataSet"
    :time-extractor="timeExtractor"
    :value-extractor="valueExtractor"
    :rate-series="rateSeries"
    :title="title"
    :on-button-click="toggleFullScreen"
    :repo-name="repoName"
  />
  <v-dialog v-model="fullScreen">
    <MetricCard
      :start-date="startDate"
      :end-date="endDate"
      :data-set="dataSet"
      :time-extractor="timeExtractor"
      :value-extractor="valueExtractor"
      :rate-series="rateSeries"
      :title="`${repoName}: ${title}`"
      :on-button-click="toggleFullScreen"
      :repo-name="repoName"
      expanded
    />
  </v-dialog>
</template>

<script>

import MetricCard from '@/components/metrics/MetricCard.vue';

export default {
  name: 'MetricCardContainer',
  components: {
    MetricCard
  },
  props: {
    dataSet: {
      type: Object,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    repoName: {
      type: String,
      required: true
    },
    startDate: {
      type: Date,
      required: true
    },
    endDate: {
      type: Date,
      required: true
    },
    timeExtractor: {
      type: Function,
      required: true
    },
    valueExtractor: {
      type: Function,
      required: true
    },
    rateSeries: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {
      fullScreen: false
    };
  },
  computed: {
    id() {
      return `${this.repoName}_${this.title.replaceAll(' ', '_')}`;
    },
    icon() {
      return this.fullScreen ? '$close' : '$expand';
    }
  },
  methods: {
    toggleFullScreen() {
      this.fullScreen = !this.fullScreen;
    }
  }
};
</script>

<style scoped>

</style>
