package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Booking
import com.example.data.TravelRepository
import com.example.ui.components.StatusBadge
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun BookingDetailScreen(
    booking: Booking,
    onNavigateBack: () -> Unit,
    onSubmitProof: (String) -> Unit,
    onViewBoardingPass: () -> Unit
) {
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Reservation?") },
            text = { Text("Are you sure you want to cancel booking ${booking.bookingRef}? Cancellation fees may apply according to fare rules.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        TravelRepository.cancelBooking(booking.id)
                        showCancelDialog = false
                        Toast.makeText(context, "Booking ${booking.bookingRef} cancelled", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                ) {
                    Text("Yes, Cancel", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Booking")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Booking Details",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
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
            // QR Code Ticket Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "E-Ticket / Voucher",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSubtle
                            )
                            Text(
                                text = booking.bookingRef,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                        StatusBadge(status = booking.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Check-in Code Image
                    Image(
                        painter = painterResource(id = R.drawable.tisa_bank_qrcode),
                        contentDescription = "Booking QR Check-in",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Present at airport counter or hotel reception",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Itinerary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Itinerary Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (booking.type == "flight" && booking.flight != null) {
                        DetailRow(label = "Airline", value = booking.flight.airline)
                        DetailRow(label = "Flight No", value = booking.flight.flightNumber)
                        DetailRow(label = "Departure", value = "${booking.flight.from} (${booking.flight.fromCode}) at ${booking.flight.departureTime}")
                        DetailRow(label = "Arrival", value = "${booking.flight.to} (${booking.flight.toCode}) at ${booking.flight.arrivalTime}")
                        DetailRow(label = "Duration", value = "${booking.flight.duration} (${if (booking.flight.stops == 0) "Direct" else "1-stop"})")
                        DetailRow(label = "Cabin Class", value = booking.flight.travelClass)
                    } else if (booking.hotel != null) {
                        DetailRow(label = "Property", value = booking.hotel.name)
                        DetailRow(label = "Location", value = booking.hotel.location)
                        DetailRow(label = "City / Region", value = "${booking.hotel.city}, ${booking.hotel.country}")
                        DetailRow(label = "Rating", value = "${booking.hotel.rating} Stars (${booking.hotel.stars}★)")
                    }

                    DetailRow(label = "Lead Guest", value = booking.passengerName)
                    DetailRow(label = "Booking Date", value = booking.date)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Advice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Payment Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Method", value = booking.paymentMethod)
                    if (booking.transferRef != null) {
                        DetailRow(label = "Transfer Reference", value = booking.transferRef)
                    }
                    DetailRow(label = "Total Charged", value = "K${booking.totalAmount} PGK")

                    if (booking.status == "pending") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = StatusWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Awaiting verification of bank transfer or QR payment. Once confirmed by our team, your electronic boarding pass will be fully validated.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrandNavyDark,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions
            if (booking.status == "pending") {
                Button(
                    onClick = { onSubmitProof(booking.bookingRef) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = BrandNavyDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Payment Receipt Proof", color = BrandNavyDark, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (booking.type == "flight") {
                OutlinedButton(
                    onClick = onViewBoardingPass,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = BrandNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Flight Boarding Pass", color = BrandNavy, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (booking.status != "cancelled") {
                TextButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Cancel Reservation", color = StatusError, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
