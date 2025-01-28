<template>
  <v-hover v-slot="{ isHovering, props: hoverProps }">
    <v-card
      :id="rootId"
      :key="name"
      :height="`${gridHeight}px`"
      :class="`grid-cell ${statusIndicator}`"
      rounded="6"
      variant="flat"
      v-bind="hoverProps"
    >
      <v-card-text class="grid-cell-name pa-2">
        {{ name }}
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
      <v-chip
        v-if="showRelativeTime && !isHovering"
        prepend-icon="$clock"
        tile
        size="small"
      >
        {{ relativeTime }}
      </v-chip>
      <v-progress-linear
        v-if="inProgress"
        color="orange-darken-4"
        :height="inProgressIndicatorHeight"
        model-value="100"
        striped
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
      return this.buildMonitorViewEnabled ? 90 : 120;
    },
    inProgressIndicatorHeight() {
      return this.buildMonitorViewEnabled ? 15 : 10;
    }
  }
};
</script>

<style scoped>

.grid-cell-name {
  font-weight: bold;
  font-size: 14px;
  overflow: auto;
  display: inline-grid;
  align-items: center;
  flex-grow: 1;
}

.grid-cell {
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

</style>
