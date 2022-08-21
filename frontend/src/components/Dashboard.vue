<template>
  <template
    v-if="loading"
  >
    <Spinner />
  </template>

  <div
    v-if="!loading"
    id="content-container"
  >
    <template
      v-for="content in contents"
      :key="content"
    >
      <component
        :is="contentDisplayer"
        :content="content"
      />
    </template>
  </div>
  <NoFailures v-if="!loading && contents.length === 0" />
</template>

<script>
import NoFailures from "@/components/Happy";
import Spinner from "@/components/Spinner";
import Job from "@/components/Job";
import Secret from "@/components/Secret";

const ONE_MINUTE = 60000;

export default {
  name: "Dashboard",
  components: {NoFailures, Spinner, Job, Secret },
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
    }
  },
  data() {
    return {
      contents: [],
      loading: true,
      idleTime: 0,
      renderPageTimer: null,
      idleTimer: null,
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
    }
  }
}
</script>

<style scoped>
#content-container {
  font-family: "OpenSans", sans-serif;
  display: grid;
  align-items: center;
  grid-template-rows: repeat(auto-fit, minmax(90px, 115px));
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  grid-gap: 10px;
  width: 100%;
}
</style>
