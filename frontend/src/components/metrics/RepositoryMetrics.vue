<template>
  <v-container
    :id="`${repoName}-metrics`"
    fluid
  >
    <div class="metric-cards-grid">
      <MetricCardContainer
        title="Workflow Run Frequency"
        :repo-name="repoName"
        :start-date="startDate"
        :end-date="endDate"
        :data-set="workflowRunMetrics"
        :time-extractor="workflowRunStartedAtExtractor"
        :value-extractor="workflowRunCountExtractor"
      />

      <MetricCardContainer
        title="Workflow Run Failure Rate"
        :repo-name="repoName"
        :start-date="startDate"
        :end-date="endDate"
        :data-set="workflowRunMetrics"
        :time-extractor="workflowRunStartedAtExtractor"
        :value-extractor="workflowRunFailureCountExtractor"
        rate-series
      />

      <MetricCardContainer
        title="Workflow Re-trigger Rate"
        :repo-name="repoName"
        :start-date="startDate"
        :end-date="endDate"
        :data-set="workflowRunMetrics"
        :time-extractor="workflowRunStartedAtExtractor"
        :value-extractor="workflowRetriggeredCountExtractor"
        rate-series
      />

      <MetricCardContainer
        title="Average Workflow Run Time (Minutes)"
        :repo-name="repoName"
        :start-date="startDate"
        :end-date="endDate"
        :data-set="workflowRunMetrics"
        :time-extractor="workflowRunStartedAtExtractor"
        :value-extractor="workflowRunTimeExtractor"
        rate-series
      />
    </div>
  </v-container>
</template>

<script>
import { fetchWorkflowRunMetrics } from '@/services/apiService';
import { watch } from 'vue';
import { differenceInMinutes } from 'date-fns';
import MetricCardContainer from '@/components/metrics/MetricCardContainer.vue';

export default {
  name: 'RepositoryMetrics',
  components: { MetricCardContainer },
  props: {
    repoName: {
      type: String,
      required: true
    },
    from: {
      type: Date,
      required: true
    },
    to: {
      type: Date,
      required: true
    }
  },
  data() {
    return {
      workflowRunMetrics: {}
    };
  },
  computed: {
    workflowRunFrequencyMetricId() {
      return `${this.repoName}-workflow-run-frequency-metric`;
    },
    workflowRunFailureRateMetricId() {
      return `${this.repoName}-workflow-run-failure-rate-metric`;
    },
    workflowRetriggeredRateMetricId() {
      return `${this.repoName}-workflow-re-triggered-rate-metric`;
    },
    workflowAvgRunTimeMetricId() {
      return `${this.repoName}-workflow-avg-run-time-metric`;
    },
    startDate() {
      return this.from;
    },
    endDate() {
      return this.to;
    }
  },
  mounted() {
    this.getWorkflowRunMetrics();

    watch(() => [this.from, this.to], () => {
      this.getWorkflowRunMetrics();
    });
  },
  methods: {
    workflowRunStartedAtExtractor({ startedAt }) {
      return startedAt;
    },
    workflowRunCountExtractor() {
      return 1;
    },
    workflowRunFailureCountExtractor({ conclusion }) {
      return conclusion === 'FAILURE' || conclusion === 'EXCEPTION' ? 1 : 0;
    },
    workflowRetriggeredCountExtractor({ runAttempt }) {
      return runAttempt > 1 ? 1 : 0;
    },
    workflowRunTimeExtractor({ startedAt, completedAt }) {
      return differenceInMinutes(completedAt, startedAt);
    },
    getWorkflowRunMetrics() {
      fetchWorkflowRunMetrics(this.repoName, this.startDate, this.endDate)
        .then(workflowRunMetrics => {
          this.workflowRunMetrics = workflowRunMetrics;
        });
    }
  }
};
</script>

<style scoped>
.metric-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 12px;
}
</style>
