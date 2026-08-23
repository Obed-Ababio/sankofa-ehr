import { test, expect, type Page } from '@playwright/test';
import { logIn } from './helpers';

const AUTH = { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') };

// Stage 2 (tasks 2.2 + 2.3): the OPD visit lifecycle on the patient chart —
// start an OPD Visit (the only visit type a Sankofa clinic offers), record
// vitals through the standard O3 vitals app, end the visit. Backed by REST
// asserts that the visit and the vitals observations actually exist.

test('OPD visit: start, record vitals, end — visit and obs persisted', async ({ page, request }) => {
  test.setTimeout(300_000);

  // A fresh patient so visit state is deterministic.
  await logIn(page);
  await page.goto('/openmrs/spa/patient-registration');
  const family = `Opd${Date.now() % 100000}`;
  await page.getByLabel(/first name/i).fill('Yaw');
  await page.getByLabel(/family name/i).fill(family);
  await page.getByText(/^male$/i).click();
  await page.getByLabel(/date of birth/i).first().fill('03/03/1985');
  await page.getByRole('button', { name: /register patient/i }).click();
  await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
  const uuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

  // Start visit — OPD Visit must be the only selectable type (2.2).
  await openStartVisitForm(page);
  const typeRadios = page.getByRole('radio');
  await expect(typeRadios.first()).toBeVisible({ timeout: 30_000 });
  await expect(typeRadios).toHaveCount(1);
  await expect(page.getByText('OPD Visit').first()).toBeVisible();
  await typeRadios.first().check({ force: true });
  // Two buttons share this name: the siderail trigger and the form submit.
  await page.getByRole('button', { name: /^start a visit$/i }).last().click();
  await expect(page.getByText(/active visit/i).first()).toBeVisible({ timeout: 60_000 });

  // Record vitals (2.3) through the vitals workspace form.
  await page.getByRole('button', { name: /vitals/i }).first().click().catch(() => {});
  await page.goto(`/openmrs/spa/patient/${uuid}/chart/Vitals%20%26%20Biometrics`);
  await page.getByRole('button', { name: /record (vital signs|vitals)/i }).first().click();
  const fill = async (label: RegExp, value: string) => {
    const box = page.getByRole('spinbutton', { name: label }).first();
    await box.fill(value);
  };
  await fill(/temperature/i, '38.5'); // above the new 37.4 normal-high => flags abnormal
  await fill(/systolic/i, '120');
  await fill(/diastolic/i, '80');
  await fill(/pulse|heart rate/i, '90');
  await fill(/respiration rate|respiratory rate/i, '18');
  await fill(/oxygen saturation/i, '98');
  await fill(/^weight/i, '70');
  await fill(/^height/i, '170');
  await page.getByRole('button', { name: /save/i }).click();
  await expect(page.getByText(/vitals.*(saved|recorded)/i).first()).toBeVisible({ timeout: 60_000 });

  // The saved observations must exist with the right values (temp via CIEL 5088).
  const obsRes = await request.get(
    `/openmrs/ws/rest/v1/obs?patient=${uuid}&concept=5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&v=custom:(value)`,
    { headers: AUTH },
  );
  const temps = (await obsRes.json()).results.map((o: { value: number }) => o.value);
  expect(temps).toContain(38.5);

  // The abnormal-range config that drives the UI flag must be live (2.3).
  const tempConcept = await (
    await request.get('/openmrs/ws/rest/v1/concept/5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA?v=full', { headers: AUTH })
  ).json();
  expect(tempConcept.hiNormal).toBe(37.4);
  expect(tempConcept.hiCritical).toBe(40);

  // End the visit via the visit-header button; confirm in the modal if shown.
  await page.goto(`/openmrs/spa/patient/${uuid}/chart`);
  await page.getByRole('button', { name: /^end visit$/i }).first().click({ timeout: 60_000 });
  await page
    .locator('[role="dialog"], .cds--modal-container')
    .getByRole('button', { name: /end visit/i })
    .first()
    .click({ timeout: 10_000 })
    .catch(() => {});
  await expect(page.getByText(/active visit/i)).toHaveCount(0, { timeout: 60_000 });

  // REST: exactly one OPD visit, now closed.
  const visitRes = await request.get(
    `/openmrs/ws/rest/v1/visit?patient=${uuid}&v=custom:(visitType:(display),startDatetime,stopDatetime)`,
    { headers: AUTH },
  );
  const visits = (await visitRes.json()).results;
  expect(visits).toHaveLength(1);
  expect(visits[0].visitType.display).toBe('OPD Visit');
  expect(visits[0].stopDatetime).toBeTruthy();
});

// Retired visit types must not be offered anywhere (2.2).
test('only OPD Visit is an active visit type', async ({ request }) => {
  const res = await request.get('/openmrs/ws/rest/v1/visittype?v=custom:(name,retired)', { headers: AUTH });
  const types: { name: string; retired: boolean }[] = (await res.json()).results;
  const active = types.filter((t) => !t.retired).map((t) => t.name);
  expect(active).toEqual(['OPD Visit']);
});

async function openStartVisitForm(page: Page) {
  // Wait for the chart header to settle before looking for the control — the
  // button exists but reports invisible while the chart is still hydrating.
  await expect(page.getByRole('button', { name: 'Actions', exact: true }).first()).toBeVisible({ timeout: 60_000 });
  const direct = page.getByRole('button', { name: /start a visit/i }).first();
  try {
    await direct.click({ timeout: 15_000 });
  } catch {
    // Small viewports tuck it into the Actions overflow menu instead.
    await page.getByRole('button', { name: 'Actions', exact: true }).first().click();
    await page.getByRole('menuitem', { name: /start.*visit/i }).click();
  }
}
