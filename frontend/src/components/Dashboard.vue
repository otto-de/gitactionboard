<template>
  <v-container
    v-if="loading"
    id="spinner-container"
    class="justify-center fill-height spinner-container"
  >
    <Spinner />
  </v-container>

  <template v-if="!loading">
    <v-container
      v-if="visibleContents.length > 0"
      fluid
    >
      <v-row
        dense
        class="grid-cells"
      >
        <v-col
          v-for="content in visibleContents"
          :key="content"
        >
          <component
            :is="contentDisplayer"
            :content="content"
            :hidden="isHidden(content)"
            @toggle-visibility="toggleVisibility"
          />
        </v-col>
      </v-row>
    </v-container>
    <v-container
      v-if="hiddenContents.length > 0"
      fluid
    >
      <v-toolbar :test-id="`${contentDisplayer.toLowerCase()}-hidden-contents-tool-bar`">
        <v-toolbar-title>{{ hiddenContents.length }} hidden {{ nameOfItems }}</v-toolbar-title>
        <v-spacer />
        <v-tooltip :text="`${showHiddenElements? 'Collapse hidden elements':'Show hidden elements'}`">
          <template #activator="{ props }">
            <v-btn
              icon
              v-bind="props"
            >
              <v-icon
                :test-id="`${contentDisplayer.toLowerCase()}-hidden-contents-tool-bar-icon`"
                :icon="`mdi-${showHiddenElements? 'eye-off': 'eye'}`"
                @click="toggleHiddenElements"
              />
            </v-btn>
          </template>
        </v-tooltip>
      </v-toolbar>
      <v-container
        v-if="showHiddenElements"
        fluid
        class="px-0"
      >
        <v-row
          dense
          class="grid-cells"
        >
          <v-col
            v-for="content in hiddenContents"
            :key="content"
          >
            <component
              :is="contentDisplayer"
              :content="content"
              :hidden="isHidden(content)"
              @toggle-visibility="toggleVisibility"
            />
          </v-col>
        </v-row>
      </v-container>
    </v-container>
    <NoFailures v-if="contents.length === 0" />
    <MaxIdleTimeoutOverlay v-if="stoppedAutoRefresh" />
  </template>
</template>

<script>
import NoFailures from '@/components/NoFailures';
import Spinner from '@/components/Spinner';
import Job from '@/components/Job';
import FailureGridCell from '@/components/FailureGridCell.vue';
import preferences from '@/services/preferences';
import MaxIdleTimeoutOverlay from '@/components/MaxIdleTimeoutOverlay';

const ONE_MINUTE = 60000;

export default {
  name: 'Dashboard',
  components: { MaxIdleTimeoutOverlay, NoFailures, Spinner, Job, FailureGridCell },
  props: {
    enableMaxIdleTimeOptimization: {
      type: Boolean,
      required: true
    },
    maxIdleTime: {
      type: Number,
      required: true
    },
    fetchContents: {
      type: Function,
      required: true
    },
    contentDisplayer: {
      type: String,
      required: true
    },
    hideByKey: {
      type: String,
      required: false,
      default: null
    },
    nameOfItems: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      contents: [],
      hiddenElements: [],
      loading: true,
      idleTime: 0,
      renderPageTimer: null,
      idleTimer: null,
      showHiddenElements: false,
      stoppedAutoRefresh: false
    };
  },
  computed: {
    visibleContents() {
      return this.contents.filter(this.isVisible);
    },
    hiddenContents() {
      return this.contents.filter(this.isHidden);
    }
  },
  mounted() {
    if (this.enableMaxIdleTimeOptimization) {
      this.initiateIdleTimer();
    }
    this.renderPage().then(() => {
      this.loading = false;
      this.renderPageTimer = setInterval(this.renderPage, 5000);
    });
    this.hiddenElements = preferences.hiddenElements[this.nameOfItems] || [];
  },
  beforeUnmount() {
    clearInterval(this.renderPageTimer);
    clearInterval(this.idleTimer);
  },
  methods: {
    initiateIdleTimer() {
      this.resetTimer();
      window.onmousemove = this.resetTimer;
      window.onmousedown = this.resetTimer;
      window.ontouchstart = this.resetTimer;
      window.onclick = this.resetTimer;
      window.onkeypress = this.resetTimer;
    },
    renderPage() {
      if (!this.enableMaxIdleTimeOptimization) {
        return this.fetchData();
      }
      if (this.maxIdleTime >= this.idleTime) return this.fetchData();
      clearInterval(this.renderPageTimer);
      clearInterval(this.idleTimer);
      this.stoppedAutoRefresh = true;
    },
    incrementIdleTime() {
      this.idleTime++;
    },
    resetTimer() {
      clearInterval(this.idleTimer);
      this.idleTime = 0;
      this.idleTimer = setInterval(this.incrementIdleTime, ONE_MINUTE);
    },
    fetchData() {
      return this.fetchContents()
        .then(contents => {
          this.contents = contents;
        })
        .catch((reason) => {
          console.error(reason);
          return Promise.reject(reason);
        });
    },
    toggleVisibility(key) {
      const indexIfThisKeyIsHidden = this.hiddenElements.indexOf(key);
      if (indexIfThisKeyIsHidden !== -1) {
        this.hiddenElements.splice(indexIfThisKeyIsHidden, 1);
      } else {
        this.hiddenElements.push(key);
      }
      preferences.hiddenElements = { ...preferences.hiddenElements, [this.nameOfItems]: this.hiddenElements };
    },
    isVisible(content) {
      return !this.isHidden(content);
    },
    isHidden(content) {
      return this.hideByKey && this.hiddenElements.indexOf(content[this.hideByKey]) !== -1;
    },
    toggleHiddenElements() {
      this.showHiddenElements = !this.showHiddenElements;
    }
  }
};
</script>

<style scoped>
.spinner-container {
    height: 90vh !important;
}

.grid-cells {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
}
</style>
