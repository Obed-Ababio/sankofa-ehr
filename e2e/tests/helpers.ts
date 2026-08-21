import { expect, type Page } from '@playwright/test';

export async function logIn(page: Page, username = 'admin', password = 'Admin123') {
  await page.goto('/openmrs/spa/login');
  await page.getByRole('textbox', { name: /username/i }).fill(username);
  await page.getByRole('button', { name: /continue/i }).click();
  await page.getByRole('textbox', { name: /password/i }).fill(password);
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
}
