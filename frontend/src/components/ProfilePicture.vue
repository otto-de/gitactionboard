<template>
  <img
    :src="avatarUrl ? avatarUrl : avatar"
    :class="cssClass"
    alt="avatar"
  >
</template>

<script>
import {getAvatarUrl} from "@/services/authenticationService";
import avatar from '../images/avatar.jpg';

export default {
  name: "ProfilePicture",
  props: {
    small: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      avatar: avatar
    }
  },
  computed:{
    avatarUrl(){
      return getAvatarUrl();
    },
    cssClass(){
      return this.small ? "avatar" : "profile-avatar";
    }
  }
}
</script>

<style scoped>
.avatar {
  height: 30px;
  width: 30px;
  border-radius: 50%;
}

.profile-avatar {
  height: 300px;
  width: 300px;
  border-radius: 50%;
  margin-top: 8%;
}
</style>
