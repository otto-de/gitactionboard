<template>
  <img
    v-if="!!avatarUrl"
    :src="avatarUrl"
    :class="cssClass"
    alt="avatar"
  >
  <AnonymousUser
    v-if="!avatarUrl"
    :class="cssClass"
  />
</template>

<script>
import AnonymousUser from "@/icons/AnonymousUser";
import {getAvatarUrl} from "@/services/authenticationService";

export default {
  name: "ProfilePicture",
  components: {AnonymousUser},
  props: {
    small: {
      type: Boolean,
      default: true
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
