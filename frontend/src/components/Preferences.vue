<template>
  <v-container
    fluid
    class="fill-height w-75 justify-center content-container"
  >
    <DashboardHeader sub-header="Preferences" />
    <v-card
      elevation="5"
      class="w-50"
    >
      <v-container fluid>
        <v-card-item class="pt-0 pb-0">
          <v-switch
            v-model="showHealthyBuilds"
            label="Show healthy builds"
            color="success"
            hide-details
            @update:model-value="modelValueUpdated"
          />
        </v-card-item>
        <v-card-item class="pt-0 pb-0">
          <v-switch
            v-model="enableMaxIdleTimeOptimization"
            label="Enable Maximum Idle Time Restriction"
            color="success"
            hide-details
            @update:model-value="modelValueUpdated"
          />
        </v-card-item>
        <v-card-item class="pt-0">
          <v-text-field
            v-model.number="maxIdleTime"
            :disabled="!enableMaxIdleTimeOptimization"
            hide-details
            type="number"
            step="1"
            min="1"
            label="Maximum Idle Time (minutes)"
            @update:model-value="modelValueUpdated"
          />
        </v-card-item>
        <v-divider class="mt-4 mb-2" />
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="success"
            variant="elevated"
            :disabled="isDisabled"
            size="large"
            @click="savePreferences"
          >
            Save
          </v-btn>
        </v-card-actions>
      </v-container>
    </v-card>
  </v-container>
</template>

<script>
import preferences from '@/services/preferences';
import DashboardHeader from '@/components/DashboardHeader.vue';

export default {
  name: 'Preferences',
  components: { DashboardHeader },
  data() {
    return {
      showHealthyBuilds: preferences.showHealthyBuilds,
      maxIdleTime: preferences.maxIdleTime,
      enableMaxIdleTimeOptimization: preferences.enableMaxIdleTimeOptimization,
      isDirty: false
    };
  },
  computed: {
    isValid() {
      return this.maxIdleTime >= 0 && !isNaN(this.maxIdleTime);
    },
    isDisabled() {
      return !(this.isValid && this.isDirty);
    }
  },
  methods: {
    savePreferences() {
      preferences.enableMaxIdleTimeOptimization = this.enableMaxIdleTimeOptimization;
      preferences.showHealthyBuilds = this.showHealthyBuilds;
      preferences.maxIdleTime = this.maxIdleTime;

      this.isDirty = false;
    },
    modelValueUpdated() {
      this.isDirty = !(this.showHealthyBuilds === preferences.showHealthyBuilds &&
            this.maxIdleTime === preferences.maxIdleTime &&
            this.enableMaxIdleTimeOptimization === preferences.enableMaxIdleTimeOptimization);
    }
  }
};
</script>

<style scoped>
.content-container {
    height: 90vh !important;
}
</style>
