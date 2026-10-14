const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";
const ADMIN_EMAIL = "pekanexus32@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read root orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").get());
});

test("Public user: can read products catalog, but CANNOT modify products", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("products").doc("p1").set({
      name: "Pulsera Fake",
      price: 1.0
    })
  );
});

test("Admin (pekanexus32@gmail.com): CAN modify products and prices in catalog", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL
  }).firestore();

  await assertSucceeds(
    adminDb.collection("products").doc("p1").set({
      name: "Pulsera 7 Chakras",
      price: 4.80
    })
  );
});

test("Authenticated customer: can create own order, but CANNOT update status or delete it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_alice_1").set({
      id: "order_alice_1",
      userId: ALICE_UID,
      subtotal: 4.80,
      total: 7.79,
      shippingCountry: "España"
    })
  );

  // Alice cannot update order status (only admin can!)
  await assertFails(
    aliceDb.collection("orders").doc("order_alice_1").update({
      orderStatus: "entregado"
    })
  );

  // Alice cannot delete order
  await assertFails(
    aliceDb.collection("orders").doc("order_alice_1").delete()
  );
});

test("Admin (pekanexus32@gmail.com): can read, update status, and delete ANY order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("order_bob_1").set({
      id: "order_bob_1",
      userId: BOB_UID,
      subtotal: 3.90,
      total: 6.89,
      orderStatus: "recibido"
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL
  }).firestore();

  // Admin can read
  await assertSucceeds(adminDb.collection("orders").doc("order_bob_1").get());

  // Admin can update status
  await assertSucceeds(
    adminDb.collection("orders").doc("order_bob_1").update({
      orderStatus: "en_taller"
    })
  );

  // Admin can delete
  await assertSucceeds(
    adminDb.collection("orders").doc("order_bob_1").delete()
  );
});
