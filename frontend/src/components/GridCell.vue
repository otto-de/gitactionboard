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
  </v-card>
</template>

<script>
export default {
  name: 'GridCell',
  props: {
    content: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.grid-cell-name {
  color: white;
  font-weight: bold;
  font-size: 14px;
  height: 100%;
  overflow: auto;
  display: inline-grid;
  align-items: center;
}

a {
  text-decoration: none;
  color: inherit;
}

.grid-cell {
  border-radius: 6px;
  border: 2px solid #000;
  padding: 5px;
}

.failure {
  background-color: #e23d2c;
}

</style>
