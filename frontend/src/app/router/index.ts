import { createRouter, createWebHistory } from "vue-router";
import YearCalendarView from "../../modules/calendar/views/YearCalendarView.vue";

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "year-calendar",
      component: YearCalendarView
    }
  ]
});
