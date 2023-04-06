import { defineConfig } from 'vite';
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
    vuetify()
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    },
    extensions: ['.vue', '.js']
  },
  base: './',
  server: {
    proxy: {
      '^/v1|^/config|^/login': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    },
    port: 8081,
    strictPort: true
  },
  test: {
    environment: 'jsdom',
    deps: {
      inline: ['vuetify']
    },
    coverage: {
      reporter: ['text', 'html'],
      all: true
    },
    setupFiles: '__tests__/vitest.setup.js'
  }
});
