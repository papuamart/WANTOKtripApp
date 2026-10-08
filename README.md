# WANTOKTrip - Android App

WANTOKTrip (formerly SkyWings PNG) is a modern travel booking and tourism application built natively for Android using Kotlin and Jetpack Compose. It connects travelers across Papua New Guinea and the wider Melanesian Pacific (Solomon Islands, Vanuatu, Fiji, New Caledonia).

## Features

- **Explore & Discover**: Curated flight deals, flash sales, popular Melanesian routes, and regional island resorts.
- **Flight Booking**: Search flights across Air Niugini, PNG Air, Qantas, Solomon Airlines, Air Vanuatu, and Fiji Airways. Filter by one-way/round-trip, class (Economy/Business), and direct non-stop routes.
- **Hotel & Resort Reservation**: Browse premier hotels across Port Moresby, Lae, Kokopo, Honiara, Port Vila, Nadi, and Noumea. Filter by amenities (Pool, Spa, Beach).
- **Payment & Checkout**:
  - PNG Bank Transfer with direct bank details for **TISA Bank** and **Kina Bank** (with one-tap copying and unique transfer reference codes).
  - Mobile QR code payment with TISA Bank Mobile Banking support.
  - Credit / Debit Card & Mobile Wallet options.
  - Loyalty points redemption directly during checkout (e.g. 200 pts = K10 off).
- **Bookings Management**: View confirmed, pending bank-transfer, and cancelled itineraries. Includes electronic boarding passes and QR codes for counter check-in.
- **Saved Digital Boarding Passes**: Offline-ready flight boarding passes with seat assignments, gates, and terminal details.
- **Wantok Rewards Loyalty Club**: Tier membership (Bronze, Silver, Gold, Platinum), points balance (Kina cash equivalent), tier perks, and points transaction history.
- **Payment Proof Portal**:
  - Customer submission form to upload transfer receipt details and transaction IDs.
  - Tracking portal to monitor payment review status.
  - Admin verification dashboard allowing staff to verify and approve incoming transfers, automatically validating e-tickets.

## Architecture & Tech Stack

- **Framework**: Android SDK 36, Kotlin 2.2.10, Jetpack Compose, Material 3
- **Image Loading**: Coil Compose 2.7.0
- **Build System**: Gradle 9.3.1 (Kotlin DSL), Android Gradle Plugin 9.1.1
- **Icons**: Material Icons Extended & Custom Adaptive App Launcher Icons
