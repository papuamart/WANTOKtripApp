package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.*
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun CheckoutScreen(
    flight: Flight? = null,
    hotel: Hotel? = null,
    nights: Int = 1,
    onNavigateBack: () -> Unit,
    onBookingConfirmed: (Booking) -> Unit
) {
    val context = LocalContext.current
    val baseTotal = if (flight != null) flight.price else (hotel?.pricePerNight ?: 0) * nights
    val loyaltyPoints by TravelRepository.loyaltyPoints.collectAsState()

    var passengerName by remember { mutableStateOf(TravelRepository.userName) }
    var passengerEmail by remember { mutableStateOf(TravelRepository.userEmail) }
    var passengerPhone by remember { mutableStateOf(TravelRepository.userPhone) }

    var selectedPaymentMethod by remember { mutableStateOf("bank") } // "bank", "qrcode", "card", "wallet"
    var redeemPoints by remember { mutableStateOf(false) }

    val discount = if (redeemPoints && loyaltyPoints >= 200) 10 else 0
    val finalTotal = (baseTotal - discount).coerceAtLeast(0)

    val transferRef = remember { "TRF-${Random.nextInt(100000, 999999)}" }

    // Card state
    var cardNumber by remember { mutableStateOf("4532 8912 3456 7890") }
    var cardExpiry by remember { mutableStateOf("08/28") }
    var cardCvv by remember { mutableStateOf("321") }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $label: $text", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Checkout & Payment",
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
                            text = "Amount Due",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "K$finalTotal PGK",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BrandNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (passengerName.isBlank() || passengerEmail.isBlank()) {
                                Toast.makeText(context, "Please enter your name and email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (redeemPoints) {
                                TravelRepository.applyRedemption(200, 10)
                            }

                            val paymentMethodLabel = when (selectedPaymentMethod) {
                                "bank" -> "TISA / Kina Bank Transfer"
                                "qrcode" -> "TISA Bank QR Code"
                                "card" -> "Credit/Debit Card"
                                else -> "Mobile Wallet"
                            }

                            val booking = TravelRepository.addBooking(
                                type = if (flight != null) "flight" else "hotel",
                                itemFlight = flight,
                                itemHotel = hotel,
                                passengerName = passengerName,
                                paymentMethod = paymentMethodLabel,
                                amount = finalTotal,
                                transferRef = transferRef
                            )

                            onBookingConfirmed(booking)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Complete Booking",
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
            // Item Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Booking Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (flight != null) {
                        Text(
                            text = "${flight.airline} (${flight.flightNumber})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "${flight.from} (${flight.fromCode}) → ${flight.to} (${flight.toCode})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Date: ${flight.date} • ${flight.departureTime} • ${flight.travelClass}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSubtle
                        )
                    } else if (hotel != null) {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "${hotel.location}, ${hotel.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Duration: $nights nights @ K${hotel.pricePerNight}/night",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSubtle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Passenger Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Passenger / Guest Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passengerName,
                        onValueChange = { passengerName = it },
                        label = { Text("Full Name (as on ID / Passport)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passengerEmail,
                        onValueChange = { passengerEmail = it },
                        label = { Text("Email Address for Confirmation") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passengerPhone,
                        onValueChange = { passengerPhone = it },
                        label = { Text("Phone Number (PNG or Int'l)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Loyalty Points Redemption Option
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Redeem Wantok Points",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Balance: $loyaltyPoints pts. Redeem 200 pts for K10 discount.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = redeemPoints,
                        onCheckedChange = { redeemPoints = it },
                        enabled = loyaltyPoints >= 200,
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandGold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Methods Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentOptionRow(
                        title = "PNG Bank Transfer (TISA / Kina Bank)",
                        subtitle = "Direct deposit or mobile bank transfer",
                        selected = selectedPaymentMethod == "bank",
                        onClick = { selectedPaymentMethod = "bank" }
                    )

                    PaymentOptionRow(
                        title = "TISA Bank QR Code",
                        subtitle = "Scan with your TISA Mobile Banking app",
                        selected = selectedPaymentMethod == "qrcode",
                        onClick = { selectedPaymentMethod = "qrcode" }
                    )

                    PaymentOptionRow(
                        title = "Credit or Debit Card",
                        subtitle = "Visa / Mastercard / BSP KunduCard",
                        selected = selectedPaymentMethod == "card",
                        onClick = { selectedPaymentMethod = "card" }
                    )

                    PaymentOptionRow(
                        title = "Mobile Wallet",
                        subtitle = "Local or regional digital wallet",
                        selected = selectedPaymentMethod == "wallet",
                        onClick = { selectedPaymentMethod = "wallet" }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic details for selected method
                    when (selectedPaymentMethod) {
                        "bank" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceAlt,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "PNG Bank Account Details:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandNavy
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    BankDetailItem(
                                        title = "TISA BANK",
                                        accountName = "PAPUAMART.com LIMITED",
                                        accountNo = "0010099069",
                                        bsb = "068-001",
                                        swift = "TSLSPGPP",
                                        onCopy = { copyToClipboard("TISA Account", "0010099069") }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = BorderLight)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    BankDetailItem(
                                        title = "KINA BANK",
                                        accountName = "PAPUAmart.com LTD",
                                        accountNo = "26203219",
                                        bsb = "026411",
                                        swift = "KINIPGPG",
                                        onCopy = { copyToClipboard("Kina Bank Account", "26203219") }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = BrandNavyDark,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Your Payment Reference:",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = BrandGold
                                                )
                                                Text(
                                                    text = transferRef,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                            IconButton(onClick = { copyToClipboard("Transfer Ref", transferRef) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandGold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "qrcode" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceAlt,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Scan TISA Bank QR Code",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandNavy
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Account: Wewo Kotokay • No: 0010110536",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Image(
                                        painter = painterResource(id = R.drawable.tisa_bank_qrcode),
                                        contentDescription = "TISA Bank QR Code",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Include reference $transferRef in description",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BrandGoldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        "card" -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { cardNumber = it },
                                    label = { Text("Card Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = { cardExpiry = it },
                                        label = { Text("MM/YY") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = cardCvv,
                                        onValueChange = { cardCvv = it },
                                        label = { Text("CVV") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        "wallet" -> {
                            OutlinedTextField(
                                value = passengerPhone,
                                onValueChange = {},
                                label = { Text("Registered Mobile Wallet Number") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) BrandNavy.copy(alpha = 0.08f) else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = BrandNavy)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun BankDetailItem(
    title: String,
    accountName: String,
    accountNo: String,
    bsb: String,
    swift: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Name: $accountName",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "A/C: $accountNo (BSB: $bsb, Swift: $swift)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = BrandNavy
            )
        }
        IconButton(onClick = onCopy) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandNavy)
        }
    }
}
