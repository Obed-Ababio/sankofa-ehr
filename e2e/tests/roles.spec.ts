import { test, expect, type APIRequestContext } from '@playwright/test';
import { logIn } from './helpers';

// Task 1.6 / Test Gate 1 item 6: Front Desk is VERIFIABLY blocked from
// clinical data at the API (privilege) level, while still able to do their
// actual job (register and find patients). The Sankofa: Front Desk role
// deliberately does not inherit Privilege Level: High (configuration/roles).

const admin = { Authorization: 'Basic ' + Buffer.from('admin:Admin123').toString('base64') };
const frontdesk = { Authorization: 'Basic ' + Buffer.from('frontdesk:Frontdesk123').toString('base64') };
const clinician = { Authorization: 'Basic ' + Buffer.from('clinician:Clinician123').toString('base64') };

async function ensureUser(request: APIRequestContext, username: string, password: string, roleUuid: string) {
  const res = await request.post('/openmrs/ws/rest/v1/user', {
    headers: { ...admin, 'Content-Type': 'application/json' },
    data: {
      username,
      password,
      person: { names: [{ givenName: username, familyName: 'Test' }], gender: 'F' },
      roles: [{ uuid: roleUuid }],
    },
  });
  // 201 = created; 400/500 = already exists from a previous run — both fine.
  expect([201, 400, 500]).toContain(res.status());
}

test.describe.serial('role isolation', () => {
  let patientUuid: string;

  test.beforeAll(async ({ request }) => {
    await ensureUser(request, 'frontdesk', 'Frontdesk123', 'c39a1f5e-8a11-4a3e-9f01-6ff8f7d10001');
    await ensureUser(request, 'clinician', 'Clinician123', 'c39a1f5e-8a11-4a3e-9f01-6ff8f7d10002');
  });

  test('front desk can register and search, but is denied clinical data', async ({ page, request }) => {
    await logIn(page, 'frontdesk', 'Frontdesk123');

    // Front desk CAN do their job: register a patient through the UI.
    await page.goto('/openmrs/spa/patient-registration');
    await page.getByLabel(/first name/i).fill('Akosua');
    await page.getByLabel(/family name/i).fill(`Rolecheck${Date.now() % 100000}`);
    await page.getByText(/^female$/i).click();
    await page.getByLabel(/date of birth/i).first().fill('03/03/1975');
    await page.getByRole('button', { name: /register patient/i }).click();
    await expect(page).toHaveURL(/\/patient\/[0-9a-f-]+\/chart/, { timeout: 60_000 });
    patientUuid = page.url().match(/patient\/([0-9a-f-]+)\/chart/)![1];

    // …and search patients via the API.
    const search = await request.get('/openmrs/ws/rest/v1/patient?q=Rolecheck', { headers: frontdesk });
    expect(search.status()).toBe(200);

    // …but clinical data reads are privilege-denied.
    for (const resource of [`encounter?patient=${patientUuid}`, `obs?patient=${patientUuid}`]) {
      const res = await request.get(`/openmrs/ws/rest/v1/${resource}`, { headers: frontdesk });
      expect(res.status(), `front desk must be denied ${resource}`).toBe(403);
    }
  });

  test('clinician retains clinical data access', async ({ request }) => {
    for (const resource of [`encounter?patient=${patientUuid}`, `obs?patient=${patientUuid}`]) {
      const res = await request.get(`/openmrs/ws/rest/v1/${resource}`, { headers: clinician });
      expect(res.status(), `clinician must be allowed ${resource}`).toBe(200);
    }
  });

  // fhir2 < 2.5.0 did not enforce OpenMRS privileges (CVE-2025-46823) —
  // front desk could read clinical Observations via /ws/fhir2/R4/. The distro
  // upgrades fhir2 to 2.5.1; this pins the fix so a base-distro downgrade
  // can't silently reopen the leak. 2.5.1 surfaces the denial as an
  // OperationOutcome with HTTP 500 (not 403), so assert on the privilege
  // message rather than the exact status.
  // Each user needs its OWN request context: the shared `request` fixture
  // keeps a cookie jar, and OpenMRS prefers the JSESSIONID from the first
  // user's response over the Basic auth header of the next request.
  test('front desk is denied clinical reads via FHIR, clinician is not', async ({ playwright, baseURL }) => {
    const asFrontdesk = await playwright.request.newContext({ baseURL: baseURL!, extraHTTPHeaders: frontdesk });
    const asClinician = await playwright.request.newContext({ baseURL: baseURL!, extraHTTPHeaders: clinician });
    try {
      for (const resource of ['Observation', 'Encounter']) {
        const denied = await asFrontdesk.get(`/openmrs/ws/fhir2/R4/${resource}?_count=1`);
        expect(denied.status(), `front desk must be denied FHIR ${resource}`).not.toBe(200);
        expect(await denied.text(), `FHIR ${resource} denial must be a privilege check`).toContain('Privileges required');
        const allowed = await asClinician.get(`/openmrs/ws/fhir2/R4/${resource}?_count=1`);
        expect(allowed.status(), `clinician must be allowed FHIR ${resource}`).toBe(200);
      }
      // Front desk keeps what their job needs: FHIR patient lookup.
      const patients = await asFrontdesk.get('/openmrs/ws/fhir2/R4/Patient?_count=1');
      expect(patients.status()).toBe(200);
    } finally {
      await asFrontdesk.dispose();
      await asClinician.dispose();
    }
  });
});
