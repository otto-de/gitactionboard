module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            '^/v1': {
                target: 'http://localhost:8080',
                changeOrigin: true
            },
        }
    }
}
