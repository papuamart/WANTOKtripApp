package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun BookingConfirmationScreen(
    booking: Booking,
    onViewBookings: () -> Unit,
    onSubmitProof: (String) -> Unit,
    onViewBoardingPasses: () -> Unit,
    onGoHome: () -> Unit
) {
    val isPending = booking.status == "pending"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Success Icon
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(if (isPending) StatusWarning.copy(alpha = 0.15f) else StatusSuccess.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPending) Icons.Default.HourglassTop else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isPending) StatusWarning else StatusSuccess,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isPending) "Booking Placed (Pending Payment)" else "Booking Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Thank you, ${booking.passengerName}. Your reservation is registered.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Booking Card Summary
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
                            text = "Booking Reference",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSubtle
                        )
                        Text(
                            text = booking.bookingRef,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                    StatusBadge(status = booking.status)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "Service Type", value = booking.type.replaceFirstChar { it.uppercase() })
                DetailRow(label = "Date Booked", value = booking.date)
                DetailRow(label = "Payment Method", value = booking.paymentMethod)
                if (booking.transferRef != null) {
                    DetailRow(label = "Payment Ref", value = booking.transferRef)
                }
                DetailRow(label = "Total Paid/Due", value = "K${booking.totalAmount} PGK")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // If pending, show prompt to upload payment receipt proof
        if (isPending) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGold.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BrandGoldDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Action Required: Submit Payment Proof",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavyDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "After transferring funds via TISA Bank or Kina Bank, upload your payment receipt or transfer reference so our finance team can verify and issue your confirmed ticket.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onSubmitProof(booking.bookingRef) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = BrandNavyDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Submit Payment Proof Now",
                            color = BrandNavyDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons
        Button(
            onClick = onViewBookings,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("View All My Bookings", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (booking.type == "flight") {
            OutlinedButton(
                onClick = onViewBoardingPasses,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = BrandNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Boarding Pass", color = BrandNavy, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        TextButton(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text("Back to Home", color = TextSecondary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
