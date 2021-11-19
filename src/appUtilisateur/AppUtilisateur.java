package appUtilisateur;

import java.sql.*;
import java.util.Scanner;
import bcrypt.BCrypt;
import org.postgresql.util.PSQLException;

public class AppUtilisateur {

    public static void main(String[] args) throws SQLException {
    	
    	Scanner scanner = new Scanner(System.in);
    	
    	//creer une connexion avec la db
    	String url = "jdbc:postgresql://localhost:5432/projet";
		Connection conn = null;
		
		try {
			
			Class.forName("org.postgresql.Driver"); //forcer le chargement du Driver
			conn = DriverManager.getConnection(url, "postgres", "Zavo00041504349"); //se connecter a la db
			
			int id_etudiant = -1;
			int action = -1;
			PreparedStatement ps;
			ResultSet rs;
			
			//temps que pas authentifie, on s'inscrit ou on se connecte
			while(id_etudiant == -1 && action != 0) {
				//proposer si se connecter ou s'inscrire
				System.out.println("-----------------------------");
				System.out.println("-- Application utilisateur --");
				System.out.println("-----------------------------");
				System.out.println();
				System.out.println("0 : Quitter");
				System.out.println("1 : S'inscrire");
				System.out.println("2 : Se connecter");
				//System.out.println("[3 : Inscrire Damas, Cambron et Ferneeuw pour demo]");
				action = scanner.nextInt();
				scanner.nextLine();
				
				//s'inscrire si demande
				if(action == 1) {
					System.out.println("-----------------------------");
					System.out.println("--       S'inscrire        --");
					System.out.println("-----------------------------");
					System.out.println();
					System.out.println("Entrez votre e-mail :");
					String inscriptionMail = scanner.nextLine();
					System.out.println("Entrez votre nom d'utilisateur :");
					String inscriptionNom = scanner.nextLine();
					System.out.println("Entrez votre mot de passe :");
					String inscriptionMdp = scanner.nextLine();
					System.out.println("Entrez le nom de votre bloc :");
					String inscriptionBloc = scanner.nextLine();
					
					//convertion du bloc entre en int de reference du bloc
					ps = conn.prepareStatement("SELECT b.id_bloc FROM projet.blocs b WHERE b.code_bloc = ?");
					ps.setString(1, inscriptionBloc);
					rs = ps.executeQuery();
					
					if(!rs.next()) {
						System.out.println("Le bloc entre n'existe pas. Veuillez le rentrer en pleines lettres (ex. : \"Bloc 1\")");
					}
					else {
						int inscriptionGetBloc = rs.getInt(1);
						//bcrypt du mdp
						String sel = BCrypt.gensalt();
						String inscriptionMdpCrypt = BCrypt.hashpw(inscriptionMdp, sel);
						
						//try, car exception possible
						try {
							//envoi du statement afin d'inscrire l'utilisateur
							ps = conn.prepareStatement("SELECT projet.inscrireEtudiant(?, ?, ?, ?);");
							ps.setString(1, inscriptionNom);
							ps.setString(2, inscriptionMail);
							ps.setString(3, inscriptionMdpCrypt);
							ps.setInt(4, inscriptionGetBloc);
							ps.execute();
							
							//redirection
							action = 2;
							System.out.println("Vous vous etes bien inscrit");
						} catch(SQLException e) {
							System.out.println(e.getMessage());
						}
					}
				}
				
				//se connecter
				if(action == 2) {
					System.out.println("-----------------------------");
					System.out.println("--      Se connecter       --");
					System.out.println("-----------------------------");
					System.out.println();
					System.out.println("Entrez votre nom d'utilisateur :");
					String userName = scanner.nextLine();
					System.out.println("Entrez votre mot de passe :");
					String userMdp = scanner.nextLine();
					
					//recuperation du mdp
					ps = conn.prepareStatement("SELECT e.mot_de_passe FROM projet.etudiants e WHERE e.nom_etudiant = ?");
					ps.setString(1, userName);
					rs = ps.executeQuery();
					if(!rs.next()) {
						System.out.println("Mauvais nom d'utilisateur");
					}
					else {
						String userGetMdp = rs.getString(1);
						//se connecte si mdp est bon
						if(BCrypt.checkpw(userMdp, userGetMdp)) {
							ps = conn.prepareStatement("SELECT projet.identifierEtudiant(?, ?)");
							ps.setString(1, userName);
							ps.setString(2, userGetMdp);
							rs = ps.executeQuery();
							rs.next();
							id_etudiant = rs.getInt(1);
							System.out.println("Vous vous etes bien connecte");
						}
						else {
							System.out.println("Vous avez entre un mauvais mot de passe");
						}
					}
				}
				
				//inscrire Damas, Cambron et Ferneeuw pour demo
				if(action == 3) {
					String sel = BCrypt.gensalt();
					
					String inscriptionMdpCrypt = BCrypt.hashpw("mdpCambron", sel);
					ps = conn.prepareStatement("SELECT projet.inscrireEtudiant(?, ?, ?, ?);");
					ps.setString(1, "Cambron");
					ps.setString(2, "isabelle.cambron@student.vinci.be");
					ps.setString(3, inscriptionMdpCrypt);
					ps.setInt(4, 2);
					ps.execute();
					
					inscriptionMdpCrypt = BCrypt.hashpw("mdpDamas", sel);
					ps = conn.prepareStatement("SELECT projet.inscrireEtudiant(?, ?, ?, ?);");
					ps.setString(1, "Damas");
					ps.setString(2, "christophe.damas@student.vinci.be");
					ps.setString(3, inscriptionMdpCrypt);
					ps.setInt(4, 1);
					ps.execute();
					
					inscriptionMdpCrypt = BCrypt.hashpw("mdpFerneeuw", sel);
					ps = conn.prepareStatement("SELECT projet.inscrireEtudiant(?, ?, ?, ?);");
					ps.setString(1, "Ferneeuw");
					ps.setString(2, "stephanie.ferneeuw@student.vinci.be");
					ps.setString(3, inscriptionMdpCrypt);
					ps.setInt(4, 2);
					ps.execute();
					
					System.out.println("ok");
				}
			}
			
			//le menu de l'utilisateur connecte
			while(action != 0) {
				System.out.println("-----------------------------");
				System.out.println("-- Application utilisateur --");
				System.out.println("-----------------------------");
				System.out.println();
				System.out.println("0 : Quitter");
				System.out.println("1 : Visualiser les examens");
				System.out.println("2 : S'inscrire a un examen");
				System.out.println("3 : S'inscrire a tous les examens de votre bloc");
				System.out.println("4 : Voir votre horaire");
				action = scanner.nextInt();
				scanner.nextLine();
				
				//visualiser les examens
				if(action == 1) {
					ps = conn.prepareStatement("SELECT \"code_examen\",\"nom\",\"code_bloc\",\"duree\" FROM projet.etudiantVisualiserExamen");
					rs = ps.executeQuery();
					while(rs.next()) {
						System.out.println("--------------------");
						System.out.println("Code : " + rs.getString(1));
						System.out.println("Nom : " + rs.getString(2));
						System.out.println("Bloc : " + rs.getString(3));
						System.out.println("Duree : " + rs.getString(4));
					}
				}
				
				//s'inscrire a un examen
				if(action == 2) {
					System.out.println("Entrez le code de l'examen auquel vous houhaitez vous inscrire :");
					String userExam = scanner.nextLine();
					
					ps = conn.prepareStatement("SELECT e.code_examen FROM projet.examens e WHERE e.code_examen = ?");
					ps.setString(1, userExam);
					rs = ps.executeQuery();
					if(!rs.next()) {
						System.out.println("L'examen entre n'existe pas");
					}
					else {
						try {
							ps = conn.prepareStatement("SELECT projet.etudiantSInscrireAExamen(?, ?)");
							ps.setInt(1, id_etudiant);
							ps.setString(2, userExam);
							ps.execute();
							System.out.println("Vous vous etes bien inscrit a l'examen");
						} catch(SQLException e) {
							System.out.println(e.getMessage());
						}
					}
				}
				
				//s'inscrire a tous les examens du bloc
				if(action == 3) {
					try {
						ps = conn.prepareStatement("SELECT projet.etudiantInscrireExamensBloc(?)");
						ps.setInt(1, id_etudiant);
						ps.execute();
						System.out.println("Vous vous etes bien inscrit a tous les examens de votre bloc");
					} catch(SQLException e) {
						System.out.println(e.getMessage());
					}
				}
				
				//voir son horaire
				if(action == 4) {
					ps = conn.prepareStatement("SELECT \"id_etudiant\",\"code_examen\",\"nom\",\"bloc\",\"heure_debut\",\"heure_fin\",\"duree\",\"nom_local\" \r\n" + 
											   "FROM projet.etudiantVisualiserHoraire\r\n" + 
											   "WHERE \"id_etudiant\" = ?");
					ps.setInt(1, id_etudiant);
					rs = ps.executeQuery();
					while(rs.next()) {
						System.out.println("--------------------");
						System.out.println("Code : " + rs.getString(2));
						System.out.println("Nom : " + rs.getString(3));
						System.out.println("Bloc : " + rs.getString(4));
						System.out.println("Debut : " + rs.getString(5));
						System.out.println("Fin : " + rs.getString(6));
						System.out.println("Duree : " + rs.getString(7));
						System.out.println("Local : " + rs.getString(8));
					}
				}
			}
			
			System.out.println("Au revoir");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver PostgreSQL manquant !");
			System.exit(1);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Impossible de joindre le server !");
			System.exit(1);
		}
    }
}