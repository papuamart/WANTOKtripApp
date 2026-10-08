package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Booking
import com.example.data.TravelRepository
import com.example.ui.components.StatusBadge
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun BookingsScreen(
    onSelectBooking: (Booking) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val bookings by TravelRepository.bookings.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") } // "All", "confirmed", "pending", "cancelled"

    val filtered = remember(bookings, selectedFilter) {
        if (selectedFilter == "All") bookings
        else bookings.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    val pendingCount = bookings.count { it.status == "pending" }

    Scaffold(
        topBar = {
            WantokTopBar(title = "My Bookings")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text("All (${bookings.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandNavy,
                            selectedLabelColor = TextInverse
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "confirmed",
                        onClick = { selectedFilter = "confirmed" },
                        label = { Text("Confirmed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusSuccess,
                            selectedLabelColor = TextInverse
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "pending",
                        onClick = { selectedFilter = "pending" },
                        label = { Text("Pending ($pendingCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusWarning,
                            selectedLabelColor = TextInverse
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "cancelled",
                        onClick = { selectedFilter = "cancelled" },
                        label = { Text("Cancelled") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusError,
                            selectedLabelColor = TextInverse
                        )
                    )
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = TextSubtle,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No $selectedFilter bookings found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your travel itinerary will show here after booking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onNavigateToExplore,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Explore Flights & Hotels", color = BrandNavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(filtered) { booking ->
                    BookingItemCard(booking = booking) {
                        onSelectBooking(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Reference & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (booking.type == "flight") Icons.Default.Flight else Icons.Default.Hotel,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.bookingRef,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body
            if (booking.type == "flight" && booking.flight != null) {
                Text(
                    text = "${booking.flight.airline} • ${booking.flight.flightNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${booking.flight.from} (${booking.flight.fromCode}) → ${booking.flight.to} (${booking.flight.toCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else if (booking.hotel != null) {
                Text(
                    text = booking.hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${booking.hotel.location}, ${booking.hotel.city}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "Reservation for ${booking.passengerName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Date & Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booked on ${booking.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSubtle
                )
                Text(
                    text = "K${booking.totalAmount} PGK",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }
        }
    }
}
