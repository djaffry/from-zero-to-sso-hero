import './assets/main.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'
import {oidcService} from './auth/oidc.service'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(router)

oidcService.loadSession().then(() => {
  app.mount('#app')
}).catch(error => {
  console.error('Failed to load session:', error)
  app.mount('#app')
})
