package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PaymentProof
import com.example.data.TravelRepository
import com.example.ui.components.StatusBadge
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun PaymentProofScreen(
    initialBookingRef: String? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(if (initialBookingRef != null) 0 else 0) }

    val proofs by TravelRepository.paymentProofs.collectAsState()
    val bookings by TravelRepository.bookings.collectAsState()

    var bookingRefInput by remember { mutableStateOf(initialBookingRef ?: "") }
    var selectedBank by remember { mutableStateOf("TISA Bank") }
    var amountInput by remember { mutableStateOf("380") }
    var transferRefInput by remember { mutableStateOf("TRF-941029") }
    var notesInput by remember { mutableStateOf("Payment transfer sent via mobile banking.") }

    val banks = listOf("TISA Bank", "Kina Bank", "Bank South Pacific (BSP)", "Other")

    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Payment Proof Portal",
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BrandNavyDark,
                contentColor = BrandGold
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Submit Receipt", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Submissions (${proofs.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Submit Bank Transfer Receipt",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Enter details of your direct deposit or mobile QR transfer to confirm your reservation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = bookingRefInput,
                                onValueChange = { bookingRefInput = it },
                                label = { Text("Booking Reference (e.g. SKY-2026-003)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Bank Used for Payment:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                banks.take(2).forEach { bank ->
                                    FilterChip(
                                        selected = selectedBank == bank,
                                        onClick = { selectedBank = bank },
                                        label = { Text(bank) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandNavy,
                                            selectedLabelColor = TextInverse
                                        )
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                banks.drop(2).forEach { bank ->
                                    FilterChip(
                                        selected = selectedBank == bank,
                                        onClick = { selectedBank = bank },
                                        label = { Text(bank) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandNavy,
                                            selectedLabelColor = TextInverse
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = amountInput,
                                onValueChange = { amountInput = it },
                                label = { Text("Amount Paid in PGK (Kina)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = transferRefInput,
                                onValueChange = { transferRefInput = it },
                                label = { Text("Bank Transfer Reference / Transaction ID") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Additional Notes (e.g. Payer Name, Branch)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (bookingRefInput.isBlank() || transferRefInput.isBlank()) {
                                        Toast.makeText(context, "Please fill in booking reference and transfer ID", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val amt = amountInput.toIntOrNull() ?: 100
                                    TravelRepository.addPaymentProof(
                                        bookingRef = bookingRefInput,
                                        bankName = selectedBank,
                                        amount = amt,
                                        transferRef = transferRefInput,
                                        notes = notesInput
                                    )
                                    Toast.makeText(context, "Payment proof submitted for review!", Toast.LENGTH_SHORT).show()
                                    selectedTab = 1
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = BrandNavyDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Payment Proof", color = BrandNavyDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (proofs.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextSubtle,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No payment proofs submitted yet", color = TextSecondary)
                            }
                        }
                    } else {
                        items(proofs) { proof ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Booking ${proof.bookingRef}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandNavy
                                            )
                                            Text(
                                                text = "${proof.bankName} • ${proof.date}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                        StatusBadge(status = proof.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Transfer Ref: ${proof.transferRef} • Amount: K${proof.amount} PGK",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )

                                    if (proof.notes.isNotBlank()) {
                                        Text(
                                            text = "Note: ${proof.notes}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }

                                    if (proof.adminFeedback.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = BrandGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Staff Note: ${proof.adminFeedback}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = BrandNavyDark,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
