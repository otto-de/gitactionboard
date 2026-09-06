<template>
  <v-hover v-slot="{ isHovering, props: hoverProps }">
    <v-card
      :id="rootId"
      :key="name"
      :height="`${gridHeight}px`"
      :class="['grid-cell', { 'build-monitor': buildMonitorViewEnabled }]"
      rounded="8"
      variant="flat"
      v-bind="hoverProps"
    >
      <span :class="['status-bar', statusIndicator]" />
      <v-card-text class="grid-cell-name pa-0">
        <v-card-subtitle
          v-if="repoName"
          class="card-repo font-mono pa-0"
        >
          {{ repoName }}
        </v-card-subtitle>
        <v-card-title class="card-title pa-0">
          {{ title }}
        </v-card-title>
      </v-card-text>
      <v-expand-transition class="bg-transparent">
        <v-toolbar
          v-if="isHovering"
          :test-id="`${rootId}-toolbar`"
          height="24"
        >
          <v-chip
            v-if="showRelativeTime"
            prepend-icon="$clock"
            density="compact"
            size="small"
            pill
          >
            {{ relativeTime }}
          </v-chip>
          <v-spacer />
          <v-tooltip text="View on GitHub">
            <template #activator="{ props }">
              <v-btn
                :href="url"
                icon="$openInNewWindow"
                target="_blank"
                v-bind="props"
                size="small"
                :data-testid="urlId"
              />
            </template>
          </v-tooltip>

          <v-tooltip
            v-if="displayToggleVisibility"
            :text="`${hidden? 'Show element':'Hide element'}`"
            :test-id="`${rootId}-toggle-visibility-tooltip`"
          >
            <template #activator="{ props }">
              <v-btn
                :test-id="`${rootId}-change-visibility-icon`"
                v-bind="props"
                :icon="hidden? `$view`: `$hide`"
                size="small"
                @click="$emit('toggleVisibility', name)"
              />
            </template>
          </v-tooltip>
        </v-toolbar>
      </v-expand-transition>
      <div
        v-if="!isHovering"
        class="card-meta"
      >
        <template v-if="showRelativeTime">
          {{ relativeTime }}
        </template>
        <v-chip
          v-if="runLabel"
          class="meta-tag font-mono"
          density="compact"
          size="x-small"
          variant="flat"
        >
          #{{ runLabel }}
        </v-chip>
      </div>
      <div
        v-if="inProgress"
        class="progress-bar"
      />
    </v-card>
  </v-hover>
</template>

<script>
import { getRelativeTime } from '@/services/utils';

export default {
  name: 'GridCell',
  props: {
    name: {
      type: String,
      required: true
    },
    url: {
      type: String,
      required: true
    },
    lastExecutedTime: {
      type: String,
      required: true
    },
    inProgress: {
      type: Boolean,
      default: false
    },
    status: {
      type: String,
      default: ''
    },
    hidden: {
      type: Boolean,
      default: false
    },
    displayToggleVisibility: {
      type: Boolean,
      default: false
    },
    showRelativeTime: {
      type: Boolean,
      default: false
    },
    buildMonitorViewEnabled: {
      type: Boolean,
      required: true
    },
    runLabel: {
      type: String,
      default: null
    }
  },
  emits: ['toggleVisibility'],
  computed: {
    relativeTime() {
      return getRelativeTime(this.lastExecutedTime);
    },
    rootId() {
      return this.name.replaceAll(/[\\:\s]/g, '-');
    },
    urlId() {
      return `${this.rootId}-url`;
    },
    nameParts() {
      return this.name.split(' :: ');
    },
    repoName() {
      return this.nameParts.length > 1 ? this.nameParts[0] : null;
    },
    title() {
      return this.nameParts.length > 1 ? this.nameParts.slice(1).join(' :: ') : this.name;
    },
    statusIndicator() {
      switch (this.status.toLowerCase()) {
        case 'success':
          return 'success';
        case 'unknown':
          return 'unknown';
        default:
          return 'failure';
      }
    },
    gridHeight() {
      return this.buildMonitorViewEnabled ? 90 : 112;
    }
  }
};
</script>

<style scoped>

.grid-cell {
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  padding: 11px 12px 11px 15px;
  box-shadow: 0 1px 2px rgb(20 22 26 / 4%), 0 8px 24px -12px rgb(20 22 26 / 12%);
}

/* stylelint-disable-next-line selector-class-pattern -- Vuetify's runtime theme class, not ours to rename */
.v-theme--dark .grid-cell {
  box-shadow: 0 1px 2px rgb(0 0 0 / 40%), 0 8px 24px -8px rgb(0 0 0 / 50%);
}

.status-bar {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.grid-cell.build-monitor .status-bar {
  width: 5px;
}

.status-bar.success {
  background: rgb(var(--v-theme-success));
}

.status-bar.failure {
  background: rgb(var(--v-theme-error));
}

.status-bar.unknown {
  background: #8a8f9c;
}

.grid-cell-name {
  flex-grow: 1;
  overflow: hidden;
}

.card-repo {
  font-size: 10.5px;
  font-weight: 600;
  color: rgb(var(--v-theme-on-surface) / var(--v-medium-emphasis-opacity));
  margin-bottom: 3px;
}

.grid-cell.build-monitor .card-repo {
  font-size: 12px;
}

.card-title {
  font-size: 12px;
  font-weight: 600;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: normal;
}

.grid-cell.build-monitor .card-title {
  font-size: 14px;
  -webkit-line-clamp: 1;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 10.5px;
  color: rgb(var(--v-theme-on-surface) / var(--v-medium-emphasis-opacity));
  margin-top: auto;
  padding-top: 7px;
}

.grid-cell.build-monitor .card-meta {
  font-size: 11.5px;
}

.meta-tag {
  font-size: 9.5px;
}

.progress-bar {
  position: absolute;
  left: 4px;
  right: 0;
  bottom: 0;
  height: 3px;
  overflow: hidden;
  background-color: rgb(var(--v-theme-warning) / 18%);
  background-image: linear-gradient(
    45deg,
    rgb(var(--v-theme-warning)) 25%, transparent 25%, transparent 50%,
    rgb(var(--v-theme-warning)) 50%, rgb(var(--v-theme-warning)) 75%, transparent 75%, transparent
  );
  background-size: 10px 10px;
  background-repeat: repeat;
  animation: progress-marquee 0.9s linear infinite;
}

.grid-cell.build-monitor .progress-bar {
  left: 5px;
  height: 4px;
}

@keyframes progress-marquee {
  from { background-position: 0 0; }
  to { background-position: 10px 0; }
}

</style>
