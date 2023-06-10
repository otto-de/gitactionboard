<template>
  <v-dialog
    v-model="displayOverlay"
    persistent
    width="auto"
    attach
  >
    <v-card class="pa-3 rounded-lg">
      <v-alert
        color="warning"
        icon="$warning"
        title="Stopped automatic page refreshment due to maximum idle timeout"
      />
      <v-card-actions class="pt-10">
        <v-spacer />
        <v-btn
          elevation="3"
          prepend-icon="mdi-refresh"
          test-id="reload-button"
          @click="reloadPage"
        >
          Reload Page
        </v-btn>
        <v-spacer />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>
export default {
  name: 'MaxIdleTimeoutOverlay',
  data() {
    return {
      displayOverlay: true
    };
  },
  methods: {
    reloadPage() {
      window.location.reload();
    }
  }
};
</script>
