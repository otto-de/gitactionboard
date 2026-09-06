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
          elevation="0"
          class="settings-card mb-6"
        >
          <v-list lines="two">
            <v-list-subheader>Board behavior</v-list-subheader>

            <v-list-item>
              <v-list-item-title>Show healthy builds</v-list-item-title>
              <v-list-item-subtitle>Display builds that are passing on the board</v-list-item-subtitle>
              <template #append>
                <v-switch
                  v-model="showHealthyBuilds"
                  color="primary"
                  hide-details
                  @update:model-value="modelValueUpdated"
                />
              </template>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Enable Build Monitor view</v-list-item-title>
              <v-list-item-subtitle>Denser grid with larger tiles for at-a-glance monitoring</v-list-item-subtitle>
              <template #append>
                <v-switch
                  v-model="enableBuildMonitorView"
                  color="primary"
                  hide-details
                  @update:model-value="modelValueUpdated"
                />
              </template>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Pause auto-refresh on idle</v-list-item-title>
              <v-list-item-subtitle>Stops polling after a period of inactivity</v-list-item-subtitle>
              <template #append>
                <v-switch
                  v-model="enableMaxIdleTimeOptimization"
                  color="primary"
                  hide-details
                  @update:model-value="modelValueUpdated"
                />
              </template>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Maximum idle timeout</v-list-item-title>
              <v-list-item-subtitle>Automatic refresh pauses after this many idle minutes</v-list-item-subtitle>
              <template #append>
                <v-text-field
                  v-model.number="maxIdleTime"
                  :disabled="!enableMaxIdleTimeOptimization"
                  class="idle-timeout-field"
                  hide-details
                  type="number"
                  step="1"
                  min="1"
                  suffix="min"
                  density="compact"
                  @update:model-value="modelValueUpdated"
                />
              </template>
            </v-list-item>
          </v-list>

          <v-divider />
          <v-list-subheader class="px-6">
            Filters
          </v-list-subheader>
          <v-expansion-panels
            flat
            variant="accordion"
          >
            <v-expansion-panel>
              <v-expansion-panel-title
                class="px-6"
                expand-icon="$chevronDown"
              >
                <span>Filter Builds by Triggered Event Type</span>
                <v-spacer />
                <v-chip
                  size="small"
                  variant="tonal"
                  color="primary"
                  class="mr-2"
                >
                  {{ eventsCountLabel }}
                </v-chip>
              </v-expansion-panel-title>
              <v-expansion-panel-text class="py-4 px-6">
                <p class="text-subtitle-1 text-medium-emphasis mb-6">
                  Select the event types for which you want to view builds. Only builds associated with
                  the checked event types will be displayed.
                </p>

                <v-text-field
                  v-model="eventSearchQuery"
                  label="Search Event Types"
                  prepend-inner-icon="$search"
                  variant="outlined"
                  clearable
                  density="compact"
                  class="mb-4"
                />
                <v-row
                  v-for="(category, categoryTitle) in filteredCategorizedEvents"
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
                      color="primary"
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
                <p
                  v-if="filteredEventKeys.length === 0"
                  class="text-medium-emphasis"
                >
                  No event types found matching your search.
                </p>

                <v-btn
                  v-if="filteredEventKeys.length > 0"
                  variant="outlined"
                  color="primary"
                  size="default"
                  width="200px"
                  class="mt-6"
                  @click="toggleAllEventsSelection"
                >
                  {{ selectDeselectAllEventsLabel }}
                </v-btn>
              </v-expansion-panel-text>
            </v-expansion-panel>

            <v-expansion-panel>
              <v-expansion-panel-title
                class="px-6"
                expand-icon="$chevronDown"
              >
                <span>Filter Builds by Branch Name</span>
                <v-spacer />
                <v-chip
                  size="small"
                  variant="tonal"
                  color="primary"
                  class="mr-2"
                >
                  {{ branchesCountLabel }}
                </v-chip>
              </v-expansion-panel-title>
              <v-expansion-panel-text class="py-4 px-6">
                <p class="text-subtitle-1 text-medium-emphasis mb-6">
                  Select the branch names for which you want to view builds. Only builds associated with
                  the checked branches will be displayed.
                </p>

                <v-text-field
                  v-model="branchSearchQuery"
                  label="Search Branches"
                  prepend-inner-icon="$search"
                  variant="outlined"
                  clearable
                  density="compact"
                  class="mb-4"
                />
                <v-row dense>
                  <v-col
                    v-for="branch in filteredBranchNames"
                    :key="branch"
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-checkbox
                      v-model="showBuildsForBranches"
                      :value="branch"
                      color="primary"
                      hide-details
                      density="compact"
                      class="mb-1"
                      :label="branch"
                      @update:model-value="modelValueUpdated"
                    />
                  </v-col>
                  <v-col
                    v-if="filteredBranchNames.length === 0"
                    cols="12"
                  >
                    <p class="text-medium-emphasis">
                      No branches found matching your search.
                    </p>
                  </v-col>
                </v-row>

                <v-btn
                  v-if="filteredBranchNames.length > 0"
                  variant="outlined"
                  color="primary"
                  size="default"
                  width="310px"
                  class="mt-6"
                  @click="toggleAllBranchesSelection"
                >
                  {{ selectDeselectAllBranchesLabel }}
                </v-btn>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>

          <v-divider />
          <v-list-subheader class="px-6">
            Appearance
          </v-list-subheader>
          <v-list>
            <v-list-item @click="onThemeUpdate">
              <template #prepend>
                <v-icon :icon="themeIcon" />
              </template>
              <v-list-item-title>{{ theme === 'light' ? 'Light' : 'Dark' }} Theme</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>

        <v-card-actions class="justify-end px-0 py-0">
          <v-btn
            color="primary"
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

import { fetchBranchNames } from '@/services/apiService';

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
      showBuildsDueToTriggeredEvents: getShowBuildsDueToTriggeredEvents(),
      eventSearchQuery: '',
      showBuildsForBranches: preferences.showBuildsForBranches || [],
      allPossibleBranchNames: [],
      branchSearchQuery: '',
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
    },
    eventsCountLabel() {
      return this.areAllEventsSelected
        ? 'All'
        : `${this.showBuildsDueToTriggeredEvents.length} of ${this.allPossibleTriggeredEvents.length}`;
    },
    filteredCategorizedEvents() {
      if (!this.eventSearchQuery) {
        return this.categorizedEvents;
      }
      const query = this.eventSearchQuery.toLowerCase();
      return Object.entries(this.categorizedEvents).reduce((categoriesAcc, [categoryTitle, events]) => {
        const filteredEvents = Object.entries(events)
          .filter(([, eventDetail]) => eventDetail.name.toLowerCase().includes(query))
          .reduce((eventsAcc, [eventKey, eventDetail]) => ({ ...eventsAcc, [eventKey]: eventDetail }), {});
        return Object.keys(filteredEvents).length === 0
          ? categoriesAcc
          : { ...categoriesAcc, [categoryTitle]: filteredEvents };
      }, {});
    },
    filteredEventKeys() {
      return Object.values(this.filteredCategorizedEvents).flatMap(events => Object.keys(events));
    },
    areAllEventsSelectedFiltered() {
      if (this.filteredEventKeys.length === 0) {
        return false;
      }
      const selectedEvents = new Set(this.showBuildsDueToTriggeredEvents);
      return this.filteredEventKeys.every(eventKey => selectedEvents.has(eventKey));
    },
    selectDeselectAllEventsLabel() {
      const filtered = !!this.eventSearchQuery;
      return this.areAllEventsSelectedFiltered
        ? `Deselect All ${filtered ? 'Filtered ' : ''}Events`
        : `Select All ${filtered ? 'Filtered ' : ''}Events`;
    },
    branchesCountLabel() {
      if (this.allPossibleBranchNames.length === 0) {
        return 'All';
      }
      return this.showBuildsForBranches.length === this.allPossibleBranchNames.length
        ? 'All'
        : `${this.showBuildsForBranches.length} of ${this.allPossibleBranchNames.length}`;
    },
    filteredBranchNames() {
      if (!this.branchSearchQuery) {
        return this.allPossibleBranchNames;
      }
      const query = this.branchSearchQuery.toLowerCase();
      return this.allPossibleBranchNames.filter(branch =>
        branch.toLowerCase().includes(query)
      );
    },
    areAllBranchesSelectedFiltered() {
      if (this.filteredBranchNames.length === 0) {
        return false;
      }
      const selectedBranches = new Set(this.showBuildsForBranches);
      return this.filteredBranchNames.every(branch => selectedBranches.has(branch));
    },
    selectDeselectAllBranchesLabel() {
      const filtered = !!this.branchSearchQuery;
      return this.areAllBranchesSelectedFiltered
        ? `Deselect All ${filtered ? 'Filtered ' : ''}Branches`
        : `Select All ${filtered ? 'Filtered ' : ''}Branches`;
    },
  },
  async mounted() {
    try {
      this.allPossibleBranchNames = (await fetchBranchNames()).sort();
      if (!preferences.showBuildsForBranches || preferences.showBuildsForBranches.length === 0) {
        this.showBuildsForBranches = [...this.allPossibleBranchNames];
      } else {
        this.showBuildsForBranches = preferences.showBuildsForBranches.filter(branch =>
          this.allPossibleBranchNames.includes(branch)
        );
        this.modelValueUpdated();
      }
    } catch (error) {
      console.error('Failed to fetch branch names:', error);
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
      preferences.showBuildsForBranches = this.showBuildsForBranches;

      this.isDirty = false;
    },
    modelValueUpdated() {
      this.isDirty = !(this.themeInstance.global.name === preferences.theme &&
          this.showHealthyBuilds === preferences.showHealthyBuilds &&
          this.enableBuildMonitorView === preferences.enableBuildMonitorView &&
          this.maxIdleTime === preferences.maxIdleTime &&
          this.enableMaxIdleTimeOptimization === preferences.enableMaxIdleTimeOptimization &&
          this.hasSameShowBuildsDueToTriggeredEvents() &&
          this.hasSameShowBuildsForBranches());
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
    hasSameShowBuildsForBranches() {
      const preferredBranches = preferences.showBuildsForBranches;

      if (!preferredBranches || preferredBranches.length === 0) {
        return this.showBuildsForBranches.length === this.allPossibleBranchNames.length &&
            this.allPossibleBranchNames.every(branch => this.showBuildsForBranches.includes(branch));
      }

      const currentSelectedBranches = new Set(this.showBuildsForBranches);
      return currentSelectedBranches.size === preferredBranches.length &&
          preferredBranches.every(branch => currentSelectedBranches.has(branch));
    },
    toggleAllEventsSelection() {
      const currentSelectionsSet = new Set(this.showBuildsDueToTriggeredEvents);

      if (this.areAllEventsSelectedFiltered) {
        this.filteredEventKeys.forEach(eventKey => currentSelectionsSet.delete(eventKey));
      } else {
        this.filteredEventKeys.forEach(eventKey => currentSelectionsSet.add(eventKey));
      }

      this.showBuildsDueToTriggeredEvents = Array.from(currentSelectionsSet);
      this.modelValueUpdated();
    },
    toggleAllBranchesSelection() {
      const currentSelectionsSet = new Set(this.showBuildsForBranches);

      if (this.areAllBranchesSelectedFiltered) {
        this.filteredBranchNames.forEach(branch => currentSelectionsSet.delete(branch));
      } else {
        this.filteredBranchNames.forEach(branch => currentSelectionsSet.add(branch));
      }

      this.showBuildsForBranches = Array.from(currentSelectionsSet);

      this.modelValueUpdated();
    },
  },
};
</script>

<style scoped>
.content-container {
  height: 90vh !important;
}

.idle-timeout-field {
  width: 100px;
}
</style>
