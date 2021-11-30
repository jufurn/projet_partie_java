package appUtilisateur;

import java.sql.*;
import java.util.Scanner;

public class AppUtilisateurMethodes {
    Scanner scanner = new Scanner(System.in);
    String url;
    Connection con;

    PreparedStatement appUtilisateur;
    ResultSet res;

    int id_etudiant = -1;

    public AppUtilisateurMethodes() {
        // creer connection
        url = "jdbc:postgresql://localhost:5432/postgres";
        con = null;

        try {
            con = DriverManager.getConnection(url, "postgres", "");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean connection(){
        System.out.println("Donnez votre email :");
        String _email = scanner.nextLine();
        System.out.println("Donner votre mot de passe : ");
        String _mdp = scanner.nextLine();
        try {
            appUtilisateur = con.prepareStatement("SELECT id_etudiant FROM projet2021.etudiants " +
                    "WHERE email = ? AND mot_de_passe = ?");
            appUtilisateur.setString(1, _email);
            appUtilisateur.setString(2, _mdp);
            res = appUtilisateur.executeQuery();
            if (res.next()) {
                id_etudiant = res.getInt(1);
                System.out.println("Connection réussie !");
                return true;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
        System.out.println("Connection échouée : email ou mot de passe incorrect !\n");
        return false;
    }

    public void ajouterUeAuPAE(){
        System.out.println("Quel Ue souhaitez vous ajouter ? (Donnez le code)");
        String code = scanner.nextLine();
        try{
            appUtilisateur = con.prepareStatement("SELECT projet2021.ajouterUeEtudiant(?,?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.setString(2,code);
            res = appUtilisateur.executeQuery();
            System.out.println("Ajout réussit !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }

    public void enleverUeAuPAE(){
        System.out.println("Quel Ue souhaitez vous enlever ? (Donnez le code)");
        String code = scanner.nextLine();
        try{
            appUtilisateur = con.prepareStatement("SELECT projet2021.enleverUEAPAEEtudiant(?,?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.setString(2,code);
            res = appUtilisateur.executeQuery();
            System.out.println("Retrait réussit !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void validerPAE(){
        try{
            appUtilisateur = con.prepareStatement("SELECT projet2021.validerPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Validation réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void visualiserUesAjoutables(){
        try {
            appUtilisateur = con.prepareStatement("SELECT code_ue, nom_ue " +
                    "FROM projet2021.VisualiserUeAjoutable WHERE id = ?");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("code_ue : "+res.getString(1)+" nom_ue : "+res.getString(2));
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void visualiserPAEEtudiant(){
        try {
            appUtilisateur = con.prepareStatement("SELECT code_ue, nom_ue, credit_ue, bloc_ue" +
                    " FROM projet2021.PAEEtudiant WHERE id = ?");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("code_ue : "+ res.getString(1));
            System.out.println("nom_ue : "+ res.getString(2));
            System.out.println("credit_ue : "+ res.getInt(3));
            System.out.println("bloc_ue : "+ res.getInt(4));
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void reinitialiserPAEEtudiant(){
        try {
            appUtilisateur = con.prepareStatement("SELECT projet2021.reinitialiserPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Réinitialisation du PAE réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }

    public void afficherMenu() {
        System.out.println("----------------------------");
        System.out.println("-------Menu utilisateur-------");
        System.out.println("----------------------------");
        System.out.println("[0] Quitter l'application");
        System.out.println("[1] Ajouter une UE au PAE.");
        System.out.println("[2] enlever une Ue au PAE.");
        System.out.println("[3] Valider le PAE.");
        System.out.println("[4] Visualiser les Ues ajoutables.");
        System.out.println("[5] Visualiser le PAE.");
        System.out.println("[6] Réinitialiser le PAE.");
    }
}
