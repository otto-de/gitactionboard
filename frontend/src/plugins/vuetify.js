import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
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
  mdiGithub,
  mdiInformationOutline,
  mdiChevronDown
} from '@mdi/js';

export default () => createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          background: '#f6f7f9',
          surface: '#ffffff',
          'surface-raised': '#f0f1f4',
          primary: '#4a63d6',
          success: '#1f9d55',
          error: '#d1483e',
          warning: '#b8790f'
        }
      },
      dark: {
        dark: true,
        colors: {
          background: '#14161a',
          surface: '#1c1f26',
          'surface-raised': '#23262e',
          primary: '#7c9eff',
          success: '#4fb87a',
          error: '#e2685f',
          warning: '#d9a441'
        }
      }
    }
  },
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
      chevronDown: mdiChevronDown,
      login: mdiLogin,
      github: mdiGithub,
      information: mdiInformationOutline,
    },
    sets: {
      mdi
    }
  }
});
