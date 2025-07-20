<template>
  <v-container
    fluid
    class="justify-center content-container"
  >
    <DashboardHeader sub-header="Preferences" />

    <v-row class="justify-center">
      <v-col
        cols="12"
        md="8"
        lg="7"
      >
        <v-card
          elevation="5"
          class="mb-6"
        >
          <v-card-title class="text-h5 py-4 px-6">
            Build & Monitoring Settings
          </v-card-title>
          <v-divider />
          <v-card-text class="py-4 px-6">
            <v-switch
              v-model="showHealthyBuilds"
              label="Show healthy builds"
              color="success"
              hide-details
              class="mb-4"
              @update:model-value="modelValueUpdated"
            />
            <v-switch
              v-model="enableBuildMonitorView"
              label="Enable build monitor view"
              color="success"
              hide-details
              class="mb-4"
              @update:model-value="modelValueUpdated"
            />
            <v-switch
              v-model="enableMaxIdleTimeOptimization"
              label="Pause Auto-Refresh on Idle"
              color="success"
              hide-details
              class="mb-4"
              @update:model-value="modelValueUpdated"
            />
            <v-text-field
              v-model.number="maxIdleTime"
              :disabled="!enableMaxIdleTimeOptimization"
              hide-details
              type="number"
              step="1"
              min="1"
              label="Maximum Idle Timeout"
              suffix="minutes"
              clearable
              class="mb-4"
              @update:model-value="modelValueUpdated"
            >
              <template #append-inner>
                <v-tooltip
                  location="bottom"
                  text="The duration of user inactivity after which the page will pause its automatic data refresh."
                >
                  <template #activator="{ props }">
                    <v-icon
                      v-bind="props"
                      icon="$information"
                      class="opacity-50"
                      size="x-small"
                    />
                  </template>
                </v-tooltip>
              </template>
            </v-text-field>
            <div class="d-flex align-center mt-4 pa-3 rounded border border-gray-200">
              <v-btn
                :icon="themeIcon"
                variant="text"
                size="small"
                @click="onThemeUpdate"
              />
              <v-label class="ml-2 text-subtitle-1">
                {{ theme === 'light' ? 'Light' : 'Dark' }} Theme
              </v-label>
            </div>
          </v-card-text>
        </v-card>

        <v-expansion-panels
          class="mb-6"
          variant="accordion"
        >
          <v-expansion-panel>
            <v-expansion-panel-title class="text-h5 py-4 px-6">
              Filter Builds by Triggered Event Type
            </v-expansion-panel-title>
            <v-expansion-panel-text class="py-4 px-6">
              <p class="text-subtitle-1 text-medium-emphasis mb-6">
                Select the event types for which you want to view builds. Only builds associated with
                the checked event types will be displayed.
              </p>

              <v-row
                v-for="(category, categoryTitle) in categorizedEvents"
                :key="categoryTitle"
                dense
              >
                <v-col cols="12">
                  <h3 class="text-h5 mb-3 text-medium-emphasis pb-2 border-b border-gray-300">
                    {{ categoryTitle }}
                  </h3>
                </v-col>
                <v-col
                  v-for="(eventDetail, eventKey) in category"
                  :key="eventKey"
                  cols="12"
                  sm="6"
                  md="4"
                >
                  <v-checkbox
                    v-model="showBuildsDueToTriggeredEvents"
                    :value="eventKey"
                    color="success"
                    hide-details
                    density="compact"
                    class="mb-1"
                    @update:model-value="modelValueUpdated"
                  >
                    <template #label>
                      <span>{{ eventDetail.name }}</span>
                      <v-tooltip
                        location="bottom"
                        :text="eventDetail.description"
                      >
                        <template #activator="{ props }">
                          <!-- Consistent icon size for tooltips -->
                          <v-icon
                            v-bind="props"
                            size="x-small"
                            icon="$information"
                            class="ml-1 opacity-50"
                          />
                        </template>
                      </v-tooltip>
                    </template>
                  </v-checkbox>
                </v-col>
              </v-row>

              <v-btn
                variant="outlined"
                color="success"
                size="default"
                width="200px"
                class="mt-6"
                @click="toggleAllEventsSelection"
              >
                {{ areAllEventsSelected ? 'Deselect All Events' : 'Select All Events' }}
              </v-btn>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>

        <v-card-actions class="justify-end px-0 py-0">
          <v-btn
            color="success"
            variant="elevated"
            :disabled="isDisabled"
            size="large"
            @click="savePreferences"
          >
            Save Changes
          </v-btn>
        </v-card-actions>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import preferences from '@/services/preferences';
import DashboardHeader from '@/components/DashboardHeader.vue';
import { useTheme } from 'vuetify';
import {
  getAllPossibleTriggeredEvents,
  getCategorisedAllPossibleTriggeredEvents,
  getShowBuildsDueToTriggeredEvents
} from '@/services/utils';

export default {
  name: 'Preferences',
  components: { DashboardHeader },
  data() {
    const themeInstance = useTheme();
    return {
      showHealthyBuilds: preferences.showHealthyBuilds,
      enableBuildMonitorView: preferences.enableBuildMonitorView,
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
      return this.theme === 'light' ? '$light' : '$dark';
    },
    theme() {
      return this.themeInstance.global.name;
    },
    allPossibleTriggeredEvents() {
      return getAllPossibleTriggeredEvents();
    },
    categorizedEvents() {
      return getCategorisedAllPossibleTriggeredEvents();
    },
    areAllEventsSelected() {
      const selectedEvents = new Set(this.showBuildsDueToTriggeredEvents);

      return selectedEvents.size === this.allPossibleTriggeredEvents.length &&
          this.allPossibleTriggeredEvents.every(event => selectedEvents.has(event));
    }
  },
  methods: {
    onThemeUpdate() {
      this.themeInstance.global.name = this.theme === 'light' ? 'dark' : 'light';
      this.modelValueUpdated();
    },
    savePreferences() {
      preferences.enableMaxIdleTimeOptimization = this.enableMaxIdleTimeOptimization;
      preferences.enableBuildMonitorView = this.enableBuildMonitorView;
      preferences.showHealthyBuilds = this.showHealthyBuilds;
      preferences.maxIdleTime = this.maxIdleTime;
      preferences.theme = this.themeInstance.global.name;
      preferences.showBuildsDueToTriggeredEvents = this.showBuildsDueToTriggeredEvents;

      this.isDirty = false;
    },
    modelValueUpdated() {
      this.isDirty = !(this.themeInstance.global.name === preferences.theme &&
          this.showHealthyBuilds === preferences.showHealthyBuilds &&
          this.enableBuildMonitorView === preferences.enableBuildMonitorView &&
          this.maxIdleTime === preferences.maxIdleTime &&
          this.enableMaxIdleTimeOptimization === preferences.enableMaxIdleTimeOptimization &&
          this.hasSameShowBuildsDueToTriggeredEvents());
    },
    hasSameShowBuildsDueToTriggeredEvents() {
      const preferredTriggeredEvents = preferences.showBuildsDueToTriggeredEvents;

      if (preferredTriggeredEvents.length === 0) {
        return this.showBuildsDueToTriggeredEvents.length === this.allPossibleTriggeredEvents.length;
      }

      const newPreferredTriggeredEvents = new Set(this.showBuildsDueToTriggeredEvents);

      return newPreferredTriggeredEvents.size === preferredTriggeredEvents.length &&
          preferredTriggeredEvents.every(event => newPreferredTriggeredEvents.has(event));
    },
    toggleAllEventsSelection() {
      if (this.areAllEventsSelected) {
        this.showBuildsDueToTriggeredEvents = [];
      } else {
        this.showBuildsDueToTriggeredEvents = [...this.allPossibleTriggeredEvents];
      }

      this.modelValueUpdated();
    }
  }
};
</script>

<style scoped>
.content-container {
  height: 90vh !important;
}
</style>
