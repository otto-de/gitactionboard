<template>
<div id="jobs">
<template v-for="job in jobs" :key="job">
  <Job :job="job" />
</template>
</div>
<div id="all-passed-container" v-if="jobs.length === 0"></div>
</template>

<script>
import Job from "@/components/Job";

export default {
  name: "Jobs",
  components: { Job },
  mounted: function(){
    return this.fetchJobs()
  },
  props: {
    showHealthyBuilds: {
      type: Boolean,
      // required: true
    }
  },
  data() {
    return {
      jobs: []
    }
  },
  methods: {
    fetchData: function () {
          return fetch("/v1/cctray")
              .then((res) => res.json())
              .then(this.marshalData)
              .catch((reason) => {
                console.error(reason);
                return Promise.reject(reason);
              });
    },
    isIdleHealthyBuild: function (lastBuildStatus, activity) {
      return lastBuildStatus === "Success" && activity === "Sleeping"
    },
    marshalData: function (data) {
      return data.filter(({ lastBuildStatus, activity }) => {
            return this.showHealthyBuilds ? true : !this.isIdleHealthyBuild(lastBuildStatus, activity);
          }
      );
    },
    fetchJobs: function () {
      return this.fetchData()
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
