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
		int selection;
		AppCentraleMethodes methodes = new AppCentraleMethodes();

		System.out.println("Bienvenue dans l'application centrale!");
	
		do {
			methodes.afficherMenu();
			System.out.println("Entrez un num�ro ");
			selection = scanner.nextInt();

			switch (selection) {
			case 0:
				System.out.println("Au revoir");
				break;
			case 1:
				methodes.ajouterLocal();
				break;
			case 2:
				methodes.ajouterExamen();
				break;
			case 3:
				methodes.encoderHeureDebut();
				break;
			case 4:
				methodes.reserverLocal();
				break;
			case 5:
				methodes.visualiserExamenUnBloc();
				break;
			case 6:
				methodes.visualiserToutsExamensUnLocal();
				break;
			case 7:
				methodes.visualiserExamenPasCompletementReserves();
				break;
			case 8:
				methodes.visualiserNbrExamenPasCompletementReserve();
				break;
			default:
				System.out.println("Entrez un entier entre 0 et 8");
				break;
			}
		} while (selection != 0);
		scanner.close();
	}
}