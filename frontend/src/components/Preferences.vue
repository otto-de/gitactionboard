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
        <v-card-item class="pt-0 pb-0">
          <v-btn
            :icon="themeIcon"
            flat
            @click="onThemeUpdate"
          />
          <v-label class="ps-2">
            {{ theme === 'light' ? 'Light' : 'Dark' }} Theme
          </v-label>
        </v-card-item>
        <v-card-item class="pt-0 pb-0">
          <v-autocomplete
            v-model="showBuildsDueToTriggeredEvents"
            chips
            closable-chips
            label="Show Builds Due To"
            multiple
            variant="solo"
            hide-selected
            clearable
            hide-no-data
            :items="getAllPossibleTriggeredEvents"
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
import { useTheme } from 'vuetify';
import { getAllPossibleTriggeredEvents, getShowBuildsDueToTriggeredEvents } from '@/services/utils';

export default {
  name: 'Preferences',
  components: { DashboardHeader },
  data() {
    const themeInstance = useTheme();
    return {
      showHealthyBuilds: preferences.showHealthyBuilds,
      maxIdleTime: preferences.maxIdleTime,
      enableMaxIdleTimeOptimization: preferences.enableMaxIdleTimeOptimization,
      themeInstance,
      isDirty: false,
      showBuildsDueToTriggeredEvents: getShowBuildsDueToTriggeredEvents()
    };
  },
  computed: {
    isValid() {
      return this.maxIdleTime >= 0 && !isNaN(this.maxIdleTime);
    },
    isDisabled() {
      return !(this.isValid && this.isDirty);
    },
    themeIcon() {
      return this.theme === 'light' ? 'mdi-weather-sunny' : 'mdi-weather-night';
    },
    theme() {
      return this.themeInstance.global.name;
    },
    getAllPossibleTriggeredEvents() {
      return getAllPossibleTriggeredEvents();
    }
  },
  methods: {
    onThemeUpdate() {
      this.themeInstance.global.name = this.theme === 'light' ? 'dark' : 'light';
      this.modelValueUpdated();
    },
    savePreferences() {
      preferences.enableMaxIdleTimeOptimization = this.enableMaxIdleTimeOptimization;
      preferences.showHealthyBuilds = this.showHealthyBuilds;
      preferences.maxIdleTime = this.maxIdleTime;
      preferences.theme = this.themeInstance.global.name;
      preferences.showBuildsDueToTriggeredEvents = this.showBuildsDueToTriggeredEvents;

      this.isDirty = false;
    },
    modelValueUpdated() {
      this.isDirty = !(this.themeInstance.global.name === preferences.theme &&
          this.showHealthyBuilds === preferences.showHealthyBuilds &&
            this.maxIdleTime === preferences.maxIdleTime &&
            this.enableMaxIdleTimeOptimization === preferences.enableMaxIdleTimeOptimization &&
          this.hasSameShowBuildsDueToTriggeredEvents());
    },
    hasSameShowBuildsDueToTriggeredEvents() {
      const newPreferredTriggeredEvents = Object.keys(this.showBuildsDueToTriggeredEvents);
      const preferredTriggeredEvents = preferences.showBuildsDueToTriggeredEvents;

      if (preferredTriggeredEvents.length === 0 &&
          newPreferredTriggeredEvents.length === getAllPossibleTriggeredEvents().length) { return true; }

      return preferredTriggeredEvents.length === newPreferredTriggeredEvents.length &&
        preferredTriggeredEvents.every((val, index) => val === newPreferredTriggeredEvents[index]);
    }
  }
};
</script>

<style scoped>
.content-container {
    height: 90vh !important;
}
</style>
