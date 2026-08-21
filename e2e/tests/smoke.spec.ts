import { test, expect } from '@playwright/test';

// Stage 0 smoke: the walking skeleton boots and a user can log in and
// reach the home dashboard. This spec is cumulative — it never gets
// deleted, only updated when the login flow intentionally changes.
test('admin can log in and reach the home dashboard', async ({ page }) => {
  await page.goto('/openmrs/spa/login');

  await page.getByRole('textbox', { name: /username/i }).fill('admin');
  await page.getByRole('button', { name: /continue/i }).click();

  await page.getByRole('textbox', { name: /password/i }).fill('Admin123');
  await page.getByRole('button', { name: /log in/i }).click();

  // The location picker only shows when >1 login location exists. Its list
  // lazy-loads and re-renders, which can silently drop the selection or
  // swallow the Confirm click (seen on slow CI runners) — so retry the whole
  // select → confirm → navigate sequence until we actually land on /home.
  await expect(async () => {
    if (page.url().includes('/login/location')) {
      await page.getByRole('radio', { name: 'Outpatient Clinic' }).check({ force: true });
      const confirm = page.getByRole('button', { name: /confirm/i });
      await expect(confirm).toBeEnabled({ timeout: 3_000 });
      await confirm.click();
    }
    await expect(page).toHaveURL(/\/home/, { timeout: 10_000 });
  }).toPass({ timeout: 120_000 });
  await expect(
    page.getByRole('banner').getByRole('button', { name: /search patient/i }),
  ).toBeVisible();
});
