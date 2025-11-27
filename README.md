# Weather App (Open-Meteo)

Jednoduchá Android aplikace v Kotlinu (Jetpack Compose, MVVM), která zobrazuje počasí.

## Použitá API
- [Open-Meteo Forecast API](https://api.open-meteo.com/v1/forecast)
- [Open-Meteo Geocoding API](https://geocoding-api.open-meteo.com/v1/search)

## Funkce aplikace
- Vyhledání počasí podle názvu města
- Počasí pro aktuální polohu (geolokace)
- Uložení naposledy použitého města (DataStore Preferences)
- Splashscreen pro rychlý start (Android 12+)
- Vlastní ikona aplikace (slunce/mráček)

## Technický stack
- Kotlin, MVVM
- Jetpack Compose (UI + Navigation)
- Retrofit + Kotlinx Serialization (REST)
- Coroutines + Lifecycle ViewModel
- DataStore Preferences
- Google Play Services Location

## Struktura balíčků (zjednodušeně)
- `data/remote` – DTO, Retrofit servis a klient (Open-Meteo)
- `data/local` – DataStore (poslední město)
- `data/repository` – WeatherRepository (mapování a přístup k datům)
- `ui/navigation` – NavHost a trasy (home, detail, settings)
- `ui/screens/*` – Compose obrazovky
- `ui/theme` – barvy, typografie, theme
- `viewmodel` – ViewModely (Weather, Settings)

## Spuštění v Android Studiu
1. Otevři projekt v Android Studiu.
2. Sync Project with Gradle Files.
3. Pokud build v CLI hlásí chybějící JDK, nastav JAVA_HOME na `Android Studio\jbr`.
4. Spusť aplikaci na emulátoru nebo zařízení (API 24+).

Poznámky:
- Pro geolokaci je nutné povolit oprávnění polohy v systému.
- Detail screen přijímá data přes navigaci (JSON serializace WeatherData).

