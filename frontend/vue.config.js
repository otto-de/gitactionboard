module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      "^/v1|^/available-auths|^/login": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
};
