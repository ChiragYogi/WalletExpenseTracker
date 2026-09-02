# Wallet

A simple, clean app to track your daily expenses and income — built entirely with Jetpack Compose.

[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)
![Minimum SDK](https://img.shields.io/badge/minimum%20sdk-Oreo%20(API%2026)-brightgreen)
![Target SDK](https://img.shields.io/badge/target%20sdk-Android%2016%20(API%2037)-brightgreen)

<a href='https://play.google.com/store/apps/details?id=com.babacode.walletexpensetracker'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width=200/></a>

## Screenshots

<img src="docs/screenshots/home.jpg" width="200"> <img src="docs/screenshots/add-transaction.jpg" width="200"> <img src="docs/screenshots/detail.jpg" width="200"> <img src="docs/screenshots/calendar.jpg" width="200">
<img src="docs/screenshots/budgets.jpg" width="200"> <img src="docs/screenshots/insights.jpg" width="200"> <img src="docs/screenshots/search.jpg" width="200"> <img src="docs/screenshots/settings.jpg" width="200">

## Features

- Track income and expenses with a fast add/edit flow
- Budgets — set spending limits per category and track progress
- Recurring transactions — automate regular income/expenses
- Insights — spending trends and breakdowns over time
- Weekly, monthly, and yearly detail views with charts
- Calendar view of transactions
- In-app search across transactions
- CSV export
- Daily/recurring reminder notifications
- Light and dark theme

## Tech Stack

- **UI:** Jetpack Compose, Material 3
- **Navigation:** Jetpack Navigation 3
- **Architecture:** MVVM, Hilt for DI
- **Persistence:** Room (with migrations), DataStore Preferences
- **Background work:** WorkManager (recurring transactions, reminders)
- **Build:** Gradle Kotlin DSL with a version catalog
- **Testing:** JUnit, Compose UI tests, Room instrumented tests

## Getting Started

1. Clone or fork this project
2. Create a Firebase project and add your `google-services.json` to `app/` for Crashlytics/Analytics
3. Open in Android Studio and run

## License

[MIT](https://choosealicense.com/licenses/mit/)
