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
					+ "	FROM projet.visualiserExamensBloc" + " WHERE bloc = " + blocEtu);
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
















	public void encoderHeureDebut(){
		
		System.out.println("Veuillez entrer le code d'examen.");
		
		String codeExamen = scanner.nextLine();
		System.out.println("Veuillez entrer l'heure de debut. (format : yyyy-mm-dd hh:mm:ss )");
		String heureDebut = scanner.nextLine();
		
		try {
			PreparedStatement 	appCentrale = con.prepareStatement("SELECT projet.encoderHeureDebutExamen(?,?::timestamp)");
			
			appCentrale.setString(1, codeExamen);
			appCentrale.setString(2, heureDebut);
			appCentrale.execute();
			System.out.println("L'encodage a r�ussi. Voil� les informations de l'encodage : " + codeExamen+" "+" "+ heureDebut);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}
	
	public void reserverLocal() {
		System.out.println("Veuillez entrer le nom du local.");
		String nomLocal = scanner.nextLine();

		System.out.println("Veuillez entrer le code examen");
		String codeExamen = scanner.nextLine();
		
	
		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT id_local FROM projet.locaux  WHERE nom_local = ?");
			appCentrale.setString(1, nomLocal);
			ResultSet rs = appCentrale.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Le local entre n'existe pas. Veuillez le rentrer en pleines lettres (ex. : \"A024\")");
			}
			
			appCentrale = con.prepareStatement("SELECT projet.reserverLocalPourExamen(?,?)");
			appCentrale.setString(1, nomLocal);
			appCentrale.setString(2, codeExamen);
			appCentrale.execute();
			System.out.println("La r�servation a r�ussi. Voil� les d�tailles. : " + nomLocal +" "+ codeExamen);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			
		}finally {
			System.out.println("\n");
		}
	}
	
	public void visualiserExamenUnBloc() {
		
		System.out.println("Entrez le bloc dont examnes vous souhaitez voir. (ex : \"Bloc 1\")");
		String blocString = scanner.nextLine();
	
	
		
		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT b.id_bloc FROM projet.blocs b WHERE b.code_bloc = ?");
			appCentrale.setString(1, blocString);
			ResultSet rs = appCentrale.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Le bloc entre n'existe pas. Veuillez le rentrer en pleines lettres (ex. : \"Bloc 1\")");
			}
			
			int blocInt = rs.getInt(1);
			
			appCentrale = con.prepareStatement("SELECT \"heure_debut\", \"code_examen\", \"nom\", \"nombre_locaux\"\r\n"
					+ "	FROM projet.visualiserExamensBloc\r\n"+ "WHERE \"bloc\"="+blocInt);
			rs = appCentrale.executeQuery();
			while(rs.next()) {
	          System.out.println("--------------------");
	          System.out.println("Heure d�but : " + rs.getString(1));
	          System.out.println("code examen : " + rs.getString(2));
	          System.out.println("nom d'examen : " + rs.getString(3));
	          System.out.println("nombre de local r�serv� : " + rs.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}
	
	 

	
	public void visualiserToutsExamensUnLocal() {
		System.out.println("Veuillez entrer le nom du local.");
		String nomLocal = scanner.nextLine();
		try {
			
			PreparedStatement appCentrale = con.prepareStatement("SELECT id_local FROM projet.locaux WHERE nom_local = ?");
			appCentrale.setString(1, nomLocal);
			ResultSet rs = appCentrale.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Le local entre n'existe pas. Veuillez le rentrer en pleines lettres (ex. : \"A024\")");
			}
			
			int idLocal = rs.getInt(1);
			
			
			appCentrale = con.prepareStatement("SELECT \"heure_debut\",\"code_examen\",\"nom\",\"id_local\"\r\n"
					+ "FROM projet.visualiserExamensLocal\r\n"
					+ "WHERE \"id_local\"="+idLocal);
			 rs = appCentrale.executeQuery();
			while(rs.next()) {
	          System.out.println("--------------------");
	          System.out.println("Heure d�but : " + rs.getString(1));
	          System.out.println("code examen : " + rs.getString(2));
	          System.out.println("nom d'examen : " + rs.getString(3));
	          System.out.println("id local: " + rs.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
	}
	
	public void visualiserExamenPasCompletementReserves() {
		
		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT \"code_examen\",\"nom\",\"bloc\",\"sur_machine\",\"heure_debut\",\"duree\"\r\n"
					+ "		FROM projet.visualiserExamensPasCompletementReserves");
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
	          System.out.println("--------------------");
	          System.out.println("code d'examen : " + rs.getString(1));
	          System.out.println("nom d'examen : " + rs.getString(2));
	          System.out.println("bloc : " + rs.getString(3));
	          System.out.println("sur machine : " + rs.getBoolean(4));
	          System.out.println("heure d�but : "+rs.getString(5));
	          System.out.println("dur�e : "+rs.getString(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}finally {
			System.out.println("\n");
		}
		
	}
	
	public void visualiserNbrExamenPasCompletementReserve() {
		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT \"code_bloc\",\"examens_non_reserves\"\r\n"
					+ "		FROM projet.visualiserExamensPasCompletementReservesParBloc");
			ResultSet rs = appCentrale.executeQuery();
			while(rs.next()) {
	          System.out.println("--------------------");
	          System.out.println("code du bloc : " + rs.getString(1));
	          System.out.println("nombre d'examen non r�serv� : " + rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		System.out.println("[1] Ajouter un local.");
		System.out.println("[2] Ajouter un examen.");
		System.out.println("[3] Encoder heure de d�but d'un examen.");
		System.out.println("[4] Reserver un local pour un examen.");
		System.out.println("[5] Visualiser les examens d'un bloc.");
		System.out.println("[6] Visualiser touts les examens d'un local.");
		System.out.println("[7] Visualiser les examens qui ne sont pas compl�tement r�serv�s");
		System.out.println("[8] Visualiser le nombre d�examens pas encore compl�tement r�serv�s pour chaque bloc.");
	}

}
