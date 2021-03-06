package appCentrale;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.postgresql.util.PSQLException;

public class ApplicationCentrale {

	public static void main(String[] args) throws SQLException {

		Scanner scanner = new Scanner(System.in);
		
		//pour la selection de methodes
		String selection;
		Boolean finish = false;
		AppCentraleMethodes methodes = new AppCentraleMethodes();
		//initialisation de la demo
		methodes.initDemo();

		System.out.println("Bienvenue dans l'application centrale!");
		do {
			methodes.afficherMenu();
			System.out.println("Entrez un numero ");
			selection = scanner.nextLine();

			switch (selection) {
			case "0":
				System.out.println("Au revoir");
				finish = true;
				break;
			case "1":
				methodes.ajouterUe();
				break;
			case "2":
				methodes.ajouterPrerequis();
				break;
			case "3":
				methodes.ajouterEtudiant();
				break;
			case "4":
				methodes.encoderUeValidee();
				break;
			case "5":
				methodes.visualiserEtudiantsDuBloc();
				break;
			case "6":
				methodes.visualiserCreditsPAEEtudiant();
				break;
			case "7":
				methodes.visualiserEtudiantsPAEPasValide();
				break;
			case "8":
				methodes.visualiserUesDuBloc();
				break;
			default:
				System.out.println("Entrez un entier entre 0 et 8");
				break;
			}
		} while (!finish);
		scanner.close();
	}
}