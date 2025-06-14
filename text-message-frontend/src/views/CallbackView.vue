<template>
  <div class="callback">
    <p>Processing authentication...</p>
  </div>
</template>

<script setup lang="ts">
import {onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {oidcService} from '../auth/oidc.service';

const router = useRouter();

onMounted(async () => {
  try {
    await oidcService.handleOidcCallback(window.location.href);
    await router.push('/');
  } catch (error) {
    console.error('Error handling callback:', error);
    await router.push('/');
  }
});
</script>

<style scoped>
.callback {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.2rem;
}
</style>
