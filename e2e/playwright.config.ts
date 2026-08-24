import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  timeout: 180_000,
  expect: { timeout: 30_000 },
  // Specs share one live stack and the queue specs mutate/clean global queue
  // state — parallel workers race each other (e.g. one spec's cleanup ends
  // another's just-created queue entry).
  workers: 1,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never' }]] : 'list',
  use: {
    baseURL: process.env.E2E_BASE_URL ?? 'http://localhost',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
});
