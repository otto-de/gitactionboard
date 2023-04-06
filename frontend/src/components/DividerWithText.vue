<template>
  <v-row
    class="my-3"
    align="center"
  >
    <v-col cols="5">
      <v-divider :thickness="thickness" />
    </v-col>
    <v-col class="text-center font-weight-bold">
      OR
    </v-col>
    <v-col cols="5">
      <v-divider :thickness="thickness" />
    </v-col>
  </v-row>
</template>

<script>
export default {
  name: 'DividerWithText',
  props: {
    thickness: {
      type: Number,
      default: 3
    }
  }
};
</script>

<style scoped>

</style>
