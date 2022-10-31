const getCookies = () =>
  document.cookie.split(';').map((cookie) => cookie.trim());

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

export const clearCookies = () => {
  const cookies = getCookies();
  for (const item of cookies) {
    const indexOfEqual = item.indexOf('=');
    const name = indexOfEqual > -1 ? item.substring(0, indexOfEqual) : item;
    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT';
  }
};

export const fetchAccessToken = () => getCookie('access_token');

export const isAuthenticate = () => !!fetchAccessToken();

export const getName = () => getCookie('name') || 'Guest';

export const getAvatarUrl = () => getCookie('avatar_url') || '';
