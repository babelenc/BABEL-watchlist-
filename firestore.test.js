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

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read library", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("library").get());
});

test("Unauthenticated user: cannot create library entry", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("library").doc("entry_1").set({
    id: "entry_1",
    userId: ALICE_UID,
    title: "Interstellar",
    mediaType: "movie",
    status: "completed"
  }));
});

// 2. Cross-user isolation checks
test("Authenticated user: cannot read another user's library", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("library").doc("entry_bob").set({
      id: "entry_bob",
      userId: BOB_UID,
      title: "Attack on Titan",
      mediaType: "anime",
      status: "watching"
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("library").doc("entry_bob").get());
});

test("Authenticated user: cannot write to another user's library", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("library").doc("entry_hack").set({
    id: "entry_hack",
    userId: BOB_UID,
    title: "Inception",
    mediaType: "movie",
    status: "plan_to_watch"
  }));
});

// 3. Authenticated owner checks
test("Authenticated user: can create and read their own library entry", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("library").doc("entry_alice").set({
    id: "entry_alice",
    userId: ALICE_UID,
    title: "Spirited Away",
    mediaType: "anime",
    status: "completed",
    personalRating: 9.5
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("library").doc("entry_alice").get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("library").get());
});

test("Authenticated user: can create and read their custom collection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("collections").doc("col_1").set({
    id: "col_1",
    userId: ALICE_UID,
    name: "Masterpieces",
    titleIds: ["entry_alice"]
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("collections").doc("col_1").get());
});
