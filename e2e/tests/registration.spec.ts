import { test, expect } from '@playwright/test';
import { logIn } from './helpers';

// Stage 1 (task 1.1/1.9): registering a new patient auto-generates the
// clinic folder number — prefix from the Mod-30 alphabet, 6-digit
// sequence, Luhn Mod-30 check digit. The patient brings nothing; the MRN
// is always system-assigned.
test('registering a new patient auto-generates a valid clinic folder number', async ({ page, request }) => {
  await logIn(page);

  await page.goto('/openmrs/spa/patient-registration');
  await page.getByLabel(/first name/i).fill('Ama');
  await page.getByLabel(/family name/i).fill('Mensah');
  await page.getByText(/^female$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('01/01/1990');
  await page.getByRole('button', { name: /register patient/i }).click();

  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  const res = await request.get(
    `/openmrs/ws/rest/v1/patient/${uuid}?v=custom:(identifiers:(identifierType:(display),identifier))`,
    { headers: { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') } },
  );
  expect(res.ok()).toBeTruthy();
  const identifiers: { identifierType: { display: string }; identifier: string }[] =
    (await res.json()).identifiers;

  const folder = identifiers.find((i) => i.identifierType.display === 'Clinic folder number');
  expect(folder, `identifiers were: ${JSON.stringify(identifiers)}`).toBeTruthy();
  expect(folder!.identifier).toMatch(/^ACC\d{6}[0-9ACDEFGHJKLMNPRTUVWXY]$/);
});

// Task 1.4/1.9: Ghana Card is pinned on the form (no Configure step) and a
// patient can register with one; phone numbers are validated to the
// NCA-derived format 0XXXXXXXXX.
test('registering with a Ghana Card and phone stores both; bad phone is rejected', async ({ page, request }) => {
  await logIn(page);
  await page.goto('/openmrs/spa/patient-registration');

  await page.getByLabel(/first name/i).fill('Kofi');
  await page.getByLabel(/family name/i).fill(`Adjei${Date.now() % 10000}`);
  await page.getByText(/^male$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('02/02/1980');

  // Ghana Card field is visible without any Configure interaction.
  const ghanaCard = page.getByLabel(/ghana card pin/i);
  await expect(ghanaCard).toBeVisible();
  const pin = `GHA-${String(Date.now()).slice(-9)}-1`;
  await ghanaCard.fill(pin);

  // Invalid phone must block, valid phone must pass.
  const phone = page.getByLabel(/^telephone number/i).first();
  await phone.fill('12345');
  await phone.blur();
  await expect(page.getByText(/invalid|match|format/i).first()).toBeVisible();
  await phone.fill('0244123456');

  await page.getByRole('button', { name: /register patient/i }).click();
  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  const res = await request.get(
    `/openmrs/ws/rest/v1/patient/${uuid}?v=custom:(identifiers:(identifierType:(display),identifier),person:(attributes:(display)))`,
    { headers: { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') } },
  );
  const body = await res.json();
  const ids = body.identifiers.map((i: any) => i.identifier);
  expect(ids).toContain(pin);
  const attrs = body.person.attributes.map((a: any) => a.display).join(';');
  expect(attrs).toContain('0244123456');
});
