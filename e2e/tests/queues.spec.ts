import { test, expect, type Page } from '@playwright/test';
import { logIn, endAllActiveQueueEntries } from './helpers';

const AUTH = { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') };
const TRIAGE_QUEUE = 'b6d0f3e4-5a92-4f13-8f6b-1c2a3d4e5f01';
const CONSULT_QUEUE = 'b6d0f3e4-5a92-4f13-8f6b-1c2a3d4e5f02';

test.beforeAll(async ({ request }) => {
  await endAllActiveQueueEntries(request);
});

// Stage 2 (task 2.7): front desk queues the patient at check-in (queue fields
// on the start-visit form), the patient shows on the service-queues board as
// Waiting, and can be moved on to the Consultation queue.

test('check-in queues the patient for triage; queue board shows and moves them', async ({ page, request }) => {
  test.setTimeout(300_000);

  await logIn(page);
  await page.goto('/openmrs/spa/patient-registration');
  const family = `Queue${Date.now() % 100000}`;
  await page.getByLabel(/first name/i).fill('Abena');
  await page.getByLabel(/family name/i).fill(family);
  await page.getByText(/^female$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('05/05/1992');
  await page.getByRole('button', { name: /register patient/i }).click();
  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  // Start the OPD visit and queue for Triage in the same form (check-in).
  await page.getByRole('button', { name: /start a visit/i }).first().click({ timeout: 60_000 });
  await page.getByRole('radio').first().check({ force: true, timeout: 30_000 });
  await page.getByRole('combobox', { name: /select a queue location/i }).selectOption({ label: 'Triage' });
  await page.getByRole('combobox', { name: /select a service/i }).selectOption({ label: 'Triage' });
  // Priority renders as a button group; Not Urgent is the default when present.
  const notUrgent = page.getByRole('tab', { name: /not urgent/i }).or(page.getByText(/^not urgent$/i)).first();
  if (await notUrgent.isVisible().catch(() => false)) {
    await notUrgent.click();
  }
  await page.getByRole('button', { name: /^start a visit$/i }).last().click();
  await expect(page.getByText(/active visit/i).first()).toBeVisible({ timeout: 60_000 });

  // Backend truth: one queue entry, in the Triage queue, status Waiting.
  // The form submits the queue entry asynchronously after the visit — poll.
  let entries: any[] = [];
  await expect
    .poll(async () => {
      const res = await request.get(`/openmrs/ws/rest/v1/queue-entry?patient=${uuid}&v=full`, { headers: AUTH });
      entries = (await res.json()).results;
      return entries.length;
    }, { timeout: 30_000 })
    .toBe(1);
  expect(entries[0].queue.uuid).toBe(TRIAGE_QUEUE);
  expect(entries[0].status.display).toBe('Waiting');

  // The queue board scopes to a queue location ("View" dropdown, defaults to
  // the login location) — switch to Triage, where the entry lives.
  await page.goto('/openmrs/spa/home/service-queues');
  await page.getByRole('combobox', { name: /view/i }).first().click({ timeout: 60_000 });
  await page.getByRole('option', { name: /^triage$/i }).first().click();
  const row = page.getByRole('row').filter({ hasText: family }).first();
  await expect(row).toBeVisible({ timeout: 60_000 });
  await expect(row).toContainText(/waiting/i);

  // Vitals done — move the patient on to the Consultation queue.
  await row.getByRole('button', { name: /transfer/i }).click();
  const dlg = page.locator('[role="dialog"], .cds--modal-container').first();
  await expect(dlg.getByText(/move patient to the next service/i)).toBeVisible({ timeout: 30_000 });
  await dlg.getByRole('combobox').first().selectOption({ label: 'Consultation Room 1' });
  // The service list refreshes async after the location pick, and its option
  // label is the queue's, not the service concept's — match loosely.
  const serviceSelect = dlg.getByRole('combobox').nth(1);
  await expect(async () => {
    const labels = await serviceSelect.locator('option').allInnerTexts();
    const target = labels.map((l) => l.trim()).find((l) => /consultation/i.test(l));
    if (!target) throw new Error(`no consultation option yet, saw: ${labels.join('|')}`);
    await serviceSelect.selectOption({ label: target });
  }).toPass({ timeout: 30_000 });
  await dlg.getByText(/^waiting$/i).click();
  await dlg.getByRole('button', { name: /move to next service/i }).click();

  // Present under Consultation Room 1 with the Consultation service. (The old
  // view can keep the stale row until the next poll — not asserted.)
  await page.getByRole('combobox', { name: /view/i }).first().click();
  await page.getByRole('option', { name: /consultation room 1/i }).first().click();
  const consultRow = page.getByRole('row').filter({ hasText: family }).first();
  await expect(consultRow).toBeVisible({ timeout: 60_000 });
  await expect(consultRow).toContainText(/consultation/i);

  // Backend truth: the single active entry is now the Consultation queue's.
  const afterRes = await request.get(`/openmrs/ws/rest/v1/queue-entry?patient=${uuid}&v=full`, { headers: AUTH });
  const active = (await afterRes.json()).results.filter((e: { endedAt: string | null }) => !e.endedAt);
  expect(active).toHaveLength(1);
  expect(active[0].queue.uuid).toBe(CONSULT_QUEUE);
});
