// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
	host: '0.0.0.0',
    port: 3000,
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/setupTests.js',
    css: true,
    coverage: {
      provider: 'istanbul', // Switch to 'istanbul' provider
      reporter: ['text', 'html', 'lcov'], // Include 'cobertura' reporter
      all: true,
      include: ['src/components/**/*.{js,jsx}'],
      exclude: ['node_modules/', 'src/setupTests.js'],
      reportsDirectory: './coverage',
      thresholds: {
        global: {
          statements: 70,
          branches: 50,
          functions: 70,
          lines: 70,
        },
      },
    },
  },
});
