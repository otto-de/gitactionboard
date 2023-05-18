<template>
  <v-card
    :id="content.id"
    :key="content.id"
    height="90px"
    class="grid-cell failure"
  >
    <v-card-text class="grid-cell-name pa-0 ma-0">
      <v-tooltip text="Open on GitHub">
        <template #activator="{ props }">
          <a
            :id="content.id + '_url'"
            :href="content.url"
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
    </div>
  </v-card>
</template>

<script>
import { getRelativeTime } from '@/services/utils';

export default {
  name: 'GridCell',
  props: {
    content: {
      type: Object,
      required: true
    }
  },
  computed: {
    relativeTime() {
      return getRelativeTime(this.content.createdAt);
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

.grid-cell {
    border-radius: 6px;
    border: 2px solid;
    padding: 5px;
    display: flex;
    flex-direction: column;
}

.failure {
  background-color: #e23d2c;
}

</style>
