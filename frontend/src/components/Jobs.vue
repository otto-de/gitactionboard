<template>
  <div id="jobs">
    <template
      v-for="job in jobs"
      :key="job"
    >
      <Job :job="job" />
    </template>
  </div>
  <div
    v-if="jobs.length === 0"
    id="all-passed-container"
  />
</template>

<script>

import {fetchCctrayJson} from "@/services/apiService";
import Job from "@/components/Job";

const ONE_MINUTE = 60000;
let idleTimer;
let renderPageTimer;

let idleTime;

export default {
  name: "Jobs",
  components: { Job },
  props: {
    showHealthyBuilds: {
      type: Boolean,
      required: true
    },
    disableMaxIdleTime: {
      type: Boolean,
      required: true
    },
    maxIdleTime: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      jobs: [],
      idleTime: 0,
      renderPageTimer,
      idleTimer,
      ONE_MINUTE
    }
  },
  mounted() {
    if (!this.disableMaxIdleTime) {
      this.initiateIdleTimer();
    }
    this.renderPage().then(() => {
      renderPageTimer = setInterval(this.renderPage, 5000);
    });
  },
  methods: {
    isIdleHealthyBuild(lastBuildStatus, activity) {
      return lastBuildStatus === "Success" && activity === "Sleeping"
    },
    marshalData(data) {
      return data.filter(({lastBuildStatus, activity}) => {
            return this.showHealthyBuilds ? true : !this.isIdleHealthyBuild(lastBuildStatus, activity);
          }
      );
    },
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
      if (this.maxIdleTime >= idleTime) return this.fetchData();
      clearInterval(renderPageTimer);
      const message = "Stopped auto page re-rendering due to max idle timeout";
      console.warn(message);
      alert(message);
    },
    incrementIdleTime() {
      idleTime++;
    },
    resetTimer() {
      clearInterval(idleTimer);
      idleTime = 0;
      idleTimer = setInterval(this.incrementIdleTime, ONE_MINUTE);
    },
    fetchData() {
      return fetchCctrayJson()
          .then(this.marshalData)
          .then((data) => this.jobs = data)
          .catch((reason) => {
            console.error(reason);
            return Promise.reject(reason);
          });
    }
  }
}
</script>

<style scoped>

#jobs {
  font-family: "OpenSans", sans-serif;
  display: grid;
  align-items: center;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  grid-gap: 10px;
  padding-left: 30px;
  padding-right: 30px;
}

</style>
