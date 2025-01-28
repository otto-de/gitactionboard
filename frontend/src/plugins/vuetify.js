import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { VDateInput } from 'vuetify/labs/VDateInput';
import { aliases, mdi } from 'vuetify/iconsets/mdi-svg';
import {
  mdiWeatherNight,
  mdiWeatherSunny,
  mdiRefresh,
  mdiMenu,
  mdiMenuOpen,
  mdiSitemap,
  mdiShieldLockOutline,
  mdiCodeBracesBox,
  mdiChartLine,
  mdiCogOutline,
  mdiAccountCircle,
  mdiLogout,
  mdiLogin,
  mdiClockTimeFourOutline,
  mdiOpenInNew,
  mdiEye,
  mdiEyeOff,
  mdiClose,
  mdiArrowExpand,
  mdiGithub
} from '@mdi/js';

export default () => createVuetify({
  components: {
    ...components,
    VDateInput
  },
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases: {
      ...aliases,
      light: mdiWeatherSunny,
      dark: mdiWeatherNight,
      refresh: mdiRefresh,
      menu: mdiMenu,
      menuOpen: mdiMenuOpen,
      workflowJobs: mdiSitemap,
      secrets: mdiShieldLockOutline,
      codeStandards: mdiCodeBracesBox,
      metrics: mdiChartLine,
      preferences: mdiCogOutline,
      avatar: mdiAccountCircle,
      logout: mdiLogout,
      clock: mdiClockTimeFourOutline,
      openInNewWindow: mdiOpenInNew,
      view: mdiEye,
      hide: mdiEyeOff,
      close: mdiClose,
      expand: mdiArrowExpand,
      login: mdiLogin,
      github: mdiGithub
    },
    sets: {
      mdi
    }
  }
});
