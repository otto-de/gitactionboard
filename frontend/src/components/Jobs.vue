<template>
  <template
    v-if="loading"
  >
    <Spinner />
  </template>
  <div
    v-if="!loading"
    id="jobs"
  >
    <template
      v-for="job in jobs"
      :key="job"
    >
      <Job :job="job" />
    </template>
  </div>
  <NoFailedBuild v-if="!loading && jobs.length === 0" />
</template>

<script>

import {fetchCctrayJson} from "@/services/apiService";
import Job from "@/components/Job";
import Spinner from "@/components/Spinner";
import NoFailedBuild from "@/components/NoFailedBuild";

const ONE_MINUTE = 60000;

export default {
  name: "Jobs",
  components: {NoFailedBuild, Spinner, Job },
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
  grid-template-rows: repeat(auto-fit, minmax(90px, 115px));
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  grid-gap: 10px;
  width: 100%;
}

</style>
