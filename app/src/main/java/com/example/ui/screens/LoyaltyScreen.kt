package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoyaltyTransaction
import com.example.data.TravelRepository
import com.example.ui.components.WantokTopBar
import com.example.ui.theme.*

@Composable
fun LoyaltyScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val points by TravelRepository.loyaltyPoints.collectAsState()
    val history by TravelRepository.loyaltyHistory.collectAsState()

    val kinaValue = points * 0.05 // 100 pts = K5 -> 1 pt = K0.05
    val currentTier = when {
        points >= 5000 -> "Platinum"
        points >= 2500 -> "Gold"
        points >= 1000 -> "Silver"
        else -> "Bronze"
    }

    Scaffold(
        topBar = {
            WantokTopBar(
                title = "Wantok Rewards Club",
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
            contentPadding = PaddingValues(16.dp)
        ) {
            // Gold Tier Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(BrandNavyDark, BrandNavy, BrandGoldDark)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = BrandGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$currentTier Member",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextInverse
                                    )
                                }

                                Surface(
                                    color = BrandGold,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "1.5x Multiplier",
                                        color = BrandNavyDark,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "$points Points",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandGold
                            )
                            Text(
                                text = "Equivalent cash discount: K${String.format("%.2f", kinaValue)} PGK",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextInverse.copy(alpha = 0.9f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Tier Progress
                            val target = 5000f
                            val progress = (points / target).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = BrandGold,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${5000 - points} points to reach Platinum Tier",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextInverse.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Quick Redeem Options
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Redeem Reward Vouchers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Use your points to get instant discount codes on your next checkout",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RedeemPill(pointsReq = 200, discountVal = 10, currentPoints = points) {
                                TravelRepository.applyRedemption(200, 10)
                                Toast.makeText(context, "K10 discount activated for next checkout!", Toast.LENGTH_SHORT).show()
                            }
                            RedeemPill(pointsReq = 500, discountVal = 25, currentPoints = points) {
                                TravelRepository.applyRedemption(500, 25)
                                Toast.makeText(context, "K25 discount activated for next checkout!", Toast.LENGTH_SHORT).show()
                            }
                            RedeemPill(pointsReq = 1000, discountVal = 50, currentPoints = points) {
                                TravelRepository.applyRedemption(1000, 50)
                                Toast.makeText(context, "K50 discount activated for next checkout!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            // Tier Benefits Table
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Gold Tier Benefits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        BenefitRow(text = "Earn 1.5x points on every PGK spent")
                        BenefitRow(text = "Free seat selection on Air Niugini & PNG Air")
                        BenefitRow(text = "Priority check-in & boarding at POM Jackson Airport")
                        BenefitRow(text = "Extra 5kg baggage allowance")
                        BenefitRow(text = "Dedicated 24/7 Melanesian Concierge Support")
                    }
                }
            }

            // Transaction History Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Points Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(history) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (tx.isEarned) StatusSuccess.copy(alpha = 0.12f)
                                        else StatusWarning.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.isEarned) Icons.Default.Add else Icons.Default.Remove,
                                    contentDescription = null,
                                    tint = if (tx.isEarned) StatusSuccess else StatusWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = tx.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSubtle
                                )
                            }
                        }

                        Text(
                            text = if (tx.isEarned) "+${tx.points} pts" else "-${tx.points} pts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.isEarned) StatusSuccess else StatusWarning
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.RedeemPill(
    pointsReq: Int,
    discountVal: Int,
    currentPoints: Int,
    onRedeem: () -> Unit
) {
    val canRedeem = currentPoints >= pointsReq
    Button(
        onClick = onRedeem,
        enabled = canRedeem,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGold,
            disabledContainerColor = SurfaceAlt
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "K$discountVal Off",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (canRedeem) BrandNavyDark else TextSubtle
            )
            Text(
                text = "$pointsReq pts",
                style = MaterialTheme.typography.labelSmall,
                color = if (canRedeem) BrandNavyDark.copy(alpha = 0.8f) else TextSubtle
            )
        }
    }
}

@Composable
fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = BrandGoldDark,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}
