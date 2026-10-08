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
import com.example.data.Flight
import com.example.data.MockDataProvider
import com.example.ui.components.FlightCard
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun FlightsScreen(
    onSelectFlight: (Flight) -> Unit
) {
    var isRoundTrip by remember { mutableStateOf(false) }
    var selectedClass by remember { mutableStateOf("Economy") }
    var selectedOrigin by remember { mutableStateOf("POM") }
    var selectedDest by remember { mutableStateOf("All") }
    var filterNonStopOnly by remember { mutableStateOf(false) }

    val destinations = listOf("All", "SYD", "BNE", "SIN", "LAE", "HIR", "VLI", "NAN", "GKA", "HGU", "RAB")

    val filteredFlights = remember(selectedDest, filterNonStopOnly, selectedClass) {
        MockDataProvider.flights.filter { flight ->
            (selectedDest == "All" || flight.toCode == selectedDest) &&
            (!filterNonStopOnly || flight.stops == 0) &&
            (selectedClass == "Economy" || flight.travelClass == selectedClass)
        }
    }

    Scaffold(
        topBar = {
            WantokTopBar(title = "Search Flights")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Flight Search Form
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
                        // Trip Type Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !isRoundTrip,
                                onClick = { isRoundTrip = false },
                                label = { Text("One Way") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandNavy,
                                    selectedLabelColor = TextInverse
                                )
                            )
                            FilterChip(
                                selected = isRoundTrip,
                                onClick = { isRoundTrip = true },
                                label = { Text("Round Trip") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandNavy,
                                    selectedLabelColor = TextInverse
                                )
                            )
                            FilterChip(
                                selected = selectedClass == "Business",
                                onClick = {
                                    selectedClass = if (selectedClass == "Business") "Economy" else "Business"
                                },
                                label = { Text("Business") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandGold,
                                    selectedLabelColor = BrandNavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Origin & Destination Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceAlt,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "From",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSubtle
                                    )
                                    Text(
                                        text = "Port Moresby (POM)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Swap",
                                    tint = BrandGoldDark
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "To",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSubtle
                                    )
                                    Text(
                                        text = if (selectedDest == "All") "Any Destination" else selectedDest,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Destination Chips
                        Text(
                            text = "Filter by Destination:",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(destinations) { dest ->
                                FilterChip(
                                    selected = selectedDest == dest,
                                    onClick = { selectedDest = dest },
                                    label = { Text(dest) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandGold,
                                        selectedLabelColor = BrandNavyDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { filterNonStopOnly = !filterNonStopOnly }
                        ) {
                            Checkbox(
                                checked = filterNonStopOnly,
                                onCheckedChange = { filterNonStopOnly = it },
                                colors = CheckboxDefaults.colors(checkedColor = BrandNavy)
                            )
                            Text(
                                text = "Non-stop flights only",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Results Count Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Flights (${filteredFlights.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Fare in PGK",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSubtle
                    )
                }
            }

            // Results List
            if (filteredFlights.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = TextSubtle,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No flights match your filter",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Try switching destination or trip class",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSubtle
                        )
                    }
                }
            } else {
                items(filteredFlights) { flight ->
                    FlightCard(flight = flight) {
                        onSelectFlight(flight)
                    }
                }
            }
        }
    }
}
