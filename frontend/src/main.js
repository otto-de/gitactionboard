import { createApp } from 'vue';
import App from './App.vue';

import router from './router';
import vuetify from '@/plugins/vuetify';
import charts from '@/plugins/charts';
import '@fontsource-variable/inter';
import '@fontsource-variable/jetbrains-mono';
import '@/assets/styles/base.css';

createApp(App)
  .use(vuetify())
  .use(charts)
  .use(router)
  .mount('#app');
