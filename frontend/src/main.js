import { createApp } from 'vue';
import App from './App.vue';

import router from './router';
import vuetify from '@/plugins/vuetify';
import charts from '@/plugins/charts';

createApp(App)
  .use(vuetify())
  .use(charts)
  .use(router)
  .mount('#app');
