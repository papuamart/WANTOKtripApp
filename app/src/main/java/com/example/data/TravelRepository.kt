package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TravelRepository {

    private val _bookings = MutableStateFlow<List<Booking>>(MockDataProvider.initialBookings)
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _paymentProofs = MutableStateFlow<List<PaymentProof>>(MockDataProvider.initialPaymentProofs)
    val paymentProofs: StateFlow<List<PaymentProof>> = _paymentProofs.asStateFlow()

    private val _boardingPasses = MutableStateFlow<List<BoardingPass>>(MockDataProvider.initialBoardingPasses)
    val boardingPasses: StateFlow<List<BoardingPass>> = _boardingPasses.asStateFlow()

    private val _loyaltyPoints = MutableStateFlow(2850)
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()

    private val _activeDiscount = MutableStateFlow(0)
    val activeDiscount: StateFlow<Int> = _activeDiscount.asStateFlow()

    private val _loyaltyHistory = MutableStateFlow<List<LoyaltyTransaction>>(MockDataProvider.loyaltyTransactions)
    val loyaltyHistory: StateFlow<List<LoyaltyTransaction>> = _loyaltyHistory.asStateFlow()

    var userName: String = "John Kila"
    var userEmail: String = "john.kila@wantoktrip.pg"
    var userPhone: String = "+675 7123 4567"
    var userCity: String = "Port Moresby, Papua New Guinea"

    fun addBooking(
        type: String,
        itemFlight: Flight? = null,
        itemHotel: Hotel? = null,
        passengerName: String,
        paymentMethod: String,
        amount: Int,
        transferRef: String? = null
    ): Booking {
        val nextNum = _bookings.value.size + 1
        val ref = "SKY-2026-${String.format("%03d", nextNum)}"
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val status = if (paymentMethod.contains("Bank", ignoreCase = true) || paymentMethod.contains("QR", ignoreCase = true)) {
            "pending"
        } else {
            "confirmed"
        }

        val newBooking = Booking(
            id = "b_${System.currentTimeMillis()}",
            type = type,
            status = status,
            bookingRef = ref,
            date = today,
            totalAmount = amount,
            currency = "PGK",
            passengerName = passengerName,
            paymentMethod = paymentMethod,
            transferRef = transferRef,
            flight = itemFlight,
            hotel = itemHotel
        )

        _bookings.value = listOf(newBooking) + _bookings.value

        // Award points if confirmed or pending
        val earnedPts = (amount / 10).coerceAtLeast(20)
        _loyaltyPoints.value += earnedPts
        _loyaltyHistory.value = listOf(
            LoyaltyTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = "Booking $ref (${if (type == "flight") itemFlight?.flightNumber ?: "Flight" else itemHotel?.name ?: "Hotel"})",
                points = earnedPts,
                date = today,
                isEarned = true
            )
        ) + _loyaltyHistory.value

        // Generate Boarding pass if flight
        if (type == "flight" && itemFlight != null) {
            val bp = BoardingPass(
                id = "bp_${System.currentTimeMillis()}",
                bookingRef = ref,
                passengerName = passengerName.uppercase(),
                airline = itemFlight.airline,
                flightNumber = itemFlight.flightNumber,
                fromCode = itemFlight.fromCode,
                fromCity = itemFlight.from,
                toCode = itemFlight.toCode,
                toCity = itemFlight.to,
                departureTime = itemFlight.departureTime,
                arrivalTime = itemFlight.arrivalTime,
                date = itemFlight.date,
                seat = "${(10..28).random()}${listOf("A", "B", "C", "D").random()}",
                gate = "G0${(1..8).random()}",
                terminal = if (itemFlight.fromCode == "POM") "T1 Intl" else "Domestic",
                boardingGroup = "GROUP ${(1..3).random()}",
                barcode = "M1${passengerName.uppercase().replace(" ", "/")}  E${itemFlight.flightNumber} 258Y014A0032 100"
            )
            _boardingPasses.value = listOf(bp) + _boardingPasses.value
        }

        return newBooking
    }

    fun cancelBooking(bookingId: String) {
        _bookings.value = _bookings.value.map {
            if (it.id == bookingId) it.copy(status = "cancelled") else it
        }
    }

    fun addPaymentProof(
        bookingRef: String,
        bankName: String,
        amount: Int,
        transferRef: String,
        notes: String
    ): PaymentProof {
        val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val proof = PaymentProof(
            id = "p_${System.currentTimeMillis()}",
            bookingRef = bookingRef,
            customerName = userName,
            customerEmail = userEmail,
            bankName = bankName,
            amount = amount,
            transferRef = transferRef,
            date = todayStr,
            status = "pending",
            notes = notes
        )
        _paymentProofs.value = listOf(proof) + _paymentProofs.value
        return proof
    }

    fun updateProofStatus(proofId: String, newStatus: String, adminNotes: String) {
        _paymentProofs.value = _paymentProofs.value.map { proof ->
            if (proof.id == proofId) {
                proof.copy(status = newStatus, adminFeedback = adminNotes)
            } else proof
        }

        // If approved, also set corresponding booking to confirmed
        if (newStatus == "approved") {
            val approvedProof = _paymentProofs.value.find { it.id == proofId }
            if (approvedProof != null) {
                _bookings.value = _bookings.value.map { b ->
                    if (b.bookingRef == approvedProof.bookingRef) {
                        b.copy(status = "confirmed")
                    } else b
                }
            }
        }
    }

    fun applyRedemption(points: Int, discountPGK: Int) {
        if (_loyaltyPoints.value >= points) {
            _loyaltyPoints.value -= points
            _activeDiscount.value = discountPGK
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            _loyaltyHistory.value = listOf(
                LoyaltyTransaction(
                    id = "tx_${System.currentTimeMillis()}",
                    title = "K$discountPGK Reward Points Redemption",
                    points = points,
                    date = today,
                    isEarned = false
                )
            ) + _loyaltyHistory.value
        }
    }

    fun clearRedemption() {
        _activeDiscount.value = 0
    }
}
