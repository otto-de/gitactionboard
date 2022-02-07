<template>
  <div
    :class="{'profile': true, 'actionable': iconName!=='profile', 'open': isClicked, 'active':isActive}"
    @click="() => emitEventForProfile(iconName)"
  >
    <img
      v-if="iconName==='profile'"
      :src="getAvatarData()"
      class="profile-avatar"
      alt="avatar"
    >
    <Settings v-if="iconName==='settings'" />
    <Logout v-if="iconName==='logout'" />
    <DashboardIcon v-if="iconName==='dashboard'" />
    <p
      v-if="isClicked"
      class="menu-item-name"
    >
      {{ calcFirstName(menuItemName) }}
    </p>
  </div>
</template>

<script>
import Settings from "@/icons/Settings";
import Logout from "@/icons/Logout";
import DashboardIcon from "@/icons/DashboardIcon";
import { getAvatarUrl } from "@/services/authenticationService";
export default {
  name: "MenuItems",
  components: {DashboardIcon, Logout, Settings},
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
    isActive: Boolean
  },
  methods: {
    calcFirstName(name) {
      const nameData = name.split(" ");
      return nameData[0];
    },
    emitEventForProfile(iconName) {
      this.$emit(iconName);
    },
    getAvatarData() {
      return getAvatarUrl()
    }
  }
}
</script>

<style scoped>

.profile {
  display: flex;
  padding-bottom: 7px;
  padding-top: 7px;
  height: 30px;
  cursor: pointer;
}

.profile:not(.open) > svg {
  margin-left: auto;
  margin-right: auto;
}

.profile.open > svg {
  margin-left: 7px;
}

.profile.open {
  width: 245px;
}

.profile.actionable.open:hover {
  cursor: pointer;
  width: 245px;
  height: 30px;
  background-color: #4f4d4d;
}

.profile.actionable.open.active {
  background-color: #5F8D77;
  height: 30px;
  width: 245.2px;
}

.profile.actionable.active {
  background-color: #5F8D77;
  height: 30px;
  width: 52px;
}

.profile p {
  color: #FFFFFF;
  font-size: 17px;
  font-family: "OpenSans", sans-serif;
}

.profile-avatar {
  height: 30px;
  width: 30px;
  border-radius: 50%;
}

.menu-item-name {
  padding-left: 25px;
  margin: auto 0;
}

</style>
