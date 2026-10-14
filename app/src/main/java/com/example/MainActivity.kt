package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BraceletProduct
import com.example.data.model.Currency
import com.example.ui.components.AuthScreen
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.ProductDetailSheet
import com.example.ui.components.attemptAutoSignIn
import com.example.ui.components.signOutUser
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.theme.PulserasArtesanalesTheme
import com.example.ui.viewmodel.ShopViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulserasArtesanalesTheme {
                AppRootContent()
            }
        }
    }
}

@Composable
fun AppRootContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isCheckingAuth by remember { mutableStateOf(currentUser == null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                    isCheckingAuth = false
                },
                onUnauthenticated = {
                    isCheckingAuth = false
                },
                scope = scope
            )
        } else {
            isCheckingAuth = false
        }
    }

    if (isCheckingAuth) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    } else if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        MainShopContent(
            onSignOut = {
                signOutUser(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        currentUser = null
                    },
                    scope = scope
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShopContent(
    onSignOut: () -> Unit,
    viewModel: ShopViewModel = viewModel()
) {
    var currentTab by remember { mutableIntStateOf(0) }
    var selectedProductForDetail by remember { mutableStateOf<BraceletProduct?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val customizerState by viewModel.customizerState.collectAsStateWithLifecycle()
    val orders by viewModel.ordersState.collectAsStateWithLifecycle()
    val savedDesigns by viewModel.savedDesignsState.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val shippingMethod by viewModel.shippingMethod.collectAsStateWithLifecycle()
    val checkoutState by viewModel.checkoutState.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    // Feedback message handler
    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearFeedbackMessage()
        }
    }

    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pulseras Artesanales",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Joyería hecha a mano",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Currency Switcher Dropdown
                    Box {
                        Surface(
                            onClick = { currencyMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "${currency.symbol} ${currency.code}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false }
                        ) {
                            Currency.values().forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text("${curr.symbol} ${curr.code} - ${curr.name}") },
                                    onClick = {
                                        viewModel.setCurrency(curr)
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Sign Out Button
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Catálogo") },
                    label = { Text("Catálogo") },
                    modifier = Modifier.testTag("tab_catalog"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.Handyman, contentDescription = "Taller") },
                    label = { Text("Taller") },
                    modifier = Modifier.testTag("tab_customizer"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        val cartCount = cartItems.sumOf { it.quantity }
                        if (cartCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("$cartCount")
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Carrito")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    },
                    label = { Text("Carrito") },
                    modifier = Modifier.testTag("tab_cart"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        val ordersCount = orders.size
                        if (ordersCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                        Text("$ordersCount")
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Mis Pedidos")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Mis Pedidos")
                        }
                    },
                    label = { Text("Mis Pedidos") },
                    modifier = Modifier.testTag("tab_orders"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> CatalogScreen(
                    currency = currency,
                    onSelectProduct = { selectedProductForDetail = it },
                    onQuickAddToCart = { product ->
                        viewModel.addToCart(
                            productId = product.id,
                            title = product.name,
                            description = product.materials,
                            priceUsd = product.basePriceUsd,
                            quantity = 1,
                            selectedColor = product.availableColors.firstOrNull() ?: "Original",
                            selectedSizeCm = product.availableSizesCm.firstOrNull() ?: 18.0,
                            imageResId = product.drawableResId
                        )
                    },
                    onGoToCustomizer = { currentTab = 1 }
                )
                1 -> CustomizerScreen(
                    state = customizerState,
                    currency = currency,
                    savedDesigns = savedDesigns,
                    onUpdateState = { viewModel.updateCustomizer(it) },
                    onSaveToCloud = { viewModel.saveCustomDesignToCloud() },
                    onAddToCart = {
                        viewModel.addCustomizerToCart()
                        currentTab = 2 // Navigate to cart
                    },
                    onLoadSavedDesign = { viewModel.loadCustomDesignIntoCustomizer(it) },
                    onDeleteSavedDesign = { viewModel.deleteCustomDesign(it) }
                )
                2 -> CartScreen(
                    cartItems = cartItems,
                    currency = currency,
                    appliedCoupon = appliedCoupon,
                    shippingMethod = shippingMethod,
                    onUpdateQuantity = { id, q -> viewModel.updateCartQuantity(id, q) },
                    onRemoveItem = { viewModel.removeCartItem(it) },
                    onCurrencyChanged = { viewModel.setCurrency(it) },
                    onApplyCoupon = { viewModel.applyCoupon(it) },
                    onRemoveCoupon = { viewModel.removeCoupon() },
                    onSelectShipping = { viewModel.setShippingMethod(it) },
                    onProceedToCheckout = { showCheckoutDialog = true },
                    onGoToCatalog = { currentTab = 0 }
                )
                3 -> OrdersScreen(
                    orders = orders,
                    onExploreCatalog = { currentTab = 0 }
                )
            }
        }

        // Product Detail Bottom Sheet
        if (selectedProductForDetail != null) {
            ProductDetailSheet(
                product = selectedProductForDetail!!,
                currency = currency,
                onDismiss = { selectedProductForDetail = null },
                onAddToCart = { product, quantity, color, size, engraving ->
                    viewModel.addToCart(
                        productId = product.id,
                        title = product.name,
                        description = product.materials,
                        priceUsd = product.basePriceUsd,
                        quantity = quantity,
                        selectedColor = color,
                        selectedSizeCm = size,
                        customEngraving = engraving,
                        imageResId = product.drawableResId
                    )
                    selectedProductForDetail = null
                }
            )
        }

        // Checkout & Payment Dialog
        if (showCheckoutDialog) {
            val subtotal = cartItems.sumOf { it.totalUsd }
            val discountRate = when (appliedCoupon) {
                "ARTESANAL10" -> 0.10
                "BIENVENIDO15" -> 0.15
                else -> 0.0
            }
            val total = (subtotal - (subtotal * discountRate)) + shippingMethod.costUsd

            CheckoutDialog(
                totalUsd = total,
                currency = currency,
                shippingMethod = shippingMethod,
                checkoutState = checkoutState,
                onDismiss = {
                    showCheckoutDialog = false
                    viewModel.resetCheckoutState()
                },
                onSubmitOrder = { name, address, country, city, zip, phone, paymentMethod ->
                    viewModel.processOrder(name, address, country, city, zip, phone, paymentMethod)
                },
                onViewOrders = {
                    showCheckoutDialog = false
                    viewModel.resetCheckoutState()
                    currentTab = 3
                }
            )
        }
    }
}
