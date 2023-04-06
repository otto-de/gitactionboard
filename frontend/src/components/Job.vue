<template>
  <v-card
    :id="content.name"
    :key="content.name"
    height="90px"
    :class="`${getBuildAndActivityStatus(content)}`"
  >
    <v-card-text class="job-name pa-0 ma-0">
      <v-tooltip text="Open on GitHub">
        <template #activator="{ props }">
          <a
            :id="content.name + '_url'"
            :href="content.webUrl"
            target="_blank"
            v-bind="props"
          >
            {{ content.name }}
          </a>
        </template>
      </v-tooltip>
    </v-card-text>
    <v-tooltip :text="`${hidden? 'Show element':'Hide element'}`">
      <template #activator="{ props }">
        <v-icon
          v-bind="props"
          class="align-self-end"
          :icon="hidden? `mdi-eye`: `mdi-eye-off`"
          size="small"
          @click="$emit('toggleVisibility', content.name)"
        />
      </template>
    </v-tooltip>
  </v-card>
</template>

<script>

export default {
  name: 'Job',
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
  methods: {
    getBuildAndActivityStatus(job) {
      const lastBuildStatusIndicator = this.getBuildStatus(job.lastBuildStatus);
      return job.activity === 'Building'
        ? `job ${lastBuildStatusIndicator} building`
        : `job ${lastBuildStatusIndicator}`;
    },
    getBuildStatus(status) {
      switch (status) {
        case 'Success':
          return 'success';
        case 'Unknown':
          return 'unknown';
        default:
          return 'failure';
      }
    }
  }
};
</script>

<style scoped>
.job-name {
  color: white;
  font-weight: bold;
  font-size: 14px;
  overflow: auto;
  display: inline-grid;
  align-items: center;
  flex-grow: 1;
}

a {
  text-decoration: none;
  color: inherit;
}

.job {
  border-radius: 6px;
  border: 2px solid #000;
  padding: 5px;
  display: flex;
  flex-direction: column;
}

.success {
  background-color: #3a964a;
}

.failure {
  background-color: #e23d2c;
}

.unknown {
  background-color: #6d6a6a;
}

.job.building.success {
  background-image:
    repeating-linear-gradient(
      135deg,
      #3a964a 30px,
      #000 50px
    );
}

.job.building.failure {
  background-image:
    repeating-linear-gradient(
      135deg,
      #e23d2c 30px,
      #000 50px
    );
}

.job.building.unknown {
  background-image:
    repeating-linear-gradient(
      135deg,
      #6d6a6a 30px,
      #000 50px
    );
}
</style>
