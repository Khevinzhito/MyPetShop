package com.example.mypetshop.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.ui.about.AboutUsScreen
import com.example.mypetshop.ui.advance.AdvanceMenuScreen
import com.example.mypetshop.ui.components.AppLogo
import com.example.mypetshop.ui.components.PetCompanionMini
import com.example.mypetshop.ui.driver.DriverRegisterScreen
import com.example.mypetshop.ui.driver.DriverRequirementsScreen
import com.example.mypetshop.ui.driver.DriverShellScreen
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.ui.onboarding.PetOnboardingScreen
import com.example.mypetshop.ui.shop.BuyerMessagesScreen
import com.example.mypetshop.ui.shop.CartScreen
import com.example.mypetshop.ui.shop.MarketplaceScreen
import com.example.mypetshop.ui.shop.OrdersScreen
import com.example.mypetshop.ui.shop.ShopViewModel
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class MainSection {
    MARKETPLACE,
    CART,
    ORDERS,
    MESSAGES,
    ADVANCE,
    ABOUT,
    PET_ONBOARDING,
    DRIVER_REQUIREMENTS,
    DRIVER_REGISTER,
    DRIVER_SHELL,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    profile: PetProfile?,
    session: NavigationSession,
    onPetProfileSaved: (PetProfile) -> Unit,
    onLogout: () -> Unit,
    shopViewModel: ShopViewModel = hiltViewModel(),
) {
    val activeProfile = session.currentProfile ?: profile
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentSection by remember { mutableStateOf(MainSection.MARKETPLACE) }
    var showDriverNoPetDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val cartItems by shopViewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    if (currentSection == MainSection.DRIVER_SHELL && session.currentDriver != null) {
        DriverShellScreen(
            driver = session.currentDriver!!,
            session = session,
            onExitToBuyer = { currentSection = MainSection.MARKETPLACE },
            onLogout = onLogout,
        )
        return
    }

    if (showDriverNoPetDialog) {
        AlertDialog(
            onDismissRequest = { showDriverNoPetDialog = false },
            title = { Text(stringResource(R.string.driver_no_pet_dialog_title)) },
            text = { Text(stringResource(R.string.driver_no_pet_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDriverNoPetDialog = false
                        currentSection = MainSection.DRIVER_SHELL
                    },
                ) {
                    Text(stringResource(R.string.driver_continue_without_pet))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDriverNoPetDialog = false
                        currentSection = MainSection.PET_ONBOARDING
                    },
                ) {
                    Text(stringResource(R.string.register_pet_now))
                }
            },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm_title)) },
            text = { Text(stringResource(R.string.logout_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(stringResource(R.string.logout), color = OrangePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    val sectionTitle = when (currentSection) {
        MainSection.MARKETPLACE -> stringResource(R.string.marketplace_title)
        MainSection.CART -> stringResource(R.string.cart_title)
        MainSection.ORDERS -> stringResource(R.string.orders_title)
        MainSection.MESSAGES -> stringResource(R.string.buyer_messages_title)
        MainSection.ADVANCE -> stringResource(R.string.advance_menu_title)
        MainSection.ABOUT -> stringResource(R.string.about_us_screen_title)
        MainSection.PET_ONBOARDING -> stringResource(R.string.onboarding_title)
        MainSection.DRIVER_REQUIREMENTS -> stringResource(R.string.driver_requirements_title)
        MainSection.DRIVER_REGISTER -> stringResource(R.string.driver_register_title)
        MainSection.DRIVER_SHELL -> stringResource(R.string.driver_dashboard_title)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppLogo(size = 72.dp, modifier = Modifier.padding(24.dp), applyTint = false)
                session.currentUser?.let { user ->
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = OrangeDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (activeProfile != null) {
                    Text(
                        text = activeProfile.breed,
                        style = MaterialTheme.typography.titleMedium,
                        color = OrangeDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Text(
                        text = stringResource(R.string.drawer_pet_age, activeProfile.ageYears),
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeDark,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    PetCompanionMini(
                        profile = activeProfile,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                } else {
                    Text(
                        text = stringResource(R.string.drawer_no_pet),
                        style = MaterialTheme.typography.titleMedium,
                        color = OrangeDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Text(
                        text = stringResource(R.string.drawer_no_pet_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    )
                }
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Storefront, null) },
                    label = { Text(stringResource(R.string.drawer_marketplace)) },
                    selected = currentSection == MainSection.MARKETPLACE,
                    onClick = {
                        currentSection = MainSection.MARKETPLACE
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text(stringResource(R.string.drawer_cart)) },
                    selected = currentSection == MainSection.CART,
                    onClick = {
                        currentSection = MainSection.CART
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Receipt, null) },
                    label = { Text(stringResource(R.string.drawer_orders)) },
                    selected = currentSection == MainSection.ORDERS,
                    onClick = {
                        currentSection = MainSection.ORDERS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Message, null) },
                    label = { Text(stringResource(R.string.drawer_messages)) },
                    selected = currentSection == MainSection.MESSAGES,
                    onClick = {
                        currentSection = MainSection.MESSAGES
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                if (activeProfile != null) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Pets, null) },
                        label = { Text(stringResource(R.string.drawer_advance)) },
                        selected = currentSection == MainSection.ADVANCE,
                        onClick = {
                            currentSection = MainSection.ADVANCE
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                } else {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Pets, null) },
                        label = { Text(stringResource(R.string.drawer_register_pet)) },
                        selected = currentSection == MainSection.PET_ONBOARDING,
                        onClick = {
                            currentSection = MainSection.PET_ONBOARDING
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DeliveryDining, null) },
                    label = {
                        Text(
                            if (session.currentDriver != null) stringResource(R.string.drawer_driver_panel)
                            else stringResource(R.string.drawer_associate_driver),
                        )
                    },
                    selected = currentSection == MainSection.DRIVER_SHELL ||
                        currentSection == MainSection.DRIVER_REQUIREMENTS ||
                        currentSection == MainSection.DRIVER_REGISTER,
                    onClick = {
                        currentSection = if (session.currentDriver != null) {
                            MainSection.DRIVER_SHELL
                        } else {
                            MainSection.DRIVER_REQUIREMENTS
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                if (session.currentDriver != null) {
                    Text(
                        text = stringResource(R.string.drawer_driver_registered_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp),
                    )
                }
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text(stringResource(R.string.about_us_button)) },
                    selected = currentSection == MainSection.ABOUT,
                    onClick = {
                        currentSection = MainSection.ABOUT
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, null) },
                    label = { Text(stringResource(R.string.logout)) },
                    selected = false,
                    onClick = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        },
    ) {
        Scaffold(
            containerColor = BackgroundWhite,
            topBar = {
                TopAppBar(
                    title = { Text(sectionTitle, color = BackgroundWhite) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = BackgroundWhite)
                        }
                    },
                    actions = {
                        if (currentSection != MainSection.CART && currentSection != MainSection.DRIVER_REQUIREMENTS &&
                            currentSection != MainSection.DRIVER_REGISTER && currentSection != MainSection.PET_ONBOARDING &&
                            currentSection != MainSection.MESSAGES
                        ) {
                            IconButton(onClick = { currentSection = MainSection.CART }) {
                                BadgedBox(
                                    badge = {
                                        if (cartCount > 0) {
                                            Badge { Text(cartCount.toString()) }
                                        }
                                    },
                                ) {
                                    Icon(Icons.Default.ShoppingCart, null, tint = BackgroundWhite)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = OrangePrimary,
                        titleContentColor = BackgroundWhite,
                    ),
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when (currentSection) {
                    MainSection.MARKETPLACE -> MarketplaceScreen(
                        profile = activeProfile,
                        onAdvanceClick = { currentSection = MainSection.ADVANCE },
                    )
                    MainSection.CART -> CartScreen(
                        onOrderPlaced = { currentSection = MainSection.ORDERS },
                    )
                    MainSection.ORDERS -> OrdersScreen()
                    MainSection.MESSAGES -> BuyerMessagesScreen()
                    MainSection.ADVANCE -> if (activeProfile != null) {
                        AdvanceMenuScreen(
                            profile = activeProfile,
                            onAboutUsClick = { currentSection = MainSection.ABOUT },
                            embedInScaffold = false,
                        )
                    } else {
                        NoPetPlaceholder { currentSection = MainSection.PET_ONBOARDING }
                    }
                    MainSection.ABOUT -> AboutUsScreen(
                        onBack = { currentSection = MainSection.MARKETPLACE },
                        embedInScaffold = false,
                    )
                    MainSection.PET_ONBOARDING -> PetOnboardingScreen(
                        onProfileSaved = { savedProfile ->
                            onPetProfileSaved(savedProfile)
                            currentSection = MainSection.MARKETPLACE
                        },
                        onSkip = { currentSection = MainSection.MARKETPLACE },
                        embedInScaffold = false,
                    )
                    MainSection.DRIVER_REQUIREMENTS -> DriverRequirementsScreen(
                        onStartRegistration = { currentSection = MainSection.DRIVER_REGISTER },
                    )
                    MainSection.DRIVER_REGISTER -> DriverRegisterScreen(
                        onRegistrationComplete = { driverProfile ->
                            session.currentDriver = driverProfile
                            if (activeProfile == null && session.currentProfile == null) {
                                showDriverNoPetDialog = true
                            } else {
                                currentSection = MainSection.DRIVER_SHELL
                            }
                        },
                        onBack = { currentSection = MainSection.DRIVER_REQUIREMENTS },
                        embedInScaffold = false,
                        useDriverTheme = true,
                    )
                    MainSection.DRIVER_SHELL -> Unit
                }
            }
        }
    }
}

@Composable
private fun NoPetPlaceholder(onRegisterPet: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.no_pet_advance_message),
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 48.dp),
        )
        TextButton(onClick = onRegisterPet) {
            Text(stringResource(R.string.register_pet_now), color = OrangePrimary)
        }
    }
}
