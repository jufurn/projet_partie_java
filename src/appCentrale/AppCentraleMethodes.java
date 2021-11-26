package appCentrale;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AppCentraleMethodes {
	
	Scanner scanner = new Scanner(System.in);
	String url;
	Connection con;
	
	public AppCentraleMethodes() {
		// creer connection
		url = "jdbc:postgresql://localhost:5432/projet";
		con = null;
	
		try {
			con = DriverManager.getConnection(url, "postgres", "");
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void ajouterUe() {
		System.out.println("Veuillez entrer le code de l'UE.");
		String codeUe = scanner.nextLine();
		System.out.println("Veuillez entrer le nom de l'UE.");
		String nomUe = scanner.nextLine();
		System.out.println("Veuillez entrer le bloc de l'UE.");
		String blocUe = scanner.nextLine();
		System.out.println("Veuillez entrer le nombre de credits de l'UE.");
		String creditsUe = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet2021.ajouterUe(?,?,?,?)");
			appCentrale.setString(1,codeUe);
			appCentrale.setString(2,nomUe);
			appCentrale.setInt(3,Integer.parseInt(codeUe));
			appCentrale.setInt(4,Integer.parseInt(creditsUe));

			appCentrale.execute();
			System.out.println("L'ajout a reussi. Voila les informations de l'UE : " + codeUe +" : "+ nomUe +" (Bloc : "+ blocUe + "; Credits : " + creditsUe);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally {
			System.out.println("\n");
		}
	}

	public void ajouterPrerequis() {
		System.out.println("Veuillez entrer le code de l'UE.");
		String codeUe = scanner.nextLine();
		System.out.println("Veuillez entrer le code de l'UE prerequis.");
		String codePrerequis = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet2021.ajouterPrerequis(?,?)");
			appCentrale.setString(1,codeUe);
			appCentrale.setString(2,codePrerequis);

			appCentrale.execute();
			System.out.println("L'ajout a reussi. Voila les informations du prerequis : " + codePrerequis + " est requis pour le cours " + codeUe);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally {
			System.out.println("\n");
		}
	}

	public void ajouterEtudiant() {
		System.out.println("Veuillez entrer le nom de l'etudiant.");
		String nomEtu = scanner.nextLine();
		System.out.println("Veuillez entrer le prenom de l'etudiant.");
		String prenomEtu = scanner.nextLine();
		System.out.println("Veuillez entrer l'email de l'etudiant.");
		String emailEtu = scanner.nextLine();
		System.out.println("Veuillez entrer le mot de passe de l'etudiant.");
		String mdpEtu = scanner.nextLine();
		System.out.println("Veuillez entrer le bloc de l'etudiant.");
		String blocEtu = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet2021.ajouterEtudiant(?,?,?,?,?)");
			appCentrale.setString(1,nomEtu);
			appCentrale.setString(2,prenomEtu);
			appCentrale.setString(3,emailEtu);
			appCentrale.setString(4,mdpEtu);
			appCentrale.setInt(5,Integer.parseInt(blocEtu));

			appCentrale.execute();
			System.out.println("L'ajout a reussi. Voila les informations de l'etudiant : " + nomEtu + " " + prenomEtu + " (Bloc : " + blocEtu + ")\nemail : "+ emailEtu + "(mdp : " + mdpEtu + ")");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally {
			System.out.println("\n");
		}
	}

	public void encoderUeValidee() {
		System.out.println("Veuillez entrer l'email de l'etudiant.");
		String emailEtu = scanner.nextLine();
		System.out.println("Veuillez entrer le code de l'UE validee.");
		String codeUe = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet2021.encoderUeValidee(?,?)");
			appCentrale.setString(1,emailEtu);
			appCentrale.setString(2,codeUe);

			appCentrale.execute();
			System.out.println("L'ajout a reussi. Voila les informations de l'Ue validee : " + emailEtu + " a valide le cours " + codeUe);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally {
			System.out.println("\n");
		}
	}

	public void visualiserEtudiantsDuBloc() {
		System.out.println("Entrez le bloc dont vous souhaitez voir les etudiants.");
		String blocEtu = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT nom, prenom, credits_pae"
					+ "	FROM projet2021.visualiserExamensBloc" + " WHERE bloc = " + blocEtu);
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getString(2) + " (PAE : " + rs.getInt(3) + " credits)");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}

	public void visualiserCreditsPAEEtudiant() {

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT nom, prenom, bloc, credits_pae"
					+ "	FROM projet2021.visualiserCreditsPAEEtudiant");
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getString(2) + " (Bloc " + rs.getInt(3) + ") (PAE : " + rs.getInt(4) + " credits)");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}

	public void visualiserEtudiantsPAEPasValide() {

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT nom, prenom, bloc, credits_valides"
					+ "	FROM projet2021.visualiserEtudiantsPAEPasValide");
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getString(2) + " (Credits valides : " + rs.getInt(3) + " credits)");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}

	public void visualiserUesDuBloc() {
		System.out.println("Entrez le bloc dont vous souhaitez voir les ues.");
		String blocEtu = scanner.nextLine();

		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT code_ue, nom, nbr_inscrits"
					+ "	FROM projet2021.visualiserUesDuBloc" + " WHERE bloc = " + blocEtu);
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(1) + " : " + rs.getString(2) + " (Inscrits : " + rs.getInt(3) + ")");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}
	
	public void afficherMenu() {
		
		System.out.println("----------------------------");
		System.out.println("-------Menu principal-------");
		System.out.println("----------------------------");
		System.out.println("[0] Quitter l'application");
		System.out.println("[1] Ajouter une UE.");
		System.out.println("[2] Ajouter un prerequis a un cours.");
		System.out.println("[3] Ajouter un etudiant.");
		System.out.println("[4] Encoder une UE validee a un etudiant.");
		System.out.println("[5] Visualiser les etudiants d'un bloc.");
		System.out.println("[6] Visualiser les credits du pae de chaque etudiants.");
		System.out.println("[7] Visualiser les etudiants avec un PAE pas encore valide");
		System.out.println("[8] Visualiser les UEs d'un bloc.");
	}

}