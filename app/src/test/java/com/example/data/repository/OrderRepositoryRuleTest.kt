package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CustomDesign
import com.example.data.model.Order
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class OrderRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createOrder_authenticatedUser_succeedsAndPersists() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = AppRepository(firestore)

        val uniqueOrderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val order = Order(
            id = uniqueOrderId,
            userId = auth.currentUser!!.uid,
            itemsCount = 2L,
            subtotal = 38.0,
            shippingCost = 8.5,
            total = 46.5,
            currency = "USD",
            paymentMethod = "stripe_card",
            paymentStatus = "paid",
            orderStatus = "recibido",
            shippingName = "Alice Crafts",
            shippingAddress = "Av. Sol 123",
            shippingCountry = "Peru",
            shippingCity = "Cusco",
            shippingZip = "08000",
            shippingPhone = "+51987654321",
            itemsSummary = "1x Pulsera 7 Chakras, 1x Pulsera Ojo de Tigre"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createOrder(order)
        }
        assertTrue("Expected order creation to succeed", result.isSuccess)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeOrders().first()
        }
        assertTrue("Expected observed orders to contain new order", observed.any { it.id == uniqueOrderId })
    }

    @Test
    fun getOrder_crossUserAccess_isRejectedByRules() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = AppRepository(firestore)
        val uniqueOrderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val order = Order(
            id = uniqueOrderId,
            userId = auth.currentUser!!.uid,
            itemsCount = 1L,
            subtotal = 25.0,
            shippingCost = 5.0,
            total = 30.0,
            currency = "USD",
            paymentMethod = "paypal",
            paymentStatus = "paid",
            orderStatus = "recibido",
            shippingName = "Alice Crafts",
            shippingAddress = "Av. Sol 123",
            shippingCountry = "Peru",
            shippingCity = "Cusco",
            shippingZip = "08000",
            shippingPhone = "+51987654321"
        )
        aliceRepo.createOrder(order)

        // Bob attempts to read Alice's document directly from Firestore
        signInTestUser(BOB_EMAIL)
        val bobDb = firestore

        var crossUserFailed = false
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobDb.collection("users").document(order.userId).collection("orders").document(uniqueOrderId).get().await()
            }
        } catch (e: FirebaseFirestoreException) {
            crossUserFailed = true
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
        assertTrue("Expected PERMISSION_DENIED on cross-user read", crossUserFailed)
    }

    @Test
    fun observeOrders_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = AppRepository(firestore)

        var unauthFailed = false
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeOrders().first()
            }
        } catch (e: IllegalStateException) {
            unauthFailed = true
            assertTrue(e.message!!.contains("User must be signed in with Google"))
        }
        assertTrue("Expected unauthenticated user exception", unauthFailed)
    }

    @Test
    fun customDesign_createAndObserve_succeedsForOwner() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = AppRepository(firestore)

        val uniqueDesignId = "des_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val design = CustomDesign(
            id = uniqueDesignId,
            userId = auth.currentUser!!.uid,
            title = "Pulsera Andina Luna",
            baseMaterial = "macrame",
            stoneType = "amatista",
            cordColor = "rojo",
            wristSizeCm = 17.5,
            engravingText = "AMOR Y PAZ",
            claspType = "nudo_corredizo",
            estimatedPrice = 32.0
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveCustomDesign(design)
        }
        assertTrue("Expected saveCustomDesign to succeed", result.isSuccess)

        val observedDesigns = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCustomDesigns().first()
        }
        assertTrue("Expected observed designs to contain new design", observedDesigns.any { it.id == uniqueDesignId })
    }

    private companion object {
        const val ALICE_EMAIL = "alice@test.com"
        const val BOB_EMAIL = "bob@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
