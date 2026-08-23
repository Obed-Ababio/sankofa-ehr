import { test, expect, type Page } from '@playwright/test';
import { logIn } from './helpers';

// Task 1.9 / Test Gate 1: a patient can be found by every search path the
// front desk uses — name, folder number, Ghana Card, NHIS number, phone.
// Registers its own patient with all identifiers, then searches five ways.

async function searchAndExpect(page: Page, term: string, family: string, label: string) {
  // Must be the patient search, not e.g. a home-page table's "Filter table"
  // searchbox — which only exists when the DB has data (seeded local stacks).
  const box = page.getByPlaceholder(/search for a patient/i).first();
  if (!(await box.isVisible().catch(() => false))) {
    await page.getByRole('banner').getByRole('button', { name: /search patient/i }).click();
  }
  await box.fill(term);
  await expect(
    page.getByText(new RegExp(family)).first(),
    `search by ${label} ("${term}") must find ${family}`,
  ).toBeVisible({ timeout: 15_000 });
}

test('patient is findable by name, folder number, Ghana Card, NHIS and phone', async ({ page, request }) => {
  const stamp = Date.now();
  const family = `Searchable${stamp % 100000}`;
  const pin = `GHA-${String(stamp).slice(-9)}-2`;
  const nhis = `9${String(stamp).slice(-7)}`;
  const phone = `055${String(stamp).slice(-7)}`;

  await logIn(page);
  await page.goto('/openmrs/spa/patient-registration');
  await page.getByLabel(/first name/i).fill('Adwoa');
  await page.getByLabel(/family name/i).fill(family);
  await page.getByText(/^female$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('04/04/1988');
  await page.getByLabel(/ghana card pin/i).fill(pin);
  await page.getByLabel(/nhis membership number/i).fill(nhis);
  await page.getByLabel(/^telephone number/i).first().fill(phone);
  await page.getByRole('button', { name: /register patient/i }).click();
  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  // Folder number was auto-generated — read it back.
  const res = await request.get(
    `/openmrs/ws/rest/v1/patient/${uuid}?v=custom:(identifiers:(identifierType:(display),identifier))`,
    { headers: { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') } },
  );
  const folder = (await res.json()).identifiers.find(
    (i: { identifierType: { display: string } }) => i.identifierType.display === 'Clinic folder number',
  ).identifier;

  await page.goto('/openmrs/spa/home');
  await searchAndExpect(page, family, family, 'name');
  await searchAndExpect(page, folder, family, 'folder number');
  await searchAndExpect(page, pin, family, 'Ghana Card');
  await searchAndExpect(page, nhis, family, 'NHIS number');
  await searchAndExpect(page, phone, family, 'phone');
});
