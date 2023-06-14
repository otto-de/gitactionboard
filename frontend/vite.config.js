import { defineConfig, splitVendorChunkPlugin } from 'vite';
import vue from '@vitejs/plugin-vue';

import vuetify, { transformAssetUrls } from 'vite-plugin-vuetify';

import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),
    vuetify(),
    splitVendorChunkPlugin()
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    },
    extensions: ['.vue', '.js']
  },
  base: process.env.BASE_PATH || './',
  server: {
    proxy: {
      '^/v1|^/config|^/login|^/oauth2|^/logout': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    },
    port: 8081,
    strictPort: true,
    open: true
  },
  test: {
    environment: 'jsdom',
    deps: {
      inline: ['vuetify']
    },
    include: ['**/?(*.){test,spec}.?([cm])[jt]s?(x)'],
    coverage: {
      reporter: ['text', 'html'],
      all: true,
      statements: 80.58,
      branches: 94.64,
      functions: 43.65,
      lines: 80.58,
      thresholdAutoUpdate: true
    },
    setupFiles: '__tests__/vitest.setup.js'
  }
});
