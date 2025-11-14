# Guide d'Utilisation - Application Météo

## 🚀 Démarrage Rapide

### 1. Synchroniser le Projet
```bash
# Dans Android Studio
File > Sync Project with Gradle Files
```

### 2. Lancer l'Application
```bash
# Via Android Studio
Run > Run 'app'

# Ou via ligne de commande
./gradlew installDebug
```

## 📱 Utilisation de l'Application

### Écran d'Accueil
1. **Première utilisation** : L'écran affiche "Aucune ville favorite"
2. **Ajouter une ville** : Cliquer sur le bouton "Ajouter une ville" ou l'icône de recherche
3. **Actualiser** : Tirer vers le bas pour rafraîchir les données
4. **Voir les détails** : Cliquer sur une carte de ville
5. **Supprimer un favori** : Cliquer sur l'icône de corbeille

### Écran de Recherche
1. **Rechercher une ville** : Taper au moins 2 caractères dans la barre de recherche
2. **Utiliser la géolocalisation** : 
   - Cliquer sur "Utiliser ma position"
   - Accepter les permissions de localisation
3. **Sélectionner une ville** : Cliquer sur un résultat pour voir la météo

### Écran de Détail
1. **Voir les informations** :
   - Température actuelle et conditions
   - Min/Max du jour
   - Humidité, vent, précipitations
   - Prévisions horaires (24h)
2. **Ajouter aux favoris** : Cliquer sur l'étoile en haut à droite
3. **Actualiser** : Tirer vers le bas
4. **Retour** : Flèche retour en haut à gauche

## 🔍 Fonctionnalités Détaillées

### Gestion des Favoris
- **Ajouter** : Depuis l'écran de détail, cliquer sur l'étoile vide
- **Retirer** : Cliquer sur l'étoile pleine ou sur la corbeille (écran d'accueil)
- **Persistance** : Les favoris sont sauvegardés localement

### Mode Hors Ligne
- Les données sont automatiquement mises en cache
- Durée du cache : 30 minutes
- Indicateur visuel "Données en cache" si hors ligne
- Les données en cache restent accessibles même après expiration

### Permissions
L'application demande les permissions suivantes :
- **Internet** : Récupération des données météo
- **Localisation** : Fonction "Ma position" (optionnel)

## 🎯 Cas d'Usage

### Scénario 1 : Ajouter sa ville
1. Lancer l'app
2. Cliquer sur "Ajouter une ville"
3. Taper le nom de votre ville
4. Sélectionner dans les résultats
5. Cliquer sur l'étoile pour ajouter aux favoris

### Scénario 2 : Utiliser la géolocalisation
1. Aller dans Recherche
2. Cliquer sur "Utiliser ma position"
3. Accepter les permissions
4. La météo de votre position s'affiche
5. Ajouter aux favoris si souhaité

### Scénario 3 : Consulter les prévisions
1. Depuis l'accueil, cliquer sur une ville favorite
2. Voir la météo actuelle en haut
3. Scroller pour voir les détails
4. Consulter les prévisions horaires en bas

### Scénario 4 : Mode hors ligne
1. Consulter une ville avec connexion internet
2. Désactiver le wifi/données mobiles
3. Retourner sur la ville
4. Les données en cache s'affichent avec un indicateur

## ⚠️ Messages d'Erreur

| Message | Signification | Solution |
|---------|---------------|----------|
| "Pas de connexion internet" | Pas de réseau | Vérifier wifi/données mobiles |
| "Délai d'attente dépassé" | Timeout API | Réessayer, vérifier la connexion |
| "Aucun résultat" | Ville non trouvée | Vérifier l'orthographe |
| "Permission de localisation refusée" | GPS non autorisé | Autoriser dans les paramètres |
| "Erreur lors de la récupération" | Erreur API | Réessayer plus tard |

## 🔧 Paramètres Techniques

### Limites
- **Recherche** : Minimum 2 caractères
- **Résultats** : Maximum 10 villes par recherche
- **Cache** : 30 minutes de validité
- **Prévisions** : 24 heures horaires

### Données Affichées
- Température en Celsius (°C)
- Vent en km/h
- Précipitations en mm
- Humidité en %

### APIs
- **Geocoding** : Recherche de villes
- **Weather** : Données météo (modèle Météo France)

## 💡 Astuces

1. **Actualisation rapide** : Tirer vers le bas sur n'importe quel écran avec des données
2. **Navigation rapide** : Utiliser le bouton retour Android pour revenir
3. **Favoris multiples** : Ajouter plusieurs villes pour comparer
4. **Économie de données** : Les données en cache évitent les requêtes inutiles
5. **Précision** : Utiliser la géolocalisation pour la météo exacte de votre position

## 🐛 Résolution de Problèmes

### L'app ne démarre pas
1. Vérifier que l'appareil a Android 8.0+ (API 26)
2. Nettoyer et rebuilder le projet
3. Désinstaller et réinstaller l'app

### La recherche ne fonctionne pas
1. Vérifier la connexion internet
2. Taper au moins 2 caractères
3. Attendre quelques secondes

### La géolocalisation ne marche pas
1. Vérifier que le GPS est activé
2. Accepter les permissions
3. Tester en extérieur (meilleure réception)
4. Sur émulateur : configurer une position fictive

### Les données ne s'actualisent pas
1. Utiliser le pull-to-refresh
2. Vérifier la connexion internet
3. Attendre l'expiration du cache (30 min)

### L'app plante
1. Vérifier les logs dans Logcat
2. Nettoyer les données de l'app
3. Réinstaller l'application

## 📞 Support

Pour toute question ou problème :
1. Consulter le README.md
2. Vérifier les logs Android Studio
3. Tester sur un appareil physique si problème sur émulateur

## 🎓 Pour les Développeurs

### Tester l'App
```bash
# Lancer les tests unitaires
./gradlew test

# Installer en mode debug
./gradlew installDebug

# Voir les logs
adb logcat | grep "WeatherApp"
```

### Modifier le Cache
Dans `WeatherRepository.kt`, ligne 23 :
```kotlin
private const val CACHE_EXPIRY_TIME = 30 * 60 * 1000L
```

### Changer les Conditions Météo
Dans `WeatherCondition.kt`, méthode `fromRainAndTemp()` :
```kotlin
fun fromRainAndTemp(rain: Double, temperature: Double): WeatherCondition
```

### Ajouter des Paramètres Météo
1. Modifier `WeatherApiService.kt` (paramètre `hourly`)
2. Ajouter les champs dans `WeatherResponse.kt`
3. Mettre à jour `WeatherEntity.kt`
4. Adapter l'UI dans les screens

## ✅ Checklist de Vérification

- [ ] L'app se lance sans erreur
- [ ] La recherche de ville fonctionne
- [ ] L'ajout aux favoris fonctionne
- [ ] La suppression de favoris fonctionne
- [ ] Le pull-to-refresh actualise les données
- [ ] La géolocalisation fonctionne (avec permissions)
- [ ] Le mode hors ligne affiche le cache
- [ ] Les erreurs sont affichées clairement
- [ ] La rotation d'écran conserve l'état
- [ ] La navigation fonctionne correctement

---

**Version** : 1.0  
**Dernière mise à jour** : Novembre 2024
