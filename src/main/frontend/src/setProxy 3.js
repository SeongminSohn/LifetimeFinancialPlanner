const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
    app.use(
        '/api', // API 요청 경로 (React에서 /api로 요청하면 Spring으로 전달됨)
        createProxyMiddleware({
            target: 'http://localhost:10000', // Spring 서버 주소
            changeOrigin: true,
        })
    );
};
