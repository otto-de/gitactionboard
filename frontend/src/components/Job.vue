<template>
  <v-card
    :id="content.name"
    :key="content.name"
    height="90px"
    :class="classNames"
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
    <div class="d-flex">
      <v-btn
        v-if="showRelativeTime"
        disabled
        prepend-icon="mdi-clock-time-four-outline"
        size="small"
        variant="text"
        density="compact"
        flat
        class="relative-time pl-1 align-self-center"
      >
        {{ relativeTime }}
      </v-btn>
      <v-spacer />
      <v-tooltip :text="`${hidden? 'Show element':'Hide element'}`">
        <template #activator="{ props }">
          <v-icon
            v-bind="props"
            :icon="hidden? `mdi-eye`: `mdi-eye-off`"
            size="small"
            @click="$emit('toggleVisibility', content.name)"
          />
        </template>
      </v-tooltip>
    </div>
  </v-card>
</template>

<script>

import { getRelativeTime } from '@/services/utils';

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
  computed: {
    relativeTime() {
      return getRelativeTime(this.content.lastBuildTime);
    },
    showRelativeTime() {
      const { activity, lastBuildStatus } = this.content;
      return activity !== 'Building' && lastBuildStatus !== 'Success';
    },
    classNames() {
      return this.content.activity === 'Building'
        ? `job ${this.buildStatusIndicator} building`
        : `job ${this.buildStatusIndicator}`;
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
  }
};
</script>

<style scoped>
.relative-time{
    text-transform: none !important;
    opacity: 1;
}

.job-name {
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
  color: white;
  border-radius: 6px;
  border: 2px solid rgb(var(--v-border-color));
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
