package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CustomDesign
import com.example.data.model.Order
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.tasks.await
import java.util.UUID

const val ADMIN_EMAIL = "pekanexus32@gmail.com"

class AppRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun isAdmin(): Boolean {
        return auth.currentUser?.email?.equals(ADMIN_EMAIL, ignoreCase = true) == true
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- ORDERS (PURCHASES) ---

    // Observes user's own orders, or ALL orders if current user is admin
    fun observeOrders(): Flow<List<Order>> = flow {
        val uid = requireUserId()
        val path = if (isAdmin()) "orders" else "orders"

        val query = if (isAdmin()) {
            // Admin can see all orders in the shop
            db.collection("orders")
        } else {
            // Regular user only sees their own orders
            db.collection("orders").whereEqualTo("userId", uid)
        }

        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createOrder(order: Order): Result<String> {
        val uid = requireUserId()
        val orderId = if (order.id.isNotBlank()) order.id else "ord_${UUID.randomUUID().toString().replace("-", "").take(12)}"

        // Write directly to the root /orders/{orderId} collection so it's instantly visible in console
        val rootOrderRef = db.collection("orders").document(orderId)

        val payload = mapOf(
            "id" to orderId,
            "userId" to uid,
            "itemsCount" to order.itemsCount,
            "subtotal" to order.subtotal,
            "shippingCost" to order.shippingCost,
            "total" to order.total,
            "currency" to order.currency,
            "paymentMethod" to order.paymentMethod,
            "paymentStatus" to order.paymentStatus,
            "orderStatus" to order.orderStatus,
            "trackingNumber" to (order.trackingNumber ?: "TRK-${(100000..999999).random()}"),
            "shippingName" to order.shippingName,
            "shippingAddress" to order.shippingAddress,
            "shippingCountry" to order.shippingCountry,
            "shippingCity" to order.shippingCity,
            "shippingZip" to order.shippingZip,
            "shippingPhone" to order.shippingPhone,
            "itemsSummary" to order.itemsSummary,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            rootOrderRef.set(payload).await()

            // Also mirror under /users/{userId}/orders/{orderId} for backup isolation
            try {
                db.collection("users").document(uid).collection("orders").document(orderId).set(payload).await()
            } catch (_: Exception) {
                // Secondary mirror, root order is primary
            }

            Result.success(orderId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, rootOrderRef.path)
            Result.failure(e)
        }
    }

    // Admin exclusive action: Update order status & tracking number
    suspend fun updateOrderStatus(orderId: String, newStatus: String, trackingNumber: String? = null): Result<Unit> {
        val rootOrderRef = db.collection("orders").document(orderId)
        val updates = mutableMapOf<String, Any>(
            "orderStatus" to newStatus,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!trackingNumber.isNullOrBlank()) {
            updates["trackingNumber"] = trackingNumber
        }

        return try {
            rootOrderRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, rootOrderRef.path)
            Result.failure(e)
        }
    }

    // --- CUSTOM DESIGNS ---

    fun observeCustomDesigns(): Flow<List<CustomDesign>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/custom_designs"
        emitAll(
            db.collection("users").document(uid).collection("custom_designs")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(CustomDesign::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L } }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveCustomDesign(design: CustomDesign): Result<String> {
        val uid = requireUserId()
        val designId = if (design.id.isNotBlank()) design.id else "des_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("users").document(uid).collection("custom_designs").document(designId)

        val payload = mutableMapOf<String, Any>(
            "id" to designId,
            "userId" to uid,
            "title" to design.title,
            "baseMaterial" to design.baseMaterial,
            "stoneType" to design.stoneType,
            "cordColor" to design.cordColor,
            "wristSizeCm" to design.wristSizeCm,
            "claspType" to design.claspType,
            "estimatedPrice" to design.estimatedPrice,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!design.engravingText.isNullOrBlank()) {
            payload["engravingText"] = design.engravingText
        }

        return try {
            docRef.set(payload).await()
            Result.success(designId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCustomDesign(designId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("custom_designs").document(designId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- USER PROFILE ---

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "email" to profile.email,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!profile.displayName.isNullOrBlank()) payload["displayName"] = profile.displayName
        if (!profile.photoUrl.isNullOrBlank()) payload["photoUrl"] = profile.photoUrl

        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }
}
