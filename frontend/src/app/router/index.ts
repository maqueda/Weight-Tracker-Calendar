import { createRouter, createWebHistory } from "vue-router";
import AuthView from "../../modules/auth/views/AuthView.vue";
import ProfileView from "../../modules/auth/views/ProfileView.vue";
import YearCalendarView from "../../modules/calendar/views/YearCalendarView.vue";
import { getAuthToken } from "../../modules/auth/services/authStorage";

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/auth",
      name: "auth",
      component: AuthView
    },
    {
      path: "/",
      name: "year-calendar",
      component: YearCalendarView
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView
    }
  ]
});

router.beforeEach((to) => {
  const token = getAuthToken();

  if (!token && to.name !== "auth") {
    return { name: "auth" };
  }

  if (token && to.name === "auth") {
    return { name: "year-calendar" };
  }

  return true;
});
