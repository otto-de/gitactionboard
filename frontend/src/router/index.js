import { createRouter, createWebHashHistory } from "vue-router";
import Dashboard from "@/components/Dashboard";
import LoginPage from "@/components/LoginPage";
import Preferences from "@/components/Preferences";
import storageService from "@/services/storageService";
import { isAuthenticate } from "@/services/authenticationService";

const isAuthenticationRequired = () => {
  const availableAuths = JSON.parse(storageService.getItem("availableAuths"));

  return (
    availableAuths === null || (availableAuths.length > 0 && !isAuthenticate())
  );
};

const validateAuthentication = (next) => {
  const authenticationRequired = isAuthenticationRequired();
  if (authenticationRequired) {
    next({
      name: "Login",
    });
  } else {
    next();
  }
};

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
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    },
  },
  {
    path: "/preferences",
    name: "Preferences",
    component: Preferences,
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    },
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
