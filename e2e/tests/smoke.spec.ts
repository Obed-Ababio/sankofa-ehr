import { test, expect } from '@playwright/test';
import { logIn } from './helpers';

// Stage 0 smoke: the walking skeleton boots and a user can log in and
// reach the home dashboard. This spec is cumulative — it never gets
// deleted, only updated when the login flow intentionally changes.
test('admin can log in and reach the home dashboard', async ({ page }) => {
  await logIn(page);

  await expect(
    page.getByRole('banner').getByRole('button', { name: /search patient/i }),
  ).toBeVisible();
});
