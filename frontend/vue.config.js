module.exports = {
  publicPath: "./",
  devServer: {
    port: 8081,
    proxy: {
      "^/v1|^/config|^/login": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
};
