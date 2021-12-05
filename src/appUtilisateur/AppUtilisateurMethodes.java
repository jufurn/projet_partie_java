package appUtilisateur;

import bcrypt.BCrypt;

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
        //url = "jdbc:postgresql://172.24.2.6:5432/dbjulienfurnelle";
        con = null;

        try {
            con = DriverManager.getConnection(url, "postgres", "");
            //con = DriverManager.getConnection(url, "julienfurnelle", "PLBGNMJ.9");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean connection(){
        System.out.println("Donnez votre email :");
        String _email = scanner.nextLine();
        System.out.println("Donner votre mot de passe : ");
        String mdp = scanner.nextLine();
        try {
            appUtilisateur = con.prepareStatement("SELECT id_etudiant, mot_de_passe FROM projet2021.etudiants " +
                    "WHERE email = ?");
            appUtilisateur.setString(1, _email);
            res = appUtilisateur.executeQuery();
            if (res.next()) {
                String mdpuser = res.getString(2);
                if (BCrypt.checkpw(mdp,mdpuser)){
                    id_etudiant = res.getInt(1);
                    System.out.println("Connection réussit !");
                    return true;
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
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
            appUtilisateur.execute();
            System.out.println("Ajout réussit !");
        }catch (SQLException e){
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
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
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
        }
    }
    public void validerPAE(){
        try{
            appUtilisateur = con.prepareStatement("SELECT projet2021.validerPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Validation réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
        }
    }
    public void visualiserUesAjoutables(){
        String[] splitedRes;
        try {
            appUtilisateur = con.prepareStatement("SELECT projet2021.VisualiserUeAjoutableParEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Vous avez acces à ces cours :");
            while (res.next()) {
                splitedRes = res.getString(1).split("[(,)]+");
                System.out.println(" Code : "+ splitedRes[1] +"     Nom : "+ splitedRes[2] +
                        "     Crédit : "+ splitedRes[3] +"     Bloc : "+ splitedRes[4]);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
        }
    }
    public void visualiserPAEEtudiant(){
        try {
            appUtilisateur = con.prepareStatement("SELECT code_ue, nom_ue, credit_ue, bloc_ue" +
                    " FROM projet2021.PAEEtudiant WHERE id = ?");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Voici votre PAE : \n");
            while (res.next()) {
                System.out.println("code_ue : " + res.getString(1));
                System.out.println("nom_ue : " + res.getString(2));
                System.out.println("credit_ue : " + res.getInt(3));
                System.out.println("bloc_ue : " + res.getInt(4));
                System.out.println("\n");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
        }
    }
    public void reinitialiserPAEEtudiant(){
        try {
            appUtilisateur = con.prepareStatement("SELECT projet2021.reinitialiserPAEEtudiant(?)");
            appUtilisateur.setInt(1,id_etudiant);
            res = appUtilisateur.executeQuery();
            System.out.println("Réinitialisation du PAE réussie !");
        }catch (SQLException e){
            System.out.println(e.getMessage().split("\n")[0]);
            //System.out.println(e.getMessage());
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
        System.out.println("[7] Déconnection / Changer d'utilisateur.");
    }
}
