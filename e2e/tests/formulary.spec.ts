import { test, expect } from '@playwright/test';
import { logIn } from './helpers';

const AUTH = { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') };

// The drug order form crashes without a provider record for the session user
// (reads currentProvider.uuid) — the EMR-only profile ships none for admin.
test.beforeAll(async ({ request }) => {
  const users = await (
    await request.get('/openmrs/ws/rest/v1/user?q=admin&v=custom:(uuid,username,systemId,person:(uuid))', { headers: AUTH })
  ).json();
  // q= matches loosely (e.g. a "Kojo Admin" dev user) — take the real admin.
  const admin = users.results.find(
    (u: { username?: string; systemId?: string }) => (u.username || u.systemId) === 'admin',
  );
  const providers = await (
    await request.get(`/openmrs/ws/rest/v1/provider?q=superuser`, { headers: AUTH })
  ).json();
  if (providers.results.length === 0) {
    const res = await request.post('/openmrs/ws/rest/v1/provider', {
      headers: AUTH,
      data: { person: admin.person.uuid, identifier: 'superuser' },
    });
    if (!res.ok()) throw new Error(`provider create failed: ${res.status()} ${await res.text()}`);
  }
});

// Stage 2 (task 2.5): the order basket offers the NHIS formulary — and only
// it. Demo drugs are retired; every active drug is one of the 425 NHIS rows.

test('drug search API serves NHIS formulary, demo drugs are gone', async ({ request }) => {
  // Paginate through all active drugs.
  const all: { display: string }[] = [];
  let url = '/openmrs/ws/rest/v1/drug?v=custom:(display)&limit=100';
  while (url) {
    const res = await request.get(url, { headers: AUTH });
    expect(res.ok()).toBeTruthy();
    const body = await res.json();
    all.push(...body.results);
    const next = (body.links || []).find((l: { rel: string }) => l.rel === 'next');
    url = next ? next.uri.replace(/^.*\/openmrs\//, '/openmrs/') : '';
  }
  // 441 in-scope NHIS formulations − 16 with no CIEL concept − 1 pack-size
  // duplicate (benzyl benzoate 30/100 mL) = 424.
  expect(all.length).toBe(424);

  const names = all.map((d) => d.display);
  // NHIS staples present…
  for (const staple of [
    'Amodiaquine + Artesunate Tablet, 135 mg + 50 mg (12 tabs)',
    'Amoxicillin + Clavulanic Acid Tablet, 500 mg + 125 mg',
    'Paracetamol Tablet, 500 mg',
  ]) {
    expect(names, `expected NHIS staple "${staple}"`).toContain(staple);
  }
  // …demo-only drugs gone (US-market formulations from the O3 demo set).
  expect(names.find((n) => /oxycodone/i.test(n))).toBeUndefined();
  expect(names.find((n) => /famotidine/i.test(n))).toBeUndefined();
});

// The prescriber can actually find and add an NHIS drug in the order basket.
test('order basket finds an NHIS drug during an OPD visit', async ({ page, request }) => {
  test.setTimeout(300_000);

  await logIn(page);
  await page.goto('/openmrs/spa/patient-registration');
  const family = `Rx${Date.now() % 100000}`;
  await page.getByLabel(/first name/i).fill('Esi');
  await page.getByLabel(/family name/i).fill(family);
  await page.getByText(/^female$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('06/06/1990');
  await page.getByRole('button', { name: /register patient/i }).click();
  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  // An active visit is required for ordering. The start-visit form includes
  // the queue fields (2.7) — leaving them empty triggers a broken
  // queue-entry-number call whose error dialog blocks the workspace, so
  // check in properly, like the front desk would.
  await page.getByRole('button', { name: /start a visit/i }).first().click({ timeout: 60_000 });
  await page.getByRole('radio').first().check({ force: true, timeout: 30_000 });
  await page.getByRole('combobox', { name: /select a queue location/i }).selectOption({ label: 'Triage' });
  await page.getByRole('combobox', { name: /select a service/i }).selectOption({ label: 'Triage' });
  await page.getByRole('button', { name: /^start a visit$/i }).last().click();
  await expect(page.getByText(/active visit/i).first()).toBeVisible({ timeout: 60_000 });

  // Order basket: search for the malaria first-line and expect the NHIS row.
  await page.getByRole('button', { name: /order basket/i }).click();
  // The drug search only appears after Add under "Drug orders" (the basket's
  // first section, so the first Add button).
  await expect(page.getByRole('heading', { name: /drug orders/i })).toBeVisible({ timeout: 60_000 });
  await page.getByRole('button', { name: /^add$/i }).first().click({ timeout: 30_000 });
  const search = page.getByPlaceholder(/search for a drug/i).first();
  await search.fill('Artemether');
  await expect(
    page.getByText(/Artemether \+ Lumefantrine Tablet, 20 mg \+ 120 mg/i).first(),
  ).toBeVisible({ timeout: 30_000 });

  // End the visit to leave no active state behind.
  await page.goto(`/openmrs/spa/patient/${uuid}/chart`);
  await page.getByRole('button', { name: /^end visit$/i }).first().click({ timeout: 60_000 });
  await page
    .locator('[role="dialog"], .cds--modal-container')
    .getByRole('button', { name: /end visit/i })
    .first()
    .click({ timeout: 10_000 })
    .catch(() => {});
  await expect(page.getByText(/active visit/i)).toHaveCount(0, { timeout: 60_000 });
});
