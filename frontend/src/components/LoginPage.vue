<template>
  <div class="main-container">
    <template v-if="loading">
      <Spinner />
    </template>
    <div
      v-if="!loading"
      class="login-form"
    >
      <div class="common-message-container">
        <h1 class="header">
          Gitaction Board
        </h1>
        <p class="welcome-message">
          Welcome to Gitaction Board
        </p>
      </div>
      <div
        v-if="isBasicAuthEnabled"
        class="basic-auth-container"
      >
        <input
          v-model="username"
          type="text"
          class="input-container"
          name="username"
          placeholder="Username"
        >
        <input
          v-model="password"
          type="password"
          class="input-container"
          name="password"
          placeholder="Password"
        >
        <div
          v-if="error"
          class="invalid-message-container"
        >
          <Invalid />
          <p class="error">
            Invalid username or password
          </p>
        </div>
        <button
          :disabled="isLoginButtonDisabled"
          :class="{'disabled': isLoginButtonDisabled}"
          type="button"
          class="login-button-container"
          @click="login"
        >
          Login
        </button>
      </div>
      <div
        v-if="isBasicAuthEnabled && isOauth2Enabled"
        class="button-separator"
      >
        <div class="button-separator-left" />
        <p class="button-separator-center">
          or
        </p>
        <div class="button-separator-right" />
      </div>
      <div
        v-if="isOauth2Enabled"
        class="oauth2-container"
      >
        <button
          type="button"
          class="github-button-container"
        >
          <a href="./oauth2/authorization/github">Login with Github</a>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import {authenticate, fetchConfig} from "@/services/apiService";
import {isAuthenticate} from "@/services/authenticationService";
import Spinner from "@/components/Spinner";
import {watch} from "vue";
import Invalid from "@/icons/InvalidIcon";
import {
  setAvailableAuths,
  setGithubCodeScanMonitoringEnabled,
  setGithubSecretsScanMonitoringEnabled
} from "@/services/utils";


export default {
  name: "LoginPage",
  components: {Invalid, Spinner},
  data(){
      return {
        availableAuths: [],
        loading: true,
        error: false,
        username: "",
        password: ""
      }
  },
  computed: {
    isBasicAuthEnabled() {
      return this.availableAuths.includes("BASIC_AUTH");
    },
    isOauth2Enabled() {
      return this.availableAuths.includes("OAUTH2");
    },
    isLoginButtonDisabled(){
      return this.username === "" || this.password === "";
    }
  },
  mounted() {
    watch(() => [this.username, this.password], ([newUsername, newPassword], [oldUsername, oldPassword])=> {
      if (this.error){
        this.error = newUsername === oldUsername && newPassword === oldPassword;
      }
    })
    fetchConfig()
        .then(({availableAuths, githubSecretsScanMonitoringEnabled, githubCodeScanMonitoringEnabled}) => {
          this.availableAuths = availableAuths;
          this.loading = false;
          setAvailableAuths(availableAuths)
          setGithubSecretsScanMonitoringEnabled(githubSecretsScanMonitoringEnabled);
          setGithubCodeScanMonitoringEnabled(githubCodeScanMonitoringEnabled);
        })
        .then(() => {
          if (isAuthenticate() || (!this.isBasicAuthEnabled && !this.isOauth2Enabled)){
            this.redirectToDashboard();
          }
        })
        .catch((reason) => {
          console.error(reason);
          return Promise.reject(reason);
        });
  },
  methods: {
    redirectToDashboard() {
      this.$router.push("dashboard")
    },
    login() {
      authenticate(this.username, this.password)
      .then(this.redirectToDashboard)
      .catch(reason => {
        this.error = true;
        console.error(reason);
      })

    }
  }
}
</script>

<style scoped>

.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-image: radial-gradient(#5F8D77, #282A27);
}

.login-form {
  box-shadow: 7px 5px 5px #2C5F2D;
  border-radius: 10px;
  height: fit-content;
  width: 30%;
  background-color: #FFFFFF;
}

.common-message-container, .basic-auth-container, .oauth2-container {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.header {
  font-family: "American Typewriter",serif;
  margin-top: 110px;
  color: #5b5454;
  font-size: 40px;
}

.welcome-message {
  margin-bottom: 30px;
  font-size: 25px;
  color: #5b5454;
  font-weight: bold;
  font-family: Snell Roundhand, cursive;
}

.input-container {
  padding: 10px;
  margin: 5px;
  border: none;
  border-bottom: 2px solid lightgray;
  width: 60%;
  font-family: monospace;
}

.input-container:focus {
  outline: none;
}

.login-button-container {
  width: 25%;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  margin: 35px 5px 100px;
  padding: 10px;
  color: #FFFFFF;
  background-color: #3a964a;
}

.login-button-container:not(.disabled):hover {
  cursor: pointer;
}

.login-button-container.disabled {
  /*background-color: #828683;*/
  background-color: #4f4d4d;
}

.button-separator {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  margin: -40px 15px 15px;
}

.button-separator-left {
  height: 0;
  width: 120px;
  border: 1px solid lightgray;
  margin-right: 8px;
}

.button-separator-center {
  margin: 0;
  color: #807878;
}

.button-separator-right {
  height: 0;
  width: 120px;
  margin-left: 8px;
  border: 1px solid lightgray;
}

.github-button-container {
  width: 56%;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  margin: 25px 5px 100px;
  padding: 10px;
  color: #FFFFFF;
  background-color: rgb(45, 164, 78);
}

.github-button-container a {
  text-decoration: none;
  color: white;
}

.invalid-message-container {
  position: relative;
  right: 69px;
  top: 5px;
}

.error {
  color: #ff6161;
  font-size: 16px;
  margin-top: 0;
  margin-left: 8px;
  /*margin: 0;*/
  position: relative;
  bottom: 23px;
  left: 22px;
}

</style>
