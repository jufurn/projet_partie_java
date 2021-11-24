package appUtilisateur;

import java.sql.*;
import java.util.Scanner;

public class AppUtilisateurMethodes {
    Scanner scanner = new Scanner(System.in);
    String url;
    Connection con;

    public AppUtilisateurMethodes() {
        // creer connection
        url = "jdbc:postgresql://localhost:5432/projet";
        con = null;

        try {
            con = DriverManager.getConnection(url, "postgres", "Zavo00041504349");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public void ajouterUeAuPAE(int id_etudiant){
        System.out.println("Quel Ue souhaitez vous ajouter ? (Donnez le code)");
        String code = scanner.nextLine();
        try{
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT projet2021.ajouterUeEtudiant(?,?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.setString(2,code);
            appUtilisateur.execute();
            System.out.println("Ajout réussit !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }

    public void enleverUeAuPAE(int id_etudiant){
        System.out.println("Quel Ue souhaitez vous enlever ? (Donnez le code)");
        String code = scanner.nextLine();
        try{
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT projet2021.enleverUEAPAEEtudiant(?,?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.setString(2,code);
            appUtilisateur.execute();
            System.out.println("Retrait réussit !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void validerPAE(int id_etudiant){
        try{
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT projet2021.validerPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.execute();
            System.out.println("Validation réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void visualiserUesAjoutables(int id_etudiant){
        try {
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT code_ue, nom_ue " +
                    "FROM projet2021.VisualiserUeAjoutable WHERE id = ?");
            appUtilisateur.setInt(1,id_etudiant);
            ResultSet result = appUtilisateur.executeQuery();
            System.out.println("code_ue : "+result.getString(1)+" nom_ue : "+result.getString(2));
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void visualiserPAEEtudiant(int id_etudiant){
        try {
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT code_ue, nom_ue, credit_ue, bloc_ue" +
                    " FROM projet2021.PAEEtudiant WHERE id = ?");
            appUtilisateur.setInt(1,id_etudiant);
            ResultSet result = appUtilisateur.executeQuery();
            System.out.println("code_ue : "+ result.getString(1));
            System.out.println("nom_ue : "+ result.getString(2));
            System.out.println("credit_ue : "+ result.getInt(3));
            System.out.println("bloc_ue : "+ result.getInt(4));
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
    public void reinitialiserPAEEtudiant(int id_etudiant){
        try {
            PreparedStatement appUtilisateur = con.prepareStatement("SELECT projet2021.reinitialiserPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            appUtilisateur.execute();
            System.out.println("Réinitialisation du PAE réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            System.out.println("\n");
        }
    }
}
