# Application de Gestion de Bibliothèque en Scala

Ce projet est une application de gestion de bibliothèque développée en Scala. Elle permet de gérer les livres, les utilisateurs, les emprunts, les retours, les réservations et les pénalités de retard.

## Objectif

L'objectif principal de ce projet était de mettre en pratique les concepts de la programmation fonctionnelle en Scala et de créer un système de bibliothèque fonctionnel et facile à utiliser[cite: 2].

## Fonctionnalités

* **Gestion des livres :** ajout, suppression et affichage des livres.
* **Gestion des utilisateurs :** inscription des utilisateurs.
* **Emprunts :** enregistrement des emprunts avec date de début et date de fin prévue.
* **Retours :** enregistrement des retours et calcul des pénalités de retard.
* **Réservations :** gestion des réservations de livres.
* **Affichage :**
    * Liste des livres disponibles.
    * Liste des livres empruntés.
    * Emprunts par utilisateur.
    * Liste des réservations.
* **Tri :** Tri des livres par auteur, genre et date d'ajout.
* **Calcul des pénalités :** Calcul automatique des pénalités pour les retards. [cite: 4]

## Technologies utilisées

* Scala
* sbt (Scala Build Tool)
* `java.time` (pour la gestion des dates)

## Installation

1.  Assurez-vous d'avoir Scala et sbt installés sur votre machine.
2.  Clonez le dépôt : `git clone <URL_DU_DEPOT>`
3.  Naviguez vers le répertoire du projet : `cd <nom_du_projet>`
4.  Compilez et exécutez le projet : `sbt run`

## Utilisation

L'application est une application en console. Suivez les instructions du menu pour interagir avec le système.

## Exemples d'utilisation

_(Vous pouvez ajouter quelques captures d'écran de l'application en cours d'exécution pour montrer les différentes fonctionnalités. Le rapport mentionne des captures d'écran qui mettent en avant des cas d’usage réel [cite: 1])_

## Compétences démontrées

* Programmation en Scala [cite: 3]
* Programmation fonctionnelle [cite: 3, 6]
* Gestion de collections immuables [cite: 3]
* Conception orientée objet
* Gestion de la logique métier [cite: 4]
* Résolution de problèmes
* Utilisation de *case classes*
* Gestion des dates et des délais

## Améliorations futures

* Intégration d'une interface graphique [cite: 7]
* Persistance des données (par exemple, avec une base de données) [cite: 7]
* Mise en réseau du système pour une application multi-utilisateurs [cite: 7]

## Auteur

Fidèle Ledoux

## Remerciements

_(Si vous souhaitez remercier des personnes ou des ressources)_
