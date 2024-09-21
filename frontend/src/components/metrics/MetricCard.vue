<template>
  <v-card
    class="border-thin"
  >
    <v-card-title v-if="expanded">
      <MetricCardTitle
        :title="title"
        :on-button-click="onButtonClick"
        expanded
      />
    </v-card-title>
    <v-card-subtitle v-else>
      <MetricCardTitle
        :title="title"
        :on-button-click="onButtonClick"
      />
    </v-card-subtitle>
    <TimeSeriesLineChart
      :id="id"
      :start-date-time="startDate"
      :end-date-time="endDate"
      :data-set="dataSet"
      :time-extractor="timeExtractor"
      :value-extractor="valueExtractor"
      :rate-series="rateSeries"
    />
  </v-card>
</template>

<script>

import TimeSeriesLineChart from '@/components/metrics/TimeSeriesLineChart.vue';
import MetricCardTitle from '@/components/metrics/MetricCardTitle.vue';

export default {
  name: 'MetricCard',
  components: {
    MetricCardTitle,
    TimeSeriesLineChart
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
    onButtonClick: {
      type: Function,
      required: true
    },
    rateSeries: {
      type: Boolean,
      required: false,
      default: false
    },
    expanded: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  computed: {
    id() {
      return `${this.repoName}_${this.title}_${this.idSuffix}`.replaceAll(' ', '_');
    },
    idSuffix() {
      return this.expanded ? 'fullscreen' : 'grid';
    }
  }
};
</script>

<style scoped>

</style>
