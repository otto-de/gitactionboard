const getCookie = (name) => {
  const processedName = `${name}=`;

  const decodedCookie = decodeURIComponent(document.cookie);

  const cookies = decodedCookie.split(";");

  for (const item of cookies) {
    const cookie = item.trim();

    if (cookie.indexOf(processedName) === 0) {
      return cookie.substring(processedName.length, cookie.length);
    }
  }
};

const clearCookies = () => {
  const cookies = document.cookie.split(";");
  for (const item of cookies) {
    const indexOfEqual = item.indexOf("=");
    const name = indexOfEqual > -1 ? item.substr(0, indexOfEqual) : item;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
  }
};

const fetchAccessToken = () => getCookie("access_token");

const isAuthenticate = () => !!fetchAccessToken();

module.exports = { fetchAccessToken, isAuthenticate, clearCookies };
