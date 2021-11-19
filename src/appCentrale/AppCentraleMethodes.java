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
			con = DriverManager.getConnection(url, "postgres", "Zavo00041504349");
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ajouterLocal() {
		Boolean presenceMachine = null;
		System.out.println("Veuillez entrer le nom de local.");
		String nomLocal = scanner.nextLine();
		System.out.println("Veuillez entrer le nombre de place.");
		int place = scanner.nextInt();
		scanner.nextLine();
		System.out.println("Veuillez sp�cifier si le local poss�de de machines.(TRUE ou FALSE)");
		String machine =scanner.nextLine();
		
		if(machine.contentEquals("FALSE")) {
			presenceMachine = false;
		}else {
			presenceMachine = true;
		}
	
		
		try {
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet.ajouterLocal(?,?,?)");
			appCentrale.setString(1,nomLocal);
			appCentrale.setInt(2,place);
			appCentrale.setBoolean(3, presenceMachine);
			
			appCentrale.execute();
			System.out.println("L'ajout a r�ussi. Voil� les informations du local : " + nomLocal +" "+ place +" "+ presenceMachine);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally {
			System.out.println("\n");
		}
	}
	
	public void ajouterExamen() {
		Boolean presenceMachine = null;
		System.out.println("Veuillez entrer le code d'examen.");
		String codeExamen = scanner.nextLine();
		System.out.println("Veuillez entrer le nom d'examen");
		String nom = scanner.nextLine();
		System.out.println("Veuillez entrer le bloc d'examen. (ex : \"Bloc 1\")");
		String blocString = scanner.nextLine();
		
		System.out.println("Veuillez sp�cifier si le local poss�de de machines.(TRUE ou FALSE)");
		String machine = scanner.nextLine();
		
		
		if(machine.contentEquals("FALSE")) {
			presenceMachine = false;
		}else {
			presenceMachine = true;
		}
	
		
		System.out.println("Veuillez sp�cifier la dur�e de l'examen.(minutes)");
		
		String duree = scanner.nextLine();
	
		
		try {
			
			PreparedStatement appCentrale = con.prepareStatement("SELECT projet.insererExamen(?,?,?,?,?::interval)");
			appCentrale.setString(1, codeExamen);
			appCentrale.setString(2, nom);
			appCentrale.setString(3, blocString);
			appCentrale.setBoolean(4, presenceMachine);
			appCentrale.setString(5,duree+"minutes");
			
			appCentrale.execute();
			System.out.println("L'ajout a r�ussi. Voil� les informations de l'examen : " + codeExamen +" "+ nom +" "+" "+ blocString +" "+ presenceMachine+" "+duree);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
