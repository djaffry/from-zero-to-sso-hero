<script setup lang="ts">
defineProps<{
  msg: string
}>()
</script>

<template>
  <div class="greetings">
    <h1 class="green">{{ msg }}</h1>
    <h3>
      Secret Messaging Service
    </h3>
  </div>
</template>

<style scoped>
h1 {
  font-weight: 500;
  font-size: 2.6rem;
  margin-bottom: 0.5rem;
}

h3 {
  font-size: 1.2rem;
  margin-top: 0;
}

.greetings {
  text-align: center;
  margin-bottom: 1rem;
}

.greetings h1,
.greetings h3 {
  text-align: center;
}
</style>
