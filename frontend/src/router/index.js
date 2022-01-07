import { createRouter, createWebHashHistory } from "vue-router";
import Dashboard from "@/components/Dashboard";
import LoginPage from "@/components/LoginPage";

const routes = [
  {
    path: "/login",
    name: "Login",
    alias: "/",
    component: LoginPage,
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
