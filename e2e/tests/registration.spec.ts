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
