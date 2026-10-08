package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Hotel
import com.example.data.MockDataProvider
import com.example.ui.components.HotelCard
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun HotelsScreen(
    onSelectHotel: (Hotel) -> Unit
) {
    var selectedCity by remember { mutableStateOf("All") }
    var minStars by remember { mutableStateOf(0) }
    var filterWithPool by remember { mutableStateOf(false) }

    val cities = listOf("All", "Port Moresby", "Lae", "Kokopo", "Honiara", "Port Vila", "Nadi", "Noumea")

    val filteredHotels = remember(selectedCity, minStars, filterWithPool) {
        MockDataProvider.hotels.filter { hotel ->
            (selectedCity == "All" || hotel.city.equals(selectedCity, ignoreCase = true)) &&
            (hotel.stars >= minStars) &&
            (!filterWithPool || hotel.amenities.any { it.contains("Pool", ignoreCase = true) })
        }
    }

    Scaffold(
        topBar = {
            WantokTopBar(title = "Hotels & Island Resorts")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // City Filters Banner
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
                            text = "Filter by Island or City",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(cities) { city ->
                                FilterChip(
                                    selected = selectedCity == city,
                                    onClick = { selectedCity = city },
                                    label = { Text(city) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandNavy,
                                        selectedLabelColor = TextInverse
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary Amenity Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = minStars == 5,
                                onClick = { minStars = if (minStars == 5) 0 else 5 },
                                label = { Text("5-Star Only") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandGold,
                                    selectedLabelColor = BrandNavyDark
                                )
                            )
                            FilterChip(
                                selected = filterWithPool,
                                onClick = { filterWithPool = !filterWithPool },
                                label = { Text("With Pool") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandGold,
                                    selectedLabelColor = BrandNavyDark
                                )
                            )
                        }
                    }
                }
            }

            // Results count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found ${filteredHotels.size} Properties",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Rates in PGK",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSubtle
                    )
                }
            }

            // Hotel list
            if (filteredHotels.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = TextSubtle,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hotels found in $selectedCity",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(filteredHotels) { hotel ->
                    HotelCard(hotel = hotel) {
                        onSelectHotel(hotel)
                    }
                }
            }
        }
    }
}
