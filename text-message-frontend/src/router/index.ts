import {createRouter, createWebHistory} from 'vue-router'
import CallbackView from '../views/CallbackView.vue'
import TextMessageView from '../views/TextMessageView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'text-message',
      component: TextMessageView,
    },
    {
      path: '/callback',
      name: 'callback',
      component: CallbackView,
    },
    {
      path: '/logout-callback',
      name: 'logout-callback',
      component: CallbackView,
    },
  ],
})

export default router
