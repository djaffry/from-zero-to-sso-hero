import {createRouter, createWebHistory} from 'vue-router'
import CallbackView from '../views/CallbackView.vue'
import AccountInfoView from "@/views/AccountInfoView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'account-info',
      component: AccountInfoView,
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
