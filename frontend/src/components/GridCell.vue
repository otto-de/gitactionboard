<template>
  <div
    :id="content.id"
    :key="content.id"
    class="grid-cell failure"
  >
    <div class="grid-cell-name">
      <a
        :id="content.id + '_url'"
        :href="content.url"
        target="_blank"
      >
        {{ content.name }}
      </a>
    </div>
  </div>
</template>

<script>
export default {
  name: "GridCell",
  props: {
    content: {
      type: Object,
      required: true
    }
  },
}
</script>

<style scoped>
.grid-cell-name {
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

.grid-cell {
  box-shadow: 5px 5px 10px #777;
  border-radius: 6px;
  height: 90px;
  border: 2px solid #000;
  padding: 5px;
}

.failure {
  background-color: #e23d2c;
}

</style>
