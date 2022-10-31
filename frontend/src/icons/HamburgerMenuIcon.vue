<template>
  <div
    class="icon-container"
    :class="{'opened':isClicked}"
    @click="openMenu"
  >
    <span />
    <span />
    <span />
  </div>
</template>

<script>
export default {
  name: 'HamburgerMenuIcon',
  props: {
    isClicked: Boolean
  },
  emits: ['clicked'],
  methods: {
    openMenu() {
      this.$emit('clicked');
    }
  }
};
</script>

<style scoped>
.icon-container {
  height: 24px;
  width: 24px;
  margin-bottom: 40px;
  margin-top: 10px;
  margin-left: 7px;
}

.icon-container:hover {
  cursor: pointer;
}

.icon-container span {
  height: 3px;
  width: 24px;
  display: inline-block;
  background-color: #fff;
  position: relative;
  border-radius: 5px;
  transition: 0.3s cubic-bezier(0.8, 0.5, 0.2, 1.4);
  transition-duration: 500ms;
}

.icon-container span:first-child {
  top: 10px;
}

.icon-container span:last-child {
  bottom: 10px;
}

.icon-container:not(.opened) {
  margin: 10px auto 40px;
}

.icon-container.opened span:first-child {
  transform: rotate(-45deg);
  top: 18.5px;
}

.icon-container.opened span:nth-child(2) {
  opacity: 0;
}

.icon-container.opened span:last-child {
  transform: rotate(45deg);
  bottom: 17px;
}

</style>
