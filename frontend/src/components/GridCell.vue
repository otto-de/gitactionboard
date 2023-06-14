<template>
  <v-card
    :id="rootId"
    :key="name"
    height="90px"
    :class="`grid-cell ${appendClassNames}`"
  >
    <v-card-text class="grid-cell-name pa-0 ma-0">
      <v-tooltip text="Open on GitHub">
        <template #activator="{ props }">
          <a
            :id="urlId"
            :href="url"
            target="_blank"
            v-bind="props"
          >
            {{ name }}
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
      <v-tooltip
        v-if="displayToggleVisibility"
        :text="`${hidden? 'Show element':'Hide element'}`"
        data-testid="toggle-visibility-tooltip"
      >
        <template #activator="{ props }">
          <v-icon
            :test-id="`${rootId}-change-visibility-icon`"
            v-bind="props"
            :icon="hidden? `mdi-eye`: `mdi-eye-off`"
            size="small"
            @click="$emit('toggleVisibility', name)"
          />
        </template>
      </v-tooltip>
    </div>
  </v-card>
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
    appendClassNames: {
      default: '',
      type: String
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
    }
  }
};
</script>

<style scoped>

.relative-time{
  text-transform: none !important;
  opacity: 1;
}

.grid-cell-name {
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

.grid-cell {
  color: white;
  border-radius: 6px;
  border: 2px solid  rgb(var(--v-border-color));
  padding: 5px;
  display: flex;
  flex-direction: column;

  &.building {
    &.success {
      background-image:
          repeating-linear-gradient(
              135deg,
              #3a964a 30px,
              #000 50px
          );
    }

    &.failure {
      background-image:
          repeating-linear-gradient(
              135deg,
              #e23d2c 30px,
              #000 50px
          );
    }

    &.unknown {
      background-image:
          repeating-linear-gradient(
              135deg,
              #6d6a6a 30px,
              #000 50px
          );
    }
  }
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
