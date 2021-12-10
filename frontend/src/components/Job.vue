<template>
  <div
    :id="job.name"
    :key="job.name"
    :class="[getBuildAndActivityStatus(job)]"
  >
    <div class="job_name">
      <a
        :id="job.name + '_url'"
        :href="job.webUrl"
        target="_blank"
      >
        {{ job.name }}
      </a>
    </div>
  </div>
</template>

<script>
export default {
  name: "Job",
  props: {
    job: {
      type: Object,
      required: true
    }
  },
  methods: {
    getBuildAndActivityStatus(job) {
      const lastBuildStatusIndicator = this.getBuildStatus(job.lastBuildStatus);
      return job.activity === "Building" ?
          `job ${lastBuildStatusIndicator} building` :
          `job ${lastBuildStatusIndicator}`;
    },
    getBuildStatus(status) {
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

.job_name {
  color: white;
  font-weight: bold;
  font-size: 14px;
  height: 100%;
  overflow: auto;
  display: inline-grid;
  align-items: center;
}
a {
  text-decoration: none;
  color: inherit;
}

.job {
  box-shadow: 5px 5px 10px #777;
  border-radius: 6px;
  height: 90px;
  border: 2px solid #000000;
  padding: 5px;
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
