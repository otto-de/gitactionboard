const DELIMITER = " :: ";
const HIDE_HEALTHY_PARAM = "hide-healthy";
const MAX_IDLE_TIME_PARAM = "max-idle-time";
const DISABLE_MAX_IDLE_TIME_PARAM = "disable-max-idle-time";
const ONE_MINUTE = 60000;
const URL_SEARCH_PARAMS = new URLSearchParams(window.location.search);

let idleTimer;
let renderPageTimer;
let idleTime = 0;

const getMaxIdleTime = () => {
  const maxIdleTimeConfig = URL_SEARCH_PARAMS.get(MAX_IDLE_TIME_PARAM);
  return maxIdleTimeConfig ? parseInt(maxIdleTimeConfig) : 5;
};

const shouldShowHealthyBuilds = () => {
  const hideHealthBuildConfig = URL_SEARCH_PARAMS.get(HIDE_HEALTHY_PARAM);
  return !hideHealthBuildConfig || hideHealthBuildConfig === "false";
};

const shouldDisableIdleOptimization = () => {
  return URL_SEARCH_PARAMS.get(DISABLE_MAX_IDLE_TIME_PARAM) === "true";
};

const MAX_IDLE_MINUTE = getMaxIdleTime();
const SHOW_HEALTHY_BUILD = shouldShowHealthyBuilds();
const DISABLE_MAX_IDLE_TIME = shouldDisableIdleOptimization();

const isIdleHealthyBuild = (lastBuildStatus, activity) =>
  lastBuildStatus === "Success" && activity === "Sleeping";

const marshalData = (data) =>
  data.filter(({ lastBuildStatus, activity }) =>
    SHOW_HEALTHY_BUILD ? true : !isIdleHealthyBuild(lastBuildStatus, activity)
  );

const fetchData = () =>
  fetch("/v1/cctray")
    .then((res) => res.json())
    .then(marshalData)
    .catch((reason) => {
      console.error(reason);
      return Promise.reject(reason);
    });

const getLastBuildStatusIndicator = (lastBuildStatus) => {
  switch (lastBuildStatus) {
    case "Success":
      return "success";
    case "Unknown":
      return "unknown";
    default:
      return "failure";
  }
};

const getClassForJobElement = (lastBuildStatus, activity) => {
  const lastBuildStatusIndicator = getLastBuildStatusIndicator(lastBuildStatus);
  return activity === "Building"
    ? `job ${lastBuildStatusIndicator} building`
    : `job ${lastBuildStatusIndicator}`;
};

const createJobElement = ({ activity, lastBuildStatus, name, webUrl }) => {
  const element = document.createElement("div");
  element.innerHTML = `<div class='job_name'><a href='${webUrl}' id='${name}_url'>${name}</a></div>`;
  element.setAttribute("id", name);
  element.setAttribute(
    "class",
    getClassForJobElement(lastBuildStatus, activity)
  );
  return element;
};

const updateExistingJobElement = ({
  activity,
  lastBuildStatus,
  name,
  webUrl,
}) => {
  document.getElementById(`${name}_url`).setAttribute("href", webUrl);

  document
    .getElementById(`${name}`)
    .setAttribute("class", getClassForJobElement(lastBuildStatus, activity));
};

const insertOrReplaceJob = (job) => {
  const element = document.getElementById(job.name);
  if (element) {
    updateExistingJobElement(job);
  } else {
    document.getElementById("jobs").appendChild(createJobElement(job));
  }
};

const updateDashboard = () =>
  fetchData().then((jobs) => {
    if (jobs.length === 0) {
      document.getElementById(
        "all-passed-container"
      ).innerHTML = `<img id="all-passed" src="../images/happy.svg">`;
      document.getElementById("jobs").innerHTML = "";
      return Promise.resolve();
    }
    document.getElementById("all-passed-container").innerHTML = "";
    return jobs.forEach(insertOrReplaceJob);
  });

const renderPage = () => {
  if (DISABLE_MAX_IDLE_TIME) {
    return updateDashboard();
  }
  if (MAX_IDLE_MINUTE >= idleTime) return updateDashboard();
  clearInterval(renderPageTimer);
  const message = "Stopped auto page re-rendering due to max idle timeout";
  console.warn(message);
  alert(message);
};

const incrementIdleTime = () => {
  idleTime++;
};

const resetTimer = () => {
  clearInterval(idleTimer);
  idleTime = 0;
  idleTimer = setInterval(incrementIdleTime, ONE_MINUTE);
};

const initiateIdleTimer = () => {
  resetTimer();
  window.onmousemove = resetTimer;
  window.onmousedown = resetTimer;
  window.ontouchstart = resetTimer;
  window.onclick = resetTimer;
  window.onkeypress = resetTimer;
};

const initiatePageLoad = () => {
  if (!DISABLE_MAX_IDLE_TIME) {
    initiateIdleTimer();
  }
  renderPage().then(() => {
    renderPageTimer = setInterval(renderPage, 5000);
  });
};

window.onload = initiatePageLoad;
