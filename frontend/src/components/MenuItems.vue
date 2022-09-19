<template>
  <div
    class="menu-item"
    :class="{'clickable': clickable, 'open': isClicked, 'active':isActive}"
    @click="emitEvent"
  >
    <ProfilePicture
      v-if="iconName==='profile'"
      :small="true"
    />
    <SettingsIcon v-if="iconName==='settings'" />
    <SecretsIcon v-if="iconName==='secrets'" />
    <CodeStandardViolationsIcon v-if="iconName==='codeStandardViolations'" />
    <LogoutIcon v-if="iconName==='logout'" />
    <WorkflowJobsIcon v-if="iconName==='workflowJobs'" />
    <p
      v-if="isClicked"
      class="menu-item-name"
    >
      {{ menuItemName }}
    </p>
  </div>
</template>

<script>
import SettingsIcon from "@/icons/SettingsIcon";
import LogoutIcon from "@/icons/LogoutIcon";
import { getAvatarUrl } from "@/services/authenticationService";
import ProfilePicture from "@/components/ProfilePicture";
import SecretsIcon from "@/icons/SecretsIcon";
import CodeStandardViolationsIcon from "@/icons/CodeStandardViolationsIcon";
import WorkflowJobsIcon from "@/icons/WorkflowJobsIcon";
export default {
  name: "MenuItems",
  components: {
    WorkflowJobsIcon,
    CodeStandardViolationsIcon, SecretsIcon, ProfilePicture, LogoutIcon, SettingsIcon},
  props: {
    isClicked: Boolean,
    menuItemName: {
      type: String,
      default: ''
    },
    iconName: {
      type: String,
      default: ''
    },
    clickable: {
      type: Boolean,
      default: true
    },
    isActive: Boolean
  },
  methods: {
    emitEvent() {
      this.$emit(this.iconName);
    },
    getAvatarData() {
      return getAvatarUrl()
    }
  }
}
</script>

<style scoped>
.menu-item {
  display: flex;
  padding-bottom: 7px;
  padding-top: 7px;
  height: fit-content;
}

.menu-item:not(.open) > svg {
  margin-left: auto;
  margin-right: auto;
}

.menu-item.open > svg {
  margin-left: 7px;
}

.menu-item.open > img {
  margin-left: 7px;
}

.menu-item.open {
  width: 245px;
}

.menu-item.clickable:hover {
  cursor: pointer;
  background-color: #4f4d4d;
}

.menu-item.clickable.open:hover {
  width: 245px;
}

.menu-item.open.active {
  background-color: #5f8d77;
  width: 245.2px;
}

.menu-item.active {
  background-color: #5f8d77;
  width: 52px;
}

.menu-item p {
  color: #fff;
  font-size: 17px;
  font-family: OpenSans, sans-serif;
}

.menu-item-name {
  padding-left: 25px;
  margin: auto 0;
}

</style>
