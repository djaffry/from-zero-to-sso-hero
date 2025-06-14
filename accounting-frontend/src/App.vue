<script setup lang="ts">
import {RouterView} from 'vue-router'
import {computed} from 'vue'
import HelloWorld from './components/HelloWorld.vue'
import UserInfo from './components/UserInfo.vue'
import {oidcService} from './auth/oidc.service'

const isLoggedIn = computed(() => oidcService.userIsLoggedIn())

const login = () => {
  oidcService.login()
}

const logout = () => {
  oidcService.logout()
}
</script>

<template>
  <div class="container">
    <img alt="C logo" class="logo" src="https://avatars.githubusercontent.com/u/56123079?s=200&v=4"
         width="125" height="125"/>
    <HelloWorld msg="C-Corp"/>
    <nav>
      <div class="auth-buttons">
        <button v-if="!isLoggedIn" @click="login" class="login-button">Login</button>
        <button v-if="isLoggedIn" @click="logout" class="logout-button">Logout</button>
      </div>
    </nav>

    <UserInfo v-if="isLoggedIn"/>
    <main>
      <RouterView/>
    </main>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.logo {
  display: block;
  margin: 1rem auto;
  transform: rotate(-45deg);
}

nav {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin: 1.5rem 0;
  padding: 1rem 0;
  border-top: 1px solid var(--color-border);
  border-bottom: 1px solid var(--color-border);
  font-size: 1rem;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
  text-decoration: none;
  color: var(--color-text);
}

nav a:first-of-type {
  border: 0;
}

.auth-buttons {
  margin-left: auto;
  padding: 0 1rem;
}

.login-button, .logout-button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.login-button {
  background-color: #004bbf;
  color: white;
}

.logout-button {
  background-color: #f44336;
  color: white;
}

main {
  width: 100%;
  padding: 1rem 0;
}
</style>
