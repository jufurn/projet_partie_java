package appUtilisateur;

import java.sql.*;
import java.util.Scanner;

import appCentrale.AppCentraleMethodes;
import bcrypt.BCrypt;
import org.postgresql.util.PSQLException;

public class AppUtilisateur {

    public static void main(String[] args) throws SQLException {

		Scanner scanner = new Scanner(System.in);

		//pour la selection de methodes
		int selection;
		AppUtilisateurMethodes methodes = new AppUtilisateurMethodes();
		while (!methodes.connection()){
			System.out.println("Connection échouée, email ou mot de passe non valide");
		}
		System.out.println("Bienvenue dans l'application utilisateur!");

		do {
			methodes.afficherMenu();
			System.out.println("Entrez un numero ");
			selection = scanner.nextInt();

			switch (selection) {
				case 0:
					System.out.println("Au revoir");
					break;
				case 1:
					methodes.ajouterUeAuPAE();
					break;
				case 2:
					methodes.enleverUeAuPAE();
					break;
				case 3:
					methodes.validerPAE();
					break;
				case 4:
					methodes.visualiserUesAjoutables();
					break;
				case 5:
					methodes.visualiserPAEEtudiant();
					break;
				case 6:
					methodes.reinitialiserPAEEtudiant();
					break;
				default:
					System.out.println("Entrez un entier entre 0 et 6");
					break;
			}
		} while (selection != 0);
		scanner.close();
	}
}