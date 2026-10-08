package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AdminProofScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val proofs by TravelRepository.paymentProofs.collectAsState()

    var activeDialogProof by remember { mutableStateOf<PaymentProof?>(null) }
    var actionType by remember { mutableStateOf("approve") } // "approve" or "reject"
    var feedbackInput by remember { mutableStateOf("") }

    if (activeDialogProof != null) {
        val target = activeDialogProof!!
        AlertDialog(
            onDismissRequest = { activeDialogProof = null },
            title = {
                Text(
                    text = if (actionType == "approve") "Approve Payment Proof" else "Reject Payment Proof",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Booking: ${target.bookingRef}\nAmount: K${target.amount} PGK (${target.bankName})\nReference: ${target.transferRef}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = { Text("Verification notes for customer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newStatus = if (actionType == "approve") "approved" else "rejected"
                        val note = feedbackInput.ifBlank {
                            if (actionType == "approve") "Payment received and verified by accounts." else "Payment receipt could not be verified."
                        }
                        TravelRepository.updateProofStatus(target.id, newStatus, note)
                        activeDialogProof = null
                        feedbackInput = ""
                        Toast.makeText(context, "Proof marked as $newStatus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actionType == "approve") StatusSuccess else StatusError
                    )
                ) {
                    Text(if (actionType == "approve") "Approve & Confirm" else "Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogProof = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Admin: Payment Verification",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = BrandNavyDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Admin Finance Portal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandGold
                        )
                        Text(
                            text = "Verify incoming TISA and Kina Bank transfers to confirm pending customer e-tickets.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextInverse
                        )
                    }
                }
            }

            if (proofs.isEmpty()) {
                item {
                    Text("No customer proofs submitted yet.", modifier = Modifier.padding(20.dp))
                }
            } else {
                items(proofs) { proof ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                        text = "${proof.customerName} • ${proof.customerEmail}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                StatusBadge(status = proof.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bank: ${proof.bankName} • Amount: K${proof.amount} PGK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Transfer Ref: ${proof.transferRef} (Submitted ${proof.date})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            if (proof.notes.isNotBlank()) {
                                Text(
                                    text = "Customer Note: ${proof.notes}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            if (proof.status == "pending") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            activeDialogProof = proof
                                            actionType = "approve"
                                            feedbackInput = "Payment received and verified by accounts."
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve & Ticket", color = TextInverse, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            activeDialogProof = proof
                                            actionType = "reject"
                                            feedbackInput = "Payment reference not matched in bank statement."
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reject", color = StatusError, fontWeight = FontWeight.Bold)
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
