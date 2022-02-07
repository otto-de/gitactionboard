<template>
  <div
    :class="{'menu-icon-container': true, 'toggle':isClicked}"
    @click="openMenu"
  >
    <span />
    <span />
    <span />
  </div>
</template>

<script>
export default {
  name: "MenuIcon",
  props: {
    isClicked: Boolean
  },
  emits: ['clicked'],
  methods: {
    openMenu() {
      this.$emit('clicked')
    }
  }
}
</script>

<style scoped>

.menu-icon-container {
  height: 24px;
  width: 24px;
  margin-bottom: 40px;
  margin-top: 10px;
  margin-left: 7px;
}

.menu-icon-container:hover {
  cursor: pointer;
}

.menu-icon-container span {
  height: 3px;
  width: 24px;
  display: inline-block;
  background-color: #fff;
  position: relative;
  border-radius: 5px;
  transition: .3s cubic-bezier(.8, .5, .2, 1.4);
  transition-duration: 500ms;
}

.menu-icon-container span:first-child {
  top: 10px;
}

.menu-icon-container span:last-child {
  bottom: 10px;
}

.menu-icon-container:not(.toggle) {
  margin: 10px auto 40px;
}

.menu-icon-container.toggle span:first-child {
  transform: rotate(-45deg);
  top: 18.5px;
}

.menu-icon-container.toggle span:nth-child(2) {
  opacity:0;
}

.menu-icon-container.toggle span:last-child {
  transform: rotate(45deg);
  bottom: 17px;
}

</style>
