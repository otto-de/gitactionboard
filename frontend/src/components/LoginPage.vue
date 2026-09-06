<template>
  <v-container
    fluid
    class="h-screen fill-height d-flex justify-center align-center flex-wrap main-container bg-background"
  >
    <template v-if="loading">
      <Spinner />
    </template>
    <v-card
      v-if="!loading"
      class="px-5 py-8 rounded-xl login-card"
      rounded
      elevation="0"
    >
      <v-card-item class="brand-row px-0 pt-0 pb-0 mb-1">
        <template #prepend>
          <v-img
            src="/favicon.ico"
            alt="GitactionBoard"
            class="brand-mark"
            width="38"
            height="28"
          />
        </template>
        <v-card-title class="header pa-0">
          GitactionBoard
        </v-card-title>
      </v-card-item>
      <v-card-subtitle class="welcome-message mb-8 mt-1 pl-0">
        Welcome to Gitaction Board
      </v-card-subtitle>
      <v-card-item v-if="isBasicAuthEnabled">
        <v-form
          v-model="validForm"
          test-id="basic-auth-login-form"
          @submit.prevent="login"
        >
          <v-text-field
            v-model="username"
            :readonly="loggingIn"
            :rules="[required]"
            class="mb-2"
            clearable
            rounded="lg"
            label="Username"
            hide-details
            placeholder="Enter your username"
            test-id="username"
          />

          <v-text-field
            v-model="password"
            :readonly="loggingIn"
            :rules="[required]"
            :type="showPassword ? 'text' : 'password'"
            :append-inner-icon="showPassword ? '$hide' : '$view'"
            clearable
            rounded="lg"
            label="Password"
            hide-details
            placeholder="Enter your password"
            test-id="password"
            @click:append-inner="showPassword = !showPassword"
          />
          <v-alert
            v-model="error"
            class="mt-4"
            closable
            type="error"
            text="Invalid username or password"
            density="compact"
            test-id="basic-auth-alert"
          />

          <v-btn
            :disabled="!validForm"
            :loading="loggingIn"
            block
            class="mt-4"
            color="primary"
            size="large"
            type="submit"
            variant="elevated"
            append-icon="$login"
            test-id="basic-auth-login-button"
          >
            Login
          </v-btn>
        </v-form>
      </v-card-item>
      <v-card-item v-if="isBasicAuthEnabled && isOauth2Enabled">
        <DividerWithText />
      </v-card-item>
      <v-card-item v-if="isOauth2Enabled">
        <v-btn
          :loading="loading"
          block
          color="primary"
          variant="outlined"
          size="large"
          type="submit"
          :href="getOauth2LoginUrl()"
          append-icon="$github"
        >
          Login with Github
        </v-btn>
      </v-card-item>
    </v-card>
  </v-container>
</template>

<script>
import { authenticate, fetchConfig, preparePath } from '@/services/apiService';
import { isAuthenticate } from '@/services/authenticationService';
import Spinner from '@/components/Spinner';
import { watch } from 'vue';
import {
  setAvailableAuths,
  setGithubCodeScanMonitoringEnabled,
  setGithubSecretsScanMonitoringEnabled,
  setVersion
} from '@/services/utils';
import DividerWithText from '@/components/DividerWithText.vue';

export default {
  name: 'LoginPage',
  components: { DividerWithText, Spinner },
  data() {
    return {
      availableAuths: [],
      loading: true,
      error: false,
      validForm: false,
      username: '',
      password: '',
      showPassword: false,
      loggingIn: false
    };
  },
  computed: {
    isBasicAuthEnabled() {
      return this.availableAuths.includes('BASIC_AUTH');
    },
    isOauth2Enabled() {
      return this.availableAuths.includes('OAUTH2');
    }
  },
  mounted() {
    watch(() => [this.username, this.password], ([newUsername, newPassword], [oldUsername, oldPassword]) => {
      if (this.error) {
        this.error = newUsername === oldUsername && newPassword === oldPassword;
      }
    });
    fetchConfig()
      .then(({ availableAuths, githubSecretsScanMonitoringEnabled, githubCodeScanMonitoringEnabled, version }) => {
        this.availableAuths = availableAuths;
        this.loading = false;
        setVersion(version);
        setAvailableAuths(availableAuths);
        setGithubSecretsScanMonitoringEnabled(githubSecretsScanMonitoringEnabled);
        setGithubCodeScanMonitoringEnabled(githubCodeScanMonitoringEnabled);
      })
      .then(() => {
        if (isAuthenticate() || (!this.isBasicAuthEnabled && !this.isOauth2Enabled)) {
          this.redirectToDashboard();
        }
      })
      .catch((reason) => {
        console.error(reason);
        return Promise.reject(reason);
      });
  },
  methods: {
    required(v) {
      return !!v || '';
    },
    redirectToDashboard() {
      this.$router.push('workflow-jobs');
    },
    login() {
      this.loggingIn = true;
      authenticate(this.username, this.password)
        .then(this.redirectToDashboard)
        .catch(reason => {
          this.error = true;
          this.loggingIn = false;
          console.error(reason);
        });
    },
    getOauth2LoginUrl() {
      return preparePath('/oauth2/authorization/github');
    }
  }
};
</script>

<style scoped>
.login-card {
  width: 400px;
  max-width: 92vw;
  box-shadow: 0 1px 2px rgb(20 22 26 / 4%), 0 8px 24px -12px rgb(20 22 26 / 12%);
}

/* stylelint-disable-next-line selector-class-pattern -- Vuetify's runtime theme class, not ours to rename */
.v-theme--dark .login-card {
  box-shadow: 0 1px 2px rgb(0 0 0 / 40%), 0 8px 24px -8px rgb(0 0 0 / 50%);
}

.brand-mark {
  border-radius: 6px;
  flex: 0 0 auto;
}

.header {
  font-weight: 700;
  font-size: 15px;
  letter-spacing: -0.01em;
}

.welcome-message {
  font-size: 13.5px;
  font-weight: 600;
  white-space: normal;
}
</style>
