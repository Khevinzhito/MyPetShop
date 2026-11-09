package com.example.mypetshop.ui.driver

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.ui.theme.DriverAccent
import com.example.mypetshop.ui.theme.DriverBackground
import com.example.mypetshop.ui.theme.DriverMuted
import com.example.mypetshop.ui.theme.DriverOnSurface
import com.example.mypetshop.ui.theme.DriverSurface
import com.example.mypetshop.util.LocationPermission

enum class DriverTab {
    DELIVERIES,
    ROUTE,
    PROFILE,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverShellScreen(
    driver: DriverProfile,
    session: NavigationSession,
    showBackToStore: Boolean = true,
    onExitToBuyer: () -> Unit,
    onLogout: () -> Unit,
    routeViewModel: DriverRouteViewModel = hiltViewModel(),
) {
    var currentDriver by remember(driver) { mutableStateOf(driver) }
    var selectedTab by remember { mutableStateOf(DriverTab.DELIVERIES) }
    val routeUiState by routeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            routeViewModel.refreshLocation { address, lat, lng ->
                currentDriver = currentDriver.copy(assignedLocation = address, latitude = lat, longitude = lng)
            }
        } else {
            routeViewModel.onLocationPermissionDenied()
        }
    }

    fun requestLocationRefresh() {
        if (LocationPermission.hasFineOrCoarse(context)) {
            routeViewModel.refreshLocation { address, lat, lng ->
                currentDriver = currentDriver.copy(assignedLocation = address, latitude = lat, longitude = lng)
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    Scaffold(
        containerColor = DriverBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.driver_mode_title), color = DriverOnSurface) },
                navigationIcon = {
                    if (showBackToStore) {
                        IconButton(onClick = onExitToBuyer) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_to_store),
                                tint = DriverOnSurface,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DriverSurface,
                    titleContentColor = DriverOnSurface,
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DriverSurface) {
                NavigationBarItem(
                    selected = selectedTab == DriverTab.DELIVERIES,
                    onClick = { selectedTab = DriverTab.DELIVERIES },
                    icon = { Icon(Icons.Default.LocalShipping, null) },
                    label = { Text(stringResource(R.string.driver_tab_deliveries)) },
                    colors = driverNavColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == DriverTab.ROUTE,
                    onClick = { selectedTab = DriverTab.ROUTE },
                    icon = { Icon(Icons.Default.Route, null) },
                    label = { Text(stringResource(R.string.driver_tab_route)) },
                    colors = driverNavColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == DriverTab.PROFILE,
                    onClick = { selectedTab = DriverTab.PROFILE },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text(stringResource(R.string.driver_tab_profile)) },
                    colors = driverNavColors(),
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DriverBackground),
        ) {
            when (selectedTab) {
                DriverTab.DELIVERIES -> DriverDashboardScreen(
                    driverName = currentDriver.fullName,
                    driverLocation = currentDriver.assignedLocation,
                    embedInScaffold = false,
                    useDriverTheme = true,
                )
                DriverTab.ROUTE -> DriverToolsPanel(
                    driverLocation = currentDriver.assignedLocation,
                    showRefreshLocation = true,
                    isLoadingLocation = routeUiState.isLoadingLocation,
                    locationErrorKey = routeUiState.locationErrorKey,
                    onRefreshLocation = ::requestLocationRefresh,
                )
                DriverTab.PROFILE -> DriverProfilePanel(
                    driver = currentDriver,
                    onBackToStore = if (showBackToStore) onExitToBuyer else null,
                    onLogout = onLogout,
                )
            }
        }
    }
}

@Composable
private fun driverNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = DriverAccent,
    selectedTextColor = DriverAccent,
    unselectedIconColor = DriverMuted,
    unselectedTextColor = DriverMuted,
    indicatorColor = DriverBackground,
)
