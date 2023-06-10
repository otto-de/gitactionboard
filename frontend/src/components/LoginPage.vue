<template>
  <v-container
    fluid
    class="h-screen fill-height justify-center main-container"
  >
    <template v-if="loading">
      <Spinner />
    </template>
    <v-card
      v-if="!loading"
      class="px-5 py-10 rounded-xl"
      min-width="33%"
      rounded
      elevation="5"
    >
      <v-card-title class="header text-center">
        Gitaction Board
      </v-card-title>
      <v-card-subtitle class="welcome-message text-center mb-10 mt-1">
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
            label="Username"
            hide-details
            placeholder="Enter your username"
            test-id="username"
          />

          <v-text-field
            v-model="password"
            :readonly="loggingIn"
            :rules="[required]"
            clearable
            type="password"
            label="Password"
            hide-details
            placeholder="Enter your password"
            test-id="password"
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
            color="success"
            size="large"
            type="submit"
            variant="elevated"
            append-icon="mdi-login"
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
          color="success"
          size="large"
          type="submit"
          variant="elevated"
          :href="getOauth2LoginUrl()"
          append-icon="mdi-github"
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
.main-container {
  background-image: radial-gradient(#5f8d77, #282a27);
}

.header {
  font-family: "American Typewriter", serif;
  font-size: 40px;
}

.welcome-message {
  margin-bottom: 30px;
  font-size: 25px;
  font-weight: bold;
  font-family: "Snell Roundhand", cursive;
}

</style>
