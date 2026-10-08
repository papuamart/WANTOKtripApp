package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class AppScreen {
    data object Home : AppScreen()
    data object Flights : AppScreen()
    data object Hotels : AppScreen()
    data object Bookings : AppScreen()
    data object Profile : AppScreen()

    // Sub-screens
    data class FlightDetail(val flight: Flight) : AppScreen()
    data class HotelDetail(val hotel: Hotel) : AppScreen()
    data class Checkout(val flight: Flight? = null, val hotel: Hotel? = null, val nights: Int = 1) : AppScreen()
    data class BookingConfirmation(val booking: Booking) : AppScreen()
    data class BookingDetail(val booking: Booking) : AppScreen()
    data object Loyalty : AppScreen()
    data object BoardingPasses : AppScreen()
    data class PaymentProof(val initialRef: String? = null) : AppScreen()
    data object AdminProof : AppScreen()
}

@Composable
fun WantokTripApp() {
    var screenStack by remember { mutableStateOf(listOf<AppScreen>(AppScreen.Home)) }
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.Home

    val bookings by TravelRepository.bookings.collectAsState()
    val pendingCount = bookings.count { it.status == "pending" }

    fun navigateTo(screen: AppScreen) {
        screenStack = screenStack + screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun switchTab(screen: AppScreen) {
        screenStack = listOf(screen)
    }

    // Handle system back button
    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    val isTopLevel = currentScreen is AppScreen.Home ||
            currentScreen is AppScreen.Flights ||
            currentScreen is AppScreen.Hotels ||
            currentScreen is AppScreen.Bookings ||
            currentScreen is AppScreen.Profile

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(
                    containerColor = BrandNavyDark,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Home,
                        onClick = { switchTab(AppScreen.Home) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                        label = { Text("Explore", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandNavyDark,
                            selectedTextColor = BrandGold,
                            indicatorColor = BrandGold,
                            unselectedIconColor = TextSubtle,
                            unselectedTextColor = TextSubtle
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Flights,
                        onClick = { switchTab(AppScreen.Flights) },
                        icon = { Icon(Icons.Default.Flight, contentDescription = "Flights") },
                        label = { Text("Flights", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandNavyDark,
                            selectedTextColor = BrandGold,
                            indicatorColor = BrandGold,
                            unselectedIconColor = TextSubtle,
                            unselectedTextColor = TextSubtle
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Hotels,
                        onClick = { switchTab(AppScreen.Hotels) },
                        icon = { Icon(Icons.Default.Hotel, contentDescription = "Hotels") },
                        label = { Text("Hotels", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandNavyDark,
                            selectedTextColor = BrandGold,
                            indicatorColor = BrandGold,
                            unselectedIconColor = TextSubtle,
                            unselectedTextColor = TextSubtle
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Bookings,
                        onClick = { switchTab(AppScreen.Bookings) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingCount > 0) {
                                        Badge(containerColor = StatusWarning) {
                                            Text("$pendingCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = "Bookings")
                            }
                        },
                        label = { Text("Bookings", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandNavyDark,
                            selectedTextColor = BrandGold,
                            indicatorColor = BrandGold,
                            unselectedIconColor = TextSubtle,
                            unselectedTextColor = TextSubtle
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Profile,
                        onClick = { switchTab(AppScreen.Profile) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandNavyDark,
                            selectedTextColor = BrandGold,
                            indicatorColor = BrandGold,
                            unselectedIconColor = TextSubtle,
                            unselectedTextColor = TextSubtle
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Home -> {
                    HomeScreen(
                        onNavigateToFlights = { switchTab(AppScreen.Flights) },
                        onNavigateToHotels = { switchTab(AppScreen.Hotels) },
                        onNavigateToBookings = { switchTab(AppScreen.Bookings) },
                        onNavigateToLoyalty = { navigateTo(AppScreen.Loyalty) },
                        onNavigateToBoardingPasses = { navigateTo(AppScreen.BoardingPasses) },
                        onNavigateToPaymentProof = { navigateTo(AppScreen.PaymentProof()) },
                        onSelectFlight = { navigateTo(AppScreen.FlightDetail(it)) },
                        onSelectHotel = { navigateTo(AppScreen.HotelDetail(it)) }
                    )
                }

                is AppScreen.Flights -> {
                    FlightsScreen(
                        onSelectFlight = { navigateTo(AppScreen.FlightDetail(it)) }
                    )
                }

                is AppScreen.Hotels -> {
                    HotelsScreen(
                        onSelectHotel = { navigateTo(AppScreen.HotelDetail(it)) }
                    )
                }

                is AppScreen.Bookings -> {
                    BookingsScreen(
                        onSelectBooking = { navigateTo(AppScreen.BookingDetail(it)) },
                        onNavigateToExplore = { switchTab(AppScreen.Home) }
                    )
                }

                is AppScreen.Profile -> {
                    ProfileScreen(
                        onNavigateToBookings = { switchTab(AppScreen.Bookings) },
                        onNavigateToBoardingPasses = { navigateTo(AppScreen.BoardingPasses) },
                        onNavigateToLoyalty = { navigateTo(AppScreen.Loyalty) },
                        onNavigateToPaymentProof = { navigateTo(AppScreen.PaymentProof()) },
                        onNavigateToAdmin = { navigateTo(AppScreen.AdminProof) }
                    )
                }

                is AppScreen.FlightDetail -> {
                    FlightDetailScreen(
                        flight = screen.flight,
                        onNavigateBack = { navigateBack() },
                        onBookFlight = { navigateTo(AppScreen.Checkout(flight = it)) }
                    )
                }

                is AppScreen.HotelDetail -> {
                    HotelDetailScreen(
                        hotel = screen.hotel,
                        onNavigateBack = { navigateBack() },
                        onBookHotel = { h, n -> navigateTo(AppScreen.Checkout(hotel = h, nights = n)) }
                    )
                }

                is AppScreen.Checkout -> {
                    CheckoutScreen(
                        flight = screen.flight,
                        hotel = screen.hotel,
                        nights = screen.nights,
                        onNavigateBack = { navigateBack() },
                        onBookingConfirmed = { booking ->
                            screenStack = listOf(AppScreen.Home, AppScreen.BookingConfirmation(booking))
                        }
                    )
                }

                is AppScreen.BookingConfirmation -> {
                    BookingConfirmationScreen(
                        booking = screen.booking,
                        onViewBookings = { switchTab(AppScreen.Bookings) },
                        onSubmitProof = { ref -> navigateTo(AppScreen.PaymentProof(initialRef = ref)) },
                        onViewBoardingPasses = { navigateTo(AppScreen.BoardingPasses) },
                        onGoHome = { switchTab(AppScreen.Home) }
                    )
                }

                is AppScreen.BookingDetail -> {
                    BookingDetailScreen(
                        booking = screen.booking,
                        onNavigateBack = { navigateBack() },
                        onSubmitProof = { ref -> navigateTo(AppScreen.PaymentProof(initialRef = ref)) },
                        onViewBoardingPass = { navigateTo(AppScreen.BoardingPasses) }
                    )
                }

                is AppScreen.Loyalty -> {
                    LoyaltyScreen(
                        onNavigateBack = { navigateBack() }
                    )
                }

                is AppScreen.BoardingPasses -> {
                    BoardingPassesScreen(
                        onNavigateBack = { navigateBack() }
                    )
                }

                is AppScreen.PaymentProof -> {
                    PaymentProofScreen(
                        initialBookingRef = screen.initialRef,
                        onNavigateBack = { navigateBack() }
                    )
                }

                is AppScreen.AdminProof -> {
                    AdminProofScreen(
                        onNavigateBack = { navigateBack() }
                    )
                }
            }
        }
    }
}
