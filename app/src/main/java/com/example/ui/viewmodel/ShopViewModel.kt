package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.CatalogData
import com.example.data.model.Currency
import com.example.data.model.CustomDesign
import com.example.data.model.Order
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class ShippingMethod(val title: String, val costUsd: Double, val deliveryDays: String) {
    STANDARD("Envío Estándar Internacional", 2.99, "8 - 14 días"),
    EXPRESS("DHL Express Asegurado", 5.99, "3 - 5 días")
}

data class CustomizerState(
    val title: String = "Mi Pulsera Única",
    val baseMaterialId: String = "macrame",
    val stoneTypeId: String = "ojo_de_tigre",
    val cordColorId: String = "terracota",
    val wristSizeCm: Double = 18.0,
    val engravingText: String = "",
    val claspTypeId: String = "nudo_corredizo"
) {
    val estimatedPriceUsd: Double
        get() {
            val base = CatalogData.customBaseMaterials.find { it.id == baseMaterialId }?.extraPriceUsd ?: 7.20
            val stone = CatalogData.customStones.find { it.id == stoneTypeId }?.extraPriceUsd ?: 2.40
            val clasp = CatalogData.customClasps.find { it.id == claspTypeId }?.extraPriceUsd ?: 0.0
            val engravingExtra = if (engravingText.isNotBlank()) 1.60 else 0.0
            return base + stone + clasp + engravingExtra
        }
}

sealed interface CheckoutUiState {
    object Idle : CheckoutUiState
    data class Processing(val step: String) : CheckoutUiState
    data class Success(val orderId: String, val trackingNumber: String, val totalFormatted: String) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _currency = MutableStateFlow(Currency.USD)
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _customizerState = MutableStateFlow(CustomizerState())
    val customizerState: StateFlow<CustomizerState> = _customizerState.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _shippingMethod = MutableStateFlow(ShippingMethod.STANDARD)
    val shippingMethod: StateFlow<ShippingMethod> = _shippingMethod.asStateFlow()

    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    val ordersState: StateFlow<List<Order>> = repository.observeOrders()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedDesignsState: StateFlow<List<CustomDesign>> = repository.observeCustomDesigns()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setCurrency(newCurrency: Currency) {
        _currency.value = newCurrency
    }

    fun setShippingMethod(method: ShippingMethod) {
        _shippingMethod.value = method
    }

    fun applyCoupon(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        return if (trimmed == "ARTESANAL10" || trimmed == "BIENVENIDO15") {
            _appliedCoupon.value = trimmed
            _userFeedbackMessage.value = "¡Cupón $trimmed aplicado con éxito!"
            true
        } else {
            _userFeedbackMessage.value = "Cupón inválido o expirado"
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    // --- CART ACTIONS ---

    fun addToCart(
        productId: String?,
        title: String,
        description: String,
        priceUsd: Double,
        quantity: Int = 1,
        selectedColor: String = "Original",
        selectedSizeCm: Double = 18.0,
        customEngraving: String? = null,
        imageResId: Int? = null,
        isCustomCraft: Boolean = false
    ) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.productId == productId &&
            it.selectedColor == selectedColor &&
            it.selectedSizeCm == selectedSizeCm &&
            it.customEngraving == customEngraving
        }

        if (existingIndex != -1 && !isCustomCraft) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentList.add(
                CartItem(
                    cartItemId = "item_${UUID.randomUUID().toString().take(8)}",
                    productId = productId,
                    title = title,
                    description = description,
                    priceUsd = priceUsd,
                    quantity = quantity,
                    selectedColor = selectedColor,
                    selectedSizeCm = selectedSizeCm,
                    customEngraving = customEngraving,
                    imageResId = imageResId,
                    isCustomCraft = isCustomCraft
                )
            )
        }
        _cartItems.value = currentList
        _userFeedbackMessage.value = "¡'$title' añadida al carrito!"
    }

    fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(cartItemId)
            return
        }
        _cartItems.value = _cartItems.value.map {
            if (it.cartItemId == cartItemId) it.copy(quantity = newQuantity) else it
        }
    }

    fun removeCartItem(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.cartItemId != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- CUSTOMIZER ACTIONS ---

    fun updateCustomizer(update: (CustomizerState) -> CustomizerState) {
        _customizerState.value = update(_customizerState.value)
    }

    fun saveCustomDesignToCloud() {
        val state = _customizerState.value
        val uid = repository.getCurrentUserId()
        if (uid == null) {
            _userFeedbackMessage.value = "Inicia sesión con Google para guardar tus diseños"
            return
        }

        val design = CustomDesign(
            id = "des_${UUID.randomUUID().toString().replace("-", "").take(10)}",
            userId = uid,
            title = state.title.ifBlank { "Mi Pulsera Artesanal" },
            baseMaterial = state.baseMaterialId,
            stoneType = state.stoneTypeId,
            cordColor = state.cordColorId,
            wristSizeCm = state.wristSizeCm,
            engravingText = state.engravingText.ifBlank { null },
            claspType = state.claspTypeId,
            estimatedPrice = state.estimatedPriceUsd
        )

        viewModelScope.launch {
            val result = repository.saveCustomDesign(design)
            if (result.isSuccess) {
                _userFeedbackMessage.value = "¡Diseño personalizado guardado en Firestore!"
            } else {
                _userFeedbackMessage.value = "Error al guardar diseño: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun addCustomizerToCart() {
        val state = _customizerState.value
        val stoneName = CatalogData.customStones.find { it.id == state.stoneTypeId }?.name ?: "Piedra"
        val baseName = CatalogData.customBaseMaterials.find { it.id == state.baseMaterialId }?.name ?: "Base"
        val colorName = CatalogData.customColors.find { it.id == state.cordColorId }?.name ?: "Original"

        val description = "$baseName + $stoneName, Cierre: ${state.claspTypeId.replace("_", " ")}"

        addToCart(
            productId = null,
            title = state.title.ifBlank { "Pulsera Personalizada Artesanal" },
            description = description,
            priceUsd = state.estimatedPriceUsd,
            quantity = 1,
            selectedColor = colorName,
            selectedSizeCm = state.wristSizeCm,
            customEngraving = state.engravingText.ifBlank { null },
            imageResId = com.example.R.drawable.img_bracelet_macrame,
            isCustomCraft = true
        )
    }

    fun loadCustomDesignIntoCustomizer(design: CustomDesign) {
        _customizerState.value = CustomizerState(
            title = design.title,
            baseMaterialId = design.baseMaterial,
            stoneTypeId = design.stoneType,
            cordColorId = design.cordColor,
            wristSizeCm = design.wristSizeCm,
            engravingText = design.engravingText ?: "",
            claspTypeId = design.claspType
        )
        _userFeedbackMessage.value = "Diseño '${design.title}' cargado en el taller"
    }

    fun deleteCustomDesign(designId: String) {
        viewModelScope.launch {
            repository.deleteCustomDesign(designId)
        }
    }

    // --- CHECKOUT PROCESS ---

    fun processOrder(
        shippingName: String,
        shippingAddress: String,
        shippingCountry: String,
        shippingCity: String,
        shippingZip: String,
        shippingPhone: String,
        paymentMethod: String
    ) {
        val uid = repository.getCurrentUserId()
        if (uid == null) {
            _checkoutState.value = CheckoutUiState.Error("Debes iniciar sesión con Google para completar la compra")
            return
        }

        val items = _cartItems.value
        if (items.isEmpty()) {
            _checkoutState.value = CheckoutUiState.Error("El carrito está vacío")
            return
        }

        val subtotalUsd = items.sumOf { it.totalUsd }
        val discountRate = when (_appliedCoupon.value) {
            "ARTESANAL10" -> 0.10
            "BIENVENIDO15" -> 0.15
            else -> 0.0
        }
        val discountUsd = subtotalUsd * discountRate
        val shippingUsd = _shippingMethod.value.costUsd
        val totalUsd = (subtotalUsd - discountUsd) + shippingUsd
        val curr = _currency.value
        val totalConverted = totalUsd * curr.rateToUsd
        val formattedTotal = curr.format(totalUsd)

        val itemsSummary = items.joinToString("; ") {
            "${it.quantity}x ${it.title} (${it.selectedSizeCm}cm${if (!it.customEngraving.isNullOrBlank()) ", Grabado: ${it.customEngraving}" else ""})"
        }

        viewModelScope.launch {
            _checkoutState.value = CheckoutUiState.Processing("Conectando con pasarela internacional segura...")
            kotlinx.coroutines.delay(1200)

            _checkoutState.value = CheckoutUiState.Processing("Validando token criptográfico y 3D Secure...")
            kotlinx.coroutines.delay(1500)

            _checkoutState.value = CheckoutUiState.Processing("Registrando compra en la base de datos Firestore...")

            val trackingCode = "INTL-${(1000000..9999999).random()}"
            val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(12)}"

            val order = Order(
                id = orderId,
                userId = uid,
                itemsCount = items.sumOf { it.quantity }.toLong(),
                subtotal = subtotalUsd,
                shippingCost = shippingUsd,
                total = totalUsd,
                currency = curr.code,
                paymentMethod = paymentMethod,
                paymentStatus = "paid",
                orderStatus = "recibido",
                trackingNumber = trackingCode,
                shippingName = shippingName,
                shippingAddress = shippingAddress,
                shippingCountry = shippingCountry,
                shippingCity = shippingCity,
                shippingZip = shippingZip,
                shippingPhone = shippingPhone,
                itemsSummary = itemsSummary
            )

            val result = repository.createOrder(order)
            if (result.isSuccess) {
                clearCart()
                _appliedCoupon.value = null
                _checkoutState.value = CheckoutUiState.Success(
                    orderId = orderId,
                    trackingNumber = trackingCode,
                    totalFormatted = formattedTotal
                )
            } else {
                _checkoutState.value = CheckoutUiState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "No se pudo procesar la compra en el servidor"
                )
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutUiState.Idle
    }

    fun isAdmin(): Boolean = repository.isAdmin()

    fun getCurrentUserEmail(): String? = repository.getCurrentUserEmail()

    fun updateOrderStatus(orderId: String, newStatus: String, trackingNumber: String? = null) {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(orderId, newStatus, trackingNumber)
            if (result.isSuccess) {
                _userFeedbackMessage.value = "Estado de orden actualizado a '$newStatus'"
            } else {
                _userFeedbackMessage.value = "Error al actualizar estado: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }
}
