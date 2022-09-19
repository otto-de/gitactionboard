<template>
  <template v-if="loading">
    <Spinner />
  </template>

  <template v-if="!loading">
    <div class="content-container">
      <template
        v-for="content in visibleContents"
        :key="content"
      >
        <component
          :is="contentDisplayer"
          :content="content"
          :hidden="isHidden(content)"
          @toggle-visibility="toggleVisibility"
        />
      </template>
    </div>
    <div
      v-if="hiddenContents.length > 0"
      class="hidden-elements"
    >
      <hr>
      {{ hiddenContents.length }} hidden {{ nameOfItems }}
      <button @click="toggleHiddenElements()">
        {{ showHiddenElements ? 'hide' : 'show' }}
      </button>
      <div
        v-if="showHiddenElements"
        class="content-container"
      >
        <template
          v-for="content in hiddenContents"
          :key="content"
        >
          <component
            :is="contentDisplayer"
            :content="content"
            :hidden="isHidden(content)"
            @toggle-visibility="toggleVisibility"
          />
        </template>
      </div>
    </div>
  </template>
  <NoFailures v-if="!loading && contents.length === 0" />
</template>

<script>
import NoFailures from "@/components/Happy";
import Spinner from "@/components/Spinner";
import Job from "@/components/Job";
import GridCell from "@/components/GridCell";
import preferences from "@/services/preferences";

const ONE_MINUTE = 60000;

export default {
  name: "Dashboard",
  components: {NoFailures, Spinner, Job, GridCell },
  props: {
    disableMaxIdleTime: {
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
      showHiddenElements: false
    }
  },
  computed: {
    visibleContents() {
      return this.contents.filter(this.isVisible)
    },
    hiddenContents() {
      return this.contents.filter(this.isHidden)
    }
  },
  mounted() {
    if (!this.disableMaxIdleTime) {
      this.initiateIdleTimer();
    }
    this.renderPage().then(() => {
      this.loading = false;
      this.renderPageTimer = setInterval(this.renderPage, 5000);
    });
    this.hiddenElements = preferences.hiddenElements[this.nameOfItems] || []
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
      if (this.disableMaxIdleTime) {
        return this.fetchData();
      }
      if (this.maxIdleTime >= this.idleTime) return this.fetchData();
      clearInterval(this.renderPageTimer);
      clearInterval(this.idleTimer);
      const message = "Stopped auto page re-rendering due to max idle timeout";
      console.warn(message);
      alert(message);
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
          .then((contents) => this.contents = contents)
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
      preferences.hiddenElements = {...preferences.hiddenElements, [this.nameOfItems]: this.hiddenElements}
    },
    isVisible(content) {
      return !this.isHidden(content)
    },
    isHidden(content) {
      return this.hideByKey && this.hiddenElements.indexOf(content[this.hideByKey]) !== -1
    },
    toggleHiddenElements() {
      this.showHiddenElements = !this.showHiddenElements
    }
  }
}
</script>

<style scoped>
.content-container {
  font-family: "OpenSans", sans-serif;
  display: grid;
  align-items: center;
  grid-template-rows: repeat(auto-fit, minmax(90px, 115px));
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  grid-gap: 10px;
  width: 100%;
}
.hidden-elements {
  margin-top: 3em;
  margin-bottom: 1em;
  font-weight: bold;
  color: #333;
}
</style>
