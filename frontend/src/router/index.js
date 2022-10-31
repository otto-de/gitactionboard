import { createRouter, createWebHashHistory } from 'vue-router';
import LoginPage from '@/components/LoginPage';
import Preferences from '@/components/Preferences';
import { isAuthenticate } from '@/services/authenticationService';
import SecretsDashboard from '@/components/SecretsDashboard';
import { getAvailableAuths } from '@/services/utils';
import WorkflowDashboard from '@/components/WorkflowDashboard';
import CodeStandardViolationsDashboard from '@/components/CodeStandardViolationsDashboard';

const isAuthenticationRequired = () => {
  const availableAuths = getAvailableAuths();

  return (
    availableAuths === null || (availableAuths.length > 0 && !isAuthenticate())
  );
};

const validateAuthentication = (next) => {
  const authenticationRequired = isAuthenticationRequired();
  if (authenticationRequired) {
    next({
      name: 'Login'
    });
  } else {
    next();
  }
};

const routes = [
  {
    path: '/login',
    name: 'Login',
    alias: '/',
    component: LoginPage
  },
  {
    path: '/workflow-jobs',
    name: 'Workflow Jobs',
    component: WorkflowDashboard,
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    }
  },
  {
    path: '/preferences',
    name: 'Preferences',
    component: Preferences,
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    }
  },
  {
    path: '/secrets',
    name: 'Secrets',
    component: SecretsDashboard,
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    }
  },
  {
    path: '/code-standard-violations',
    name: 'Code Standard Violations',
    component: CodeStandardViolationsDashboard,
    beforeEnter(to, from, next) {
      validateAuthentication(next);
    }
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

export default router;
