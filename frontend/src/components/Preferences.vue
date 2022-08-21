<template>
  <div id="container">
    <div class="preferences-container">
      <div class="preference-header">
        <ProfilePicture :small="false" />
        <div class="text">
          {{ name }}
        </div>
      </div>
      <div class="preferences-data">
        <div class="header">
          Your current preferences:
        </div>
        <div class="preferences-body">
          <div class="preferences-body-content">
            <div>
              Show healthy builds:
              <input
                v-model="showHealthyBuilds"
                type="checkbox"
                class="preferences-input-checkbox"
              >
            </div>
          </div>

          <div class="preferences-body-content">
            <div>
              Disable Max Idle Time:
              <input
                v-model="disableIdleOptimization"
                type="checkbox"
                class="preferences-input-checkbox"
              >
            </div>
          </div>
          <div
            v-if="!disableIdleOptimization"
            class="preferences-body-content"
          >
            <div>
              Maximum Idle Time (minutes):
              <input
                v-model="maxIdleTime"
                type="number"
                class="preferences-input"
                min="0"
              >
            </div>
          </div>
          <div class="save-container">
            <button
              class="save"
              :class="{disabled: isDisabled}"
              :disabled="isDisabled"
              @click="savePreferences"
            >
              Save
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import preferences from "@/services/preferences";
import { getAvatarUrl, getName } from "@/services/authenticationService";
import ProfilePicture from "@/components/ProfilePicture";

export default {
  name: "Preferences",
  components: {ProfilePicture},
  data() {
    return {
      showHealthyBuilds: preferences.showHealthyBuilds,
      maxIdleTime: preferences.maxIdleTime,
      disableIdleOptimization: preferences.disableIdleOptimization,
      saved: true
    }
  },
  computed: {
    isDirty() {
      return !(this.saved && this.showHealthyBuilds === preferences.showHealthyBuilds &&
          this.maxIdleTime === preferences.maxIdleTime &&
          this.disableIdleOptimization === preferences.disableIdleOptimization)
    },
    isValid() {
      return this.maxIdleTime >= 0 && !isNaN(this.maxIdleTime);
    },
    isDisabled() {
      return !(this.isDirty && this.isValid);
    },
    name() {
      return getName();
    }
  },
  watch: {
    isDirty() {
      this.saved = !this.isDirty
    }
  },
  methods: {
    savePreferences() {
      preferences.disableIdleOptimization = this.disableIdleOptimization;
      preferences.showHealthyBuilds = this.showHealthyBuilds;
      preferences.maxIdleTime = this.maxIdleTime;

      this.saved = true;
    },
    getAvatarData() {
      return getAvatarUrl()
    }
  }
}
</script>

<style scoped>

#container {
  display: flex;
  flex-direction: row;
  height: 100vh;
  width: 100vw;
}

.preferences-container {
  width: 97.1vw;
  margin-left: auto;
}

.preference-header {
  background-color: #5F8D77;
  width: 100%;
  height: 30%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.text {
  margin-top: 5px;
  margin-bottom: 10px;
  font-size: 25px;
  font-family: "Apple Chancery";
}

.preferences-data {
  width: fit-content;
  margin-top: 15%;
  margin-left: auto;
  margin-right: auto;
}

.header {
  font-size: 35px;
  font-family: "Apple Chancery";
  color: black;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}

.preferences-body {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.preferences-body-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 25px;
  font-family: "Al Bayan";
  color: #4f4d4d;
}

.preferences-input-checkbox {
  margin-left: 10px;
  margin-top: 11px;
  margin-bottom: 11px;
}

.preferences-input {
  /*margin-left: 8px;*/
  font-size: 20px;
  height: 23px;
  width: 45px;
  border: 1px transparent;
  border-bottom: 1px solid black;
}

.save-container {
  width: 40%;
  margin-left: auto;
  margin-right: auto;
}

.save {
  font-size: 25px;
  font-family: "Al Bayan";
  background-color: #3a964a;
  color: #FFFFFF;
  border-radius: 5px;
  margin-top: 25px;
  width: 100%;
}

.save:not(.disabled):hover {
  cursor: pointer;
}

.save.disabled {
  background-color: #4f4d4d;
}

</style>
