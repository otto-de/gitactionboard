const {
  fetchAccessToken,
  clearCookies,
} = require("@/services/authenticationService");

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

const fetchConfig = () =>
  fetch("./config", {
    headers: new Headers({
      Accept: "application/json",
    }),
  })
    .then(validate)
    .then((response) => response.json());

const fetchCctrayJson = () =>
  fetch("./v1/cctray", {
    headers: new Headers(
      marshalHeaders({
        Authorization: fetchAccessToken(),
        Accept: "application/json",
      })
    ),
  })
    .then(validate)
    .then((res) => res.json());

const authenticate = (username, password) => {
  return fetch("./login/basic", {
    method: "POST",
    headers: new Headers({ "Content-Type": "application/json" }),
    body: JSON.stringify({ username, password }),
  }).then(validate);
};

module.exports = { fetchConfig, fetchCctrayJson, authenticate };
