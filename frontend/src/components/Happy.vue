<template>
  <div class="container">
    <img
      :src="happyImage"
      alt="no-failures"
    >
  </div>
</template>

<script>
import happy from '../images/happy.png';

export default {
  name: "Happy",
  data() {
    return {
      happyImage: happy
    }
  }

}
</script>

<style scoped>
.container {
  height: 70%;
  display: flex;
  justify-content: center;
  align-items: center;
}

img {
  height: 40%;
}
</style>
