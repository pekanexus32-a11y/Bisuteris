package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    val id: String = "",
    val userId: String = "",
    val itemsCount: Long = 1L,
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "USD",
    val paymentMethod: String = "stripe_card",
    val paymentStatus: String = "paid",
    val orderStatus: String = "recibido",
    val trackingNumber: String? = null,
    val shippingName: String = "",
    val shippingAddress: String = "",
    val shippingCountry: String = "",
    val shippingCity: String = "",
    val shippingZip: String = "",
    val shippingPhone: String = "",
    val itemsSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomDesign(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val baseMaterial: String = "macrame",
    val stoneType: String = "ojo_de_tigre",
    val cordColor: String = "negro",
    val wristSizeCm: Double = 18.0,
    val engravingText: String? = null,
    val claspType: String = "nudo_corredizo",
    val estimatedPrice: Double = 28.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

// UI and Domain Models
enum class Currency(val code: String, val symbol: String, val rateToUsd: Double) {
    USD("USD", "$", 1.0),
    EUR("EUR", "€", 0.92),
    MXN("MXN", "MX$", 17.5),
    GBP("GBP", "£", 0.79);

    fun format(amountUsd: Double): String {
        val converted = amountUsd * rateToUsd
        return when (this) {
            USD -> "$${"%.2f".format(converted)}"
            EUR -> "€${"%.2f".format(converted)}"
            MXN -> "MX$${"%.0f".format(converted)}"
            GBP -> "£${"%.2f".format(converted)}"
        }
    }
}

enum class PaymentGateway(val id: String, val title: String, val subtitle: String, val iconBadge: String) {
    STRIPE_CARD("stripe_card", "Tarjeta Internacional (Stripe)", "Visa, Mastercard, AMEX con 3D Secure", "💳"),
    PAYPAL("paypal", "PayPal Checkout", "Protección al comprador internacional", "🅿️"),
    GOOGLE_PAY("google_pay", "Google Pay / Apple Pay", "Pago rápido y cifrado en 1 toque", "⚡")
}

data class BraceletProduct(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val materials: String,
    val technique: String,
    val basePriceUsd: Double,
    val originalPriceUsd: Double? = null,
    val drawableResId: Int,
    val rating: Double,
    val reviewsCount: Int,
    val availableColors: List<String>,
    val availableSizesCm: List<Double>,
    val tags: List<String>
)

data class CartItem(
    val cartItemId: String,
    val productId: String?,
    val title: String,
    val description: String,
    val priceUsd: Double,
    val quantity: Int = 1,
    val selectedColor: String = "Original",
    val selectedSizeCm: Double = 18.0,
    val customEngraving: String? = null,
    val imageResId: Int? = null,
    val isCustomCraft: Boolean = false
) {
    val totalUsd: Double get() = priceUsd * quantity
}
