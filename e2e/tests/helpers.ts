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
      await page.getByRole('radio', { name: 'Registration' }).check({ force: true });
      const confirm = page.getByRole('button', { name: /confirm/i });
      await expect(confirm).toBeEnabled({ timeout: 3_000 });
      await confirm.click();
    }
    await expect(page).toHaveURL(/\/home/, { timeout: 10_000 });
  }).toPass({ timeout: 120_000 });
}

// Queue entries outlive their visits (ending a visit does NOT end its queue
// entry), so test runs accumulate Waiting rows until the board paginates new
// patients out of sight. Call before any spec that asserts on the board.
export async function endAllActiveQueueEntries(request: import('@playwright/test').APIRequestContext) {
  const auth = { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') };
  const res = await request.get('/openmrs/ws/rest/v1/queue-entry?v=custom:(uuid,endedAt)&limit=100', {
    headers: auth,
  });
  const entries: { uuid: string; endedAt: string | null }[] = (await res.json()).results;
  const now = new Date().toISOString();
  for (const e of entries.filter((e) => !e.endedAt)) {
    await request.post(`/openmrs/ws/rest/v1/queue-entry/${e.uuid}`, {
      headers: auth,
      data: { endedAt: now },
    });
  }
}
