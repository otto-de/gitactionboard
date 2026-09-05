<template>
  <v-progress-circular
    indeterminate
    size="128"
    width="12"
    color="#5f8d77"
  />
</template>

<script>
export default {
  name: 'Spinner'
};
</script>

<style scoped>
.v-progress-circular {
  animation: spinnerEntrance 180ms ease-out;
}

@keyframes spinnerEntrance {
  from {
    opacity: 0;
    transform: scale(0.97);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
