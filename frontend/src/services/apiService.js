import {clearCookies, fetchAccessToken} from "@/services/authenticationService";

const validate = (res) => {
  if (!res.ok && res.redirected) {
    clearCookies();
    window.location.assign("#/login");
  }

  return res.ok ? Promise.resolve(res) : Promise.reject(res);
};

const isNullOrUndefined = (value) => value === null || value === undefined;

const marshalHeaders = (headers = {}) =>
  Object.keys(headers).reduce(
    (accumulator, key) =>
      isNullOrUndefined(headers[key])
        ? accumulator
        : { ...accumulator, [key]: headers[key] },
    {}
  );

const fetchJsonContent = (url, authToken) => {
  return fetch(url, {
    headers: new Headers(
      marshalHeaders({
        Authorization: authToken,
        Accept: "application/json",
      })
    ),
  })
    .then(validate)
    .then((res) => res.json());
};

export const fetchConfig = () => fetchJsonContent("./config");

export const fetchCctrayJson = () =>
  fetchJsonContent("./v1/cctray", fetchAccessToken());

export const fetchSecretAlerts = () =>
  fetchJsonContent("./v1/alerts/secrets", fetchAccessToken());

export const fetchCodeStandardViolations = () =>
  fetchJsonContent("./v1/alerts/code-standard-violations", fetchAccessToken());

export const authenticate = (username, password) => {
  return fetch("./login/basic", {
    method: "POST",
    headers: new Headers({ "Content-Type": "application/json" }),
    body: JSON.stringify({ username, password }),
  }).then(validate);
};
