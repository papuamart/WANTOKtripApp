package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.components.DealCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToFlights: () -> Unit,
    onNavigateToHotels: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToLoyalty: () -> Unit,
    onNavigateToBoardingPasses: () -> Unit,
    onNavigateToPaymentProof: () -> Unit,
    onSelectFlight: (Flight) -> Unit,
    onSelectHotel: (Hotel) -> Unit
) {
    val loyaltyPoints by TravelRepository.loyaltyPoints.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BrandNavyDark, BrandNavy)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WANTOKTrip",
                                style = MaterialTheme.typography.headlineLarge,
                                color = BrandGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Explore Papua New Guinea & Melanesia",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextInverse.copy(alpha = 0.85f)
                            )
                        }

                        // Loyalty Points Pill
                        Surface(
                            color = BrandNavyLight,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable { onNavigateToLoyalty() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = BrandGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$loyaltyPoints pts",
                                    color = TextInverse,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Search input banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToFlights() },
                        color = CardBg,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BrandNavy
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Where do you want to fly or stay?",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid / Row
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Travel Services",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Flight,
                            label = "Flights",
                            bgColor = BrandNavy.copy(alpha = 0.1f),
                            tint = BrandNavy,
                            onClick = onNavigateToFlights
                        )
                        QuickActionButton(
                            icon = Icons.Default.Hotel,
                            label = "Hotels",
                            bgColor = BrandGold.copy(alpha = 0.15f),
                            tint = BrandGoldDark,
                            onClick = onNavigateToHotels
                        )
                        QuickActionButton(
                            icon = Icons.Default.ConfirmationNumber,
                            label = "Bookings",
                            bgColor = StatusSuccess.copy(alpha = 0.12f),
                            tint = StatusSuccess,
                            onClick = onNavigateToBookings
                        )
                        QuickActionButton(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Boarding",
                            bgColor = BrandNavyLight.copy(alpha = 0.12f),
                            tint = BrandNavyLight,
                            onClick = onNavigateToBoardingPasses
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Stars,
                            label = "Loyalty",
                            bgColor = BrandGold.copy(alpha = 0.15f),
                            tint = BrandGoldDark,
                            onClick = onNavigateToLoyalty
                        )
                        QuickActionButton(
                            icon = Icons.Default.ReceiptLong,
                            label = "Payment Proof",
                            bgColor = StatusWarning.copy(alpha = 0.15f),
                            tint = StatusWarning,
                            onClick = onNavigateToPaymentProof
                        )
                    }
                }
            }
        }

        // Special Deals Section
        item {
            SectionHeader(
                title = "Special Offers & Flash Sales",
                actionText = "View All",
                onActionClick = onNavigateToFlights
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(MockDataProvider.deals) { deal ->
                    DealCard(deal = deal) {
                        if (deal.type == "flight") {
                            val flight = MockDataProvider.flights.firstOrNull { it.id == "f1" }
                                ?: MockDataProvider.flights[0]
                            onSelectFlight(flight)
                        } else {
                            val hotel = MockDataProvider.hotels.firstOrNull { it.id == "h5" }
                                ?: MockDataProvider.hotels[0]
                            onSelectHotel(hotel)
                        }
                    }
                }
            }
        }

        // Popular Destinations
        item {
            SectionHeader(title = "Popular Melanesian Routes")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(MockDataProvider.destinations) { dest ->
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(end = 12.dp)
                            .clickable { onNavigateToFlights() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Column {
                            Box(modifier = Modifier.height(95.dp).fillMaxWidth()) {
                                AsyncImage(
                                    model = dest.image,
                                    contentDescription = dest.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    color = BrandNavyDark.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "${dest.flag} ${dest.code}",
                                        color = TextInverse,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = dest.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "From K${dest.fromPrice}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandGoldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Melanesia Resorts & Hotels
        item {
            SectionHeader(
                title = "Hotels Across Melanesia",
                actionText = "Explore",
                onActionClick = onNavigateToHotels
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(MockDataProvider.melanesianHotels) { mh ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .padding(end = 12.dp)
                            .clickable {
                                val hotel = MockDataProvider.hotels.find { it.id == mh.hotelId }
                                    ?: MockDataProvider.hotels[0]
                                onSelectHotel(hotel)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Column {
                            Box(modifier = Modifier.height(105.dp).fillMaxWidth()) {
                                AsyncImage(
                                    model = mh.image,
                                    contentDescription = mh.city,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    color = BrandNavyDark.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "${mh.flag} ${mh.city}",
                                        color = TextInverse,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = mh.country,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "From K${mh.fromPrice} / night",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
