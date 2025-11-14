# Application Météo Android

Application Android complète de prévisions météorologiques utilisant l'API gratuite open-meteo.com.

## 🌟 Fonctionnalités

### ✅ Fonctionnalités Implémentées

1. **Écran d'accueil**
   - Liste des villes favorites avec résumé météo
   - Pull-to-refresh pour actualiser les données
   - Affichage de la température actuelle et icône météo
   - Indicateur de données en cache

2. **Système de recherche**
   - Recherche de ville via l'API Geocoding d'open-meteo
   - Affichage des résultats avec nom, pays et coordonnées
   - Bouton de géolocalisation avec gestion des permissions

3. **Écran de détail météo**
   - Température actuelle avec conditions météo
   - Températures min/max du jour
   - Vitesse du vent, humidité, précipitations
   - Prévisions horaires détaillées (24h)
   - Bouton favori (étoile) pour ajouter/supprimer
   - Pull-to-refresh

4. **Gestion des favoris**
   - Ajout/suppression de favoris
   - Persistance locale avec Room
   - Confirmation avant suppression

5. **Cache et mode hors connexion**
   - Mise en cache des données météo (30 minutes)
   - Affichage des données en cache si pas de connexion
   - Indicateur visuel pour les données en cache
   - Timestamp sur les données

6. **Gestion des erreurs**
   - Messages clairs pour chaque type d'erreur
   - Snackbar pour les notifications
   - Gestion des timeouts et erreurs réseau

7. **Gestion de la rotation**
   - État conservé via ViewModel
   - Configuration changes gérés automatiquement

## 🏗️ Architecture

### MVVM (Model-View-ViewModel)

```
app/
├── data/
│   ├── local/
│   │   ├── dao/           # Data Access Objects
│   │   ├── entity/        # Entités Room
│   │   └── WeatherDatabase.kt
│   ├── model/             # Modèles API
│   ├── remote/            # Services Retrofit
│   └── repository/        # Repositories
├── domain/
│   └── model/             # Modèles métier
├── ui/
│   ├── components/        # Composants réutilisables
│   ├── screen/            # Écrans Compose
│   ├── theme/             # Thème Material 3
│   └── viewmodel/         # ViewModels
├── navigation/            # Navigation Compose
├── util/                  # Utilitaires
└── MainActivity.kt
```

## 🛠️ Technologies Utilisées

- **Kotlin** - Langage de programmation
- **Jetpack Compose** - UI moderne et déclarative
- **Material 3** - Design system
- **Retrofit** - Appels API REST
- **Room** - Base de données locale
- **Coroutines & Flow** - Programmation asynchrone
- **ViewModel** - Gestion du cycle de vie
- **Navigation Compose** - Navigation entre écrans
- **Accompanist** - SwipeRefresh et Permissions
- **Google Play Services** - Géolocalisation

## 📡 APIs Utilisées

### Geocoding API
```
https://geocoding-api.open-meteo.com/v1/search?name=[VILLE]
```

### Weather API
```
https://api.open-meteo.com/v1/forecast?
  latitude=42.7028&
  longitude=9.45&
  hourly=temperature_2m,relative_humidity_2m,apparent_temperature,rain,wind_speed_10m&
  models=meteofrance_seamless
```

**Modèle utilisé:** `meteofrance_seamless` (obligatoire)

## 🚀 Installation

1. Cloner le projet
2. Ouvrir dans Android Studio
3. Synchroniser les dépendances Gradle
4. Lancer l'application sur un émulateur ou appareil physique

### Prérequis
- Android Studio Hedgehog ou supérieur
- SDK Android 26 (Android 8.0) minimum
- SDK Android 34 (Android 14) pour la compilation

## 📱 Permissions Requises

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

## 🎨 Fonctionnalités UI

- **Material 3 Design** - Interface moderne et cohérente
- **Dark/Light Theme** - Support automatique
- **Animations** - Transitions fluides
- **Icônes météo** - Représentation visuelle des conditions
- **Pull-to-refresh** - Actualisation intuitive
- **Swipe-to-delete** - Suppression facile des favoris

## 🔧 Configuration

### Cache
Le cache des données météo expire après **30 minutes**. Modifiable dans `WeatherRepository.kt`:

```kotlin
companion object {
    private const val CACHE_EXPIRY_TIME = 30 * 60 * 1000L // 30 minutes
}
```

### Conditions Météo
Les conditions sont déterminées automatiquement selon les précipitations et la température:
- **Ensoleillé** - Pas de pluie, température > 20°C
- **Partiellement nuageux** - Pluie légère (0.1-1.0 mm)
- **Pluvieux** - Pluie modérée (1.0-5.0 mm)
- **Forte pluie** - Pluie importante (> 5.0 mm)

## 📝 Notes Techniques

### Gestion du Cache
- Les données sont automatiquement mises en cache à chaque requête API
- Le cache est vérifié avant chaque nouvelle requête
- Les données expirées sont nettoyées automatiquement

### Gestion des Erreurs
- **NoInternet** - Pas de connexion internet
- **Timeout** - Délai d'attente dépassé
- **ServerError** - Erreur serveur
- **ApiError** - Erreur API avec code et message

### Base de Données
- **cities** - Table des villes (favoris)
- **weather_cache** - Table du cache météo

## 🐛 Dépannage

### Problème de géolocalisation
1. Vérifier que les permissions sont accordées
2. Activer le GPS sur l'appareil
3. Tester sur un appareil physique (l'émulateur peut avoir des limitations)

### Données non actualisées
1. Utiliser le pull-to-refresh
2. Vérifier la connexion internet
3. Le cache expire après 30 minutes

### Erreur de compilation
1. Synchroniser les dépendances Gradle
2. Nettoyer et rebuilder le projet
3. Vérifier la version de Kotlin et des plugins

## 📄 Licence

Projet éducatif - M2 Android

## 👨‍💻 Auteur

Développé pour le cours de développement Android M2
