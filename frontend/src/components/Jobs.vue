<template>
  <div id="jobs" >
    <div v-for="job in jobsInfo" :key="job.name" class="job" :class="[getBuildAndActivityStatus(job)]">
      <div class="job_name"><a>{{ job.name }}</a></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Jobs",
  mounted: function(){
    this.fetchJobs()
  },
  data() {
    return {
      jobsInfo: []
    }
  },
  methods: {
    fetchData: function () {
          return fetch("/v1/cctray")
              .then((res) => res.json())
              .catch((reason) => {
                console.error(reason);
                return Promise.reject(reason);
              });
    },
    fetchJobs: function () {
      return this.fetchData()
          .then((data) => this.jobsInfo = data)
          .catch((reason) => {
            console.error(reason);
            return Promise.reject(reason);
          });
    },
    getBuildAndActivityStatus: function (job) {
      const lastBuildStatusIndicator = this.getBuildStatus(job.lastBuildStatus);
      return job.activity === "Building" ?
          `job ${lastBuildStatusIndicator} building` :
          `job ${lastBuildStatusIndicator}`;
    },
    getBuildStatus: function (status) {
      switch (status) {
        case 'Success':
          return 'success';
        case 'Unknown':
          return 'unknown';
        default:
          return 'failure';
      }
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

.job {
  box-shadow: 5px 5px 10px #777;
  border-radius: 6px;
  height: 90px;
  border: 2px solid #000000;
  padding: 5px;
}

.job_name {
  color: white;
  font-weight: bold;
  font-size: 14px;
  height: 100%;
  overflow: scroll;
  display: inline-grid;
  align-items: center;
}

.success {
  background-color: #3a964a;
}

.failure {
  background-color: #e23d2c;
}

.unknown {
  background-color: #6d6a6a;
}

.job.building.success {
  background-image: repeating-linear-gradient(
      135deg,
      #3a964a 30px,
      #000000 50px
  );
}

.job.building.failure {
  background-image: repeating-linear-gradient(
      135deg,
      #e23d2c 30px,
      #000000 50px
  );
}

.job.building.unknown {
  background-image: repeating-linear-gradient(
      135deg,
      #6d6a6a 30px,
      #000000 50px
  );
}

</style>
