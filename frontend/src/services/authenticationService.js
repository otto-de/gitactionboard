const getCookies = () =>
  document.cookie.split(";").map((cookie) => cookie.trim());

const getCookie = (name) => {
  const processedName = `${name}=`;
  const cookies = getCookies();

  for (const cookie of cookies) {
    if (cookie.indexOf(processedName) === 0) {
      return decodeURIComponent(cookie).substring(
        processedName.length,
        cookie.length
      );
    }
  }
};

const clearCookies = () => {
  const cookies = getCookies();
  for (const item of cookies) {
    const indexOfEqual = item.indexOf("=");
    const name = indexOfEqual > -1 ? item.substring(0, indexOfEqual) : item;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
  }
};

const fetchAccessToken = () => getCookie("access_token");

const isAuthenticate = () => !!fetchAccessToken();

const getUsername = () => getCookie("username");

const getName = () => getCookie("name") || "Guest";

const getAvatarUrl = () => getCookie("avatar_url") || "";

module.exports = {
  fetchAccessToken,
  isAuthenticate,
  clearCookies,
  getUsername,
  getName,
  getAvatarUrl,
};
