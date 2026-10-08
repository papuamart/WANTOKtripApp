package com.example.data

data class Flight(
    val id: String,
    val airline: String,
    val airlineCode: String,
    val airlineLogo: String,
    val flightNumber: String,
    val from: String,
    val fromCode: String,
    val to: String,
    val toCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val stops: Int,
    val price: Int,
    val currency: String = "PGK",
    val travelClass: String = "Economy",
    val seatsLeft: Int = 10,
    val date: String = "2026-09-15",
    val refundable: Boolean = true
)

data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val city: String,
    val country: String,
    val stars: Int,
    val rating: Double,
    val reviewCount: Int,
    val pricePerNight: Int,
    val currency: String = "PGK",
    val image: String,
    val amenities: List<String>,
    val description: String,
    val refundable: Boolean = true,
    val discount: Int? = null
)

data class Deal(
    val id: String,
    val type: String, // "flight" or "hotel"
    val title: String,
    val subtitle: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val discount: Int,
    val image: String,
    val tag: String
)

data class Booking(
    val id: String,
    val type: String, // "flight" or "hotel"
    val status: String, // "confirmed", "pending", "cancelled"
    val bookingRef: String,
    val date: String,
    val totalAmount: Int,
    val currency: String = "PGK",
    val passengerName: String,
    val paymentMethod: String,
    val transferRef: String? = null,
    val flight: Flight? = null,
    val hotel: Hotel? = null
)

data class PaymentProof(
    val id: String,
    val bookingRef: String,
    val customerName: String,
    val customerEmail: String,
    val bankName: String, // "TISA Bank", "Kina Bank", "BSP", "Other"
    val amount: Int,
    val transferRef: String,
    val date: String,
    val status: String, // "pending", "approved", "rejected"
    val notes: String = "",
    val adminFeedback: String = ""
)

data class BoardingPass(
    val id: String,
    val bookingRef: String,
    val passengerName: String,
    val airline: String,
    val flightNumber: String,
    val fromCode: String,
    val fromCity: String,
    val toCode: String,
    val toCity: String,
    val departureTime: String,
    val arrivalTime: String,
    val date: String,
    val seat: String,
    val gate: String,
    val terminal: String,
    val boardingGroup: String,
    val barcode: String
)

data class LoyaltyTransaction(
    val id: String,
    val title: String,
    val points: Int,
    val date: String,
    val isEarned: Boolean
)

data class DestinationItem(
    val code: String,
    val name: String,
    val country: String,
    val flag: String,
    val image: String,
    val fromPrice: Int
)

data class MelanesianCityHotel(
    val city: String,
    val country: String,
    val flag: String,
    val image: String,
    val fromPrice: Int,
    val hotelId: String
)
