//  Projet Scala : Gestion d'une bibliothèque intelligente
//  Objectif : Créer un système de gestion de bibliothèque avec emprunts, retours,
//  réservations, pénalités de retard, et classement.
//  Auteur : Fidèle Ledoux

import scala.io.StdIn.readLine
import java.time.{LocalDate, Period}

// =============================================
// 1. DÉFINITION DES ENTITÉS
// =============================================
case class Livre(id: Int, titre: String, auteur: String, genre: String, dateAjout: LocalDate = LocalDate.now)
case class Utilisateur(id: Int, nom: String)
case class Emprunt(livre: Livre, utilisateur: Utilisateur, dateDebut: LocalDate, dateFin: Option[LocalDate])
case class Reservation(livre: Livre, utilisateur: Utilisateur, dateReservation: LocalDate)

// =============================================
// 2. CLASSE PRINCIPALE : Bibliotheque
// =============================================
case class Bibliotheque(
  livres: List[Livre] = List(
    Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Fiction"),
    Livre(2, "1984", "George Orwell", "Dystopie"),
    Livre(3, "Les Misérables", "Victor Hugo", "Classique")
  ),
  utilisateurs: List[Utilisateur] = Nil,
  emprunts: List[Emprunt] = Nil,
  reservations: List[Reservation] = Nil
) {
  val dureeMax = 14 // jours
  val penaliteParJour = 1.0 // euros

  // 2.1 Ajouter un livre
  def ajouterLivre(livre: Livre): Bibliotheque = {
    copy(livres = livre :: livres)
  }

  // 2.2 Supprimer un livre
  def retirerLivre(id: Int): Bibliotheque = {
    copy(livres = livres.filterNot(_.id == id))
  }

  // 2.3 Inscription utilisateur
  def inscrireUtilisateur(utilisateur: Utilisateur): Bibliotheque = {
    copy(utilisateurs = utilisateur :: utilisateurs)
  }

  // 2.4 Emprunter un livre
  def emprunterLivre(idLivre: Int, idUtilisateur: Int, date: LocalDate): Bibliotheque = {
    (livres.find(_.id == idLivre), utilisateurs.find(_.id == idUtilisateur)) match {
      case (Some(l), Some(u)) if !emprunts.exists(e => e.livre.id == idLivre && e.dateFin.isEmpty) =>
        val e = Emprunt(l, u, date, None)
        copy(emprunts = e :: emprunts, reservations = reservations.filterNot(r => r.livre.id == idLivre && r.utilisateur.id == idUtilisateur))
      case (Some(_), Some(_)) => reserverLivre(idLivre, idUtilisateur, date)
      case _ => this
    }
  }

  // 2.5 Réserver un livre
  def reserverLivre(idLivre: Int, idUtilisateur: Int, date: LocalDate): Bibliotheque = {
    if (!reservations.exists(r => r.livre.id == idLivre && r.utilisateur.id == idUtilisateur)) {
      (livres.find(_.id == idLivre), utilisateurs.find(_.id == idUtilisateur)) match {
        case (Some(l), Some(u)) => copy(reservations = Reservation(l, u, date) :: reservations)
        case _ => this
      }
    } else this
  }

  // 2.6 Retourner un livre
  def retournerLivre(idLivre: Int, dateRetour: LocalDate): Bibliotheque = {
    val majEmprunts = emprunts.map {
      case e if e.livre.id == idLivre && e.dateFin.isEmpty => e.copy(dateFin = Some(dateRetour))
      case e => e
    }
    val prochain = reservations.reverse.find(_.livre.id == idLivre)
    val (majAuto, resteResa) = prochain match {
      case Some(r) => (Emprunt(r.livre, r.utilisateur, dateRetour, None) :: majEmprunts, reservations.filterNot(_ == r))
      case None => (majEmprunts, reservations)
    }
    copy(emprunts = majAuto, reservations = resteResa)
  }

  // 2.7 Fonctions d'affichage et de gestion
  def livresDisponibles: List[Livre] = {
    livres.filter(l => !emprunts.exists(e => e.livre.id == l.id && e.dateFin.isEmpty))
  }

  def livresEmpruntes: List[Livre] = {
    emprunts.filter(_.dateFin.isEmpty).map(_.livre)
  }

  def empruntsParUtilisateur(idUtilisateur: Int): List[Emprunt] = {
    emprunts.filter(_.utilisateur.id == idUtilisateur)
  }

  def verifierRetards(): Unit = {
    val aujourdHui = LocalDate.now
    emprunts.foreach {
      case e if e.dateFin.exists(df => Period.between(e.dateDebut, df).getDays > dureeMax) =>
        val retard = Period.between(e.dateDebut, e.dateFin.get).getDays - dureeMax
        val penalite = retard * penaliteParJour
        println(s"${e.utilisateur.nom} a rendu '${e.livre.titre}' en retard : $retard jour(s), pénalité = $penalite €")
      case e if Period.between(e.dateDebut, aujourdHui).getDays > dureeMax && e.dateFin.isEmpty =>
        val retard = Period.between(e.dateDebut, aujourdHui).getDays - dureeMax
        val penalite = retard * penaliteParJour
        println(s"${e.utilisateur.nom} a un retard pour '${e.livre.titre}' : $retard jour(s), pénalité = $penalite €")
      case _ =>
    }
  }

  def afficherReservations(): Unit = {
    reservations.foreach(r => println(s" Livre '${r.livre.titre}' réservé par ${r.utilisateur.nom} le ${r.dateReservation}"))
  }

  def trierLivresParAuteur(): Unit = {
    livres.sortBy(_.auteur).foreach(println)
  }

  def trierLivresParGenre(): Unit = {
    livres.sortBy(_.genre).foreach(println)
  }

  def trierLivresParDate(): Unit = {
    livres.sortBy(_.dateAjout).foreach(println)
  }
}
// =============================================
// 3. INTERFACE UTILISATEUR CONSOLE
// =============================================
object Main extends App {
  var biblio = Bibliotheque()

  def menu(): Unit = println("""
    |===== MENU PRINCIPAL - BIBLIOTHÈQUE =====
    |1. Ajouter un livre
    |2. Inscrire un utilisateur
    |3. Emprunter un livre
    |4. Retourner un livre
    |5. Afficher les livres disponibles
    |6. Afficher les livres empruntés
    |7. Afficher les emprunts d’un utilisateur
    |8. Réserver un livre
    |9. Afficher les réservations
    |10. Vérifier les retards
    |11. Trier les livres par auteur
    |12. Trier les livres par genre
    |13. Trier les livres par date d'ajout
    |0. Quitter
    |==========================================
    |""".stripMargin)

  var choix = -1
  while (choix != 0) {
    menu()
    choix = readLine("Choix : ").toIntOption.getOrElse(-1)
    choix match {
      case 1 =>
        val id = readLine("ID : ").toInt
        val titre = readLine("Titre : ")
        val auteur = readLine("Auteur : ")
        val genre = readLine("Genre : ")
        biblio = biblio.ajouterLivre(Livre(id, titre, auteur, genre))
        println(s"Le livre '$titre' de $auteur (Genre : $genre) a été ajouté avec succès.")
      case 2 =>
        val id = readLine("ID utilisateur : ").toInt
        val nom = readLine("Nom : ")
        biblio = biblio.inscrireUtilisateur(Utilisateur(id, nom))
        println(s"Utilisateur $nom (ID: $id) inscrit avec succès.")
      case 3 =>
        val idLivre = readLine("ID livre : ").toIntOption.getOrElse(-1)
        val idUtilisateur = readLine("ID utilisateur : ").toIntOption.getOrElse(-1)

        if (idLivre == -1 || idUtilisateur == -1) {
          println("Entrée invalide. Veuillez entrer des nombres valides.")
        } else {
          (biblio.utilisateurs.find(_.id == idUtilisateur), biblio.livres.find(_.id == idLivre)) match {
            case (Some(u), Some(l)) =>
              biblio = biblio.emprunterLivre(idLivre, idUtilisateur, LocalDate.now)
              println(s"Le livre '${l.titre}' a été emprunté avec succès par ${u.nom}.")
            case (None, _) =>
              println("Erreur : utilisateur introuvable. Veuillez inscrire l'utilisateur.")
            case (_, None) =>
              println("Erreur : livre introuvable.")
         }
       }
      case 4 =>
        val idLivre = readLine("ID livre à retourner : ").toInt
        biblio = biblio.retournerLivre(idLivre, LocalDate.now)
        println(s"Livre $idLivre retourné avec succès.")
      case 5 =>
        println(" Livres disponibles :")
        biblio.livresDisponibles.foreach(l => println(s"${l.id} - ${l.titre}"))
      case 6 =>
        println(" Livres empruntés :")
        biblio.livresEmpruntes.foreach(l => println(s"${l.id} - ${l.titre}"))
      case 7 =>
        val id = readLine("ID utilisateur : ").toInt
        biblio.empruntsParUtilisateur(id).foreach(e => println(s"📘 ${e.livre.titre} | du ${e.dateDebut} au ${e.dateFin.getOrElse("en cours")}"))
      case 8 =>
        val idLivre = readLine("ID livre à réserver : ").toIntOption.getOrElse(-1)
        val idUtilisateur = readLine("ID utilisateur : ").toIntOption.getOrElse(-1)

        if (idLivre == -1 || idUtilisateur == -1) {
          println("Entrée invalide. Veuillez entrer des nombres valides.")
        } else {
          (biblio.utilisateurs.find(_.id == idUtilisateur), biblio.livresDisponibles.find(_.id == idLivre)) match {
            case (Some(u), Some(l)) =>
              biblio = biblio.reserverLivre(idLivre, idUtilisateur, LocalDate.now)
              println(s"Le livre '${l.titre}' a été réservé avec succès par ${u.nom}.")
            case (None, _) =>
              println("Erreur : utilisateur introuvable. Veuillez inscrire l'utilisateur.")
            case (_, None) =>
              println("Erreur : livre introuvable ou non disponible.")
         }
        }
      case 9 =>
        biblio.afficherReservations()
      case 10 =>
        biblio.verifierRetards()
      case 11 =>
        biblio.trierLivresParAuteur()
      case 12 =>
        biblio.trierLivresParGenre()
      case 13 =>
        biblio.trierLivresParDate()
      case 0 =>
        println("Merci et à bientôt !")
      case _ =>
        println("Choix invalide")
    }
  }
}