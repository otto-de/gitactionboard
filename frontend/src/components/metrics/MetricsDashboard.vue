<template>
  <v-container
    id="metrics-dashboard"
    fluid
  >
    <DashboardHeader sub-header="Metrics" />
    <v-toolbar
      elevation="4"
      class="mb-2"
    >
      <v-spacer />
      <DateRangePicker
        label="Date Range"
        class="mr-2"
        @date-range-updated="dateRangeUpdated"
      />
      <v-btn
        text="Expand all"
        test-id="expand-all"
        class="text-none"
        rounded
        ripple
        @click="expandAllPanels"
      />
      <v-btn
        text="Collapse all"
        class="text-none"
        test-id="collapse-all"
        rounded
        ripple
        @click="collapseAllPanels"
      />
    </v-toolbar>
    <v-container
      v-if="loading"
      id="spinner-container"
      class="justify-center fill-height spinner-container"
    >
      <Spinner />
    </v-container>
    <v-expansion-panels
      v-else
      v-model="expandedPanels"
      multiple
      static
      test-id="metrics-dashboard-expansion-panels"
    >
      <v-expansion-panel
        v-for="repoName in repositoryNames"
        :key="repoName"
        :title="repoName"
        :value="repoName"
        :test-id="`metrics-dashboard-expansion-panel-${repoName}`"
      >
        <v-expansion-panel-text>
          <RepositoryMetrics
            :repo-name="repoName"
            :from="startDate"
            :to="endDate"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<script>
import DashboardHeader from '@/components/DashboardHeader.vue';
import { fetchRepositoryNames } from '@/services/apiService';
import Spinner from '@/components/Spinner.vue';
import RepositoryMetrics from '@/components/metrics/RepositoryMetrics.vue';
import DateRangePicker from '@/components/DateRangePicker.vue';

export default {
  name: 'MetricsDashboard',
  components: { DateRangePicker, RepositoryMetrics, Spinner, DashboardHeader },
  data() {
    return {
      loading: true,
      repositoryNames: [],
      startDate: null,
      endDate: null,
      expandedPanels: []
    };
  },
  mounted() {
    fetchRepositoryNames()
      .then(repositoryNames => {
        this.loading = false;
        this.repositoryNames = repositoryNames;
      }).catch(reason => {
        console.error(reason);
        throw reason;
      });
  },
  methods: {
    dateRangeUpdated(from, to) {
      this.startDate = from;
      this.endDate = to;
    },
    expandAllPanels() {
      this.expandedPanels = this.repositoryNames;
    },
    collapseAllPanels() {
      this.expandedPanels = [];
    }
  }
};
</script>

<style scoped>
.spinner-container {
  height: 90vh !important;
}
</style>
