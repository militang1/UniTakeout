<template>
  <div id="app">
    <router-view />
    <TabBar />
  </div>
</template>

<script setup>
import TabBar from './components/TabBar.vue'
</script>

<style>
#app {
  padding-bottom: 60px;
}
</style>
