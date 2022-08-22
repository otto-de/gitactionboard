<template>
  <div
    :id="content.name"
    :key="content.name"
    :class="[getBuildAndActivityStatus(content)]"
  >
    <div class="job_name">
      <a
        :id="content.name + '_url'"
        :href="content.webUrl"
        target="_blank"
      >
        {{ content.name }}
      </a>
    </div>
    <button
      class="hide_button"
      @click="toggleVisibility(job.name)"
    >
      <HideIcon v-if="!hidden" />
      <ShowIcon v-if="hidden" />
    </button>
  </div>
</template>

<script>
import preferences from "@/services/preferences";
import HideIcon from "@/icons/HideIcon";
import ShowIcon from "@/icons/ShowIcon";

export default {
  name: "Job",
  components: {HideIcon, ShowIcon},
  props: {
    content: {
      type: Object,
      required: true
    },
    hidden: {
      type: Boolean,
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
    },
    toggleVisibility(jobName) {
      preferences.toggleVisibility(jobName);
    }
  }
}
</script>

<style scoped>

.job_name {
  color: white;
  font-weight: bold;
  font-size: 14px;
  overflow: auto;
  display: inline-grid;
  align-items: center;
  flex-grow: 1;
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
  display: flex;
  flex-direction: column;
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

.hide_button {
  background: none;
  border: none;
  cursor: pointer;
  text-align: right;
}

.hide_button svg {
  width: 20px;
  height: 20px;
  fill: white;
}

</style>
