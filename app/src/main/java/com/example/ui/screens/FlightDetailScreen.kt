package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Flight
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun FlightDetailScreen(
    flight: Flight,
    onNavigateBack: () -> Unit,
    onBookFlight: (Flight) -> Unit
) {
    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Flight Details",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBg,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Fare",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "K${flight.price} PGK",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BrandNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onBookFlight(flight) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Proceed to Booking",
                            color = BrandNavyDark,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Airline banner card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = flight.airline,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Flight ${flight.flightNumber} • ${flight.travelClass}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            color = if (flight.refundable) StatusSuccess.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (flight.refundable) "Refundable" else "Non-refundable",
                                color = if (flight.refundable) StatusSuccess else StatusWarning,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Route Timeline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = flight.departureTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${flight.fromCode} - ${flight.from}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Gate 04 • Terminal 1",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSubtle
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = flight.duration,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSubtle
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                HorizontalDivider(modifier = Modifier.width(24.dp), color = BorderLight)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = BrandGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                HorizontalDivider(modifier = Modifier.width(24.dp), color = BorderLight)
                            }
                            Text(
                                text = if (flight.stops == 0) "Direct Non-Stop" else "${flight.stops} Stop",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (flight.stops == 0) StatusSuccess else BrandGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = flight.arrivalTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${flight.toCode} - ${flight.to}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Arrival Terminal",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSubtle
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Baggage & Amenities Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Included In Your Fare",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AmenityRow(icon = Icons.Default.Luggage, title = "Checked Baggage", detail = "1 piece up to 23 kg")
                    AmenityRow(icon = Icons.Default.Work, title = "Cabin Baggage", detail = "1 carry-on up to 7 kg")
                    AmenityRow(icon = Icons.Default.Restaurant, title = "In-Flight Meal & Drinks", detail = "Complimentary Melanesian snacks & drinks")
                    AmenityRow(icon = Icons.Default.AirlineSeatReclineExtra, title = "Seat Selection", detail = "Standard seat included")
                    AmenityRow(icon = Icons.Default.Stars, title = "Loyalty Points", detail = "Earn ${(flight.price / 10).coerceAtLeast(20)} Wantok Points")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fare Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Price Breakdown (PGK)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FareRow(label = "Base Airfare", amount = "K${(flight.price * 0.82).toInt()}")
                    FareRow(label = "Aviation Security & Taxes", amount = "K${(flight.price * 0.12).toInt()}")
                    FareRow(label = "Passenger Service Charge", amount = "K${(flight.price * 0.06).toInt()}")

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = BorderLight
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Amount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "K${flight.price} PGK",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandNavy,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun FareRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
