package com.lectorsrate.logic;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {

    private final String url = "jdbc:postgresql://localhost:5432/lectors_rate";
    private final String username = "postgres";
    private final String password = "admin";

    private Connection con;

    private String findUser = "SELECT users.username, users.email " +
            "FROM users WHERE users.username LIKE ? AND users.password LIKE ? " +
            "GROUP BY users.username, users.email LIMIT 1;";
    
    private String findUniversityID = "SELECT university.id FROM university WHERE university.name LIKE ?;";
    private String findFacultyID = "SELECT faculty.id FROM faculty WHERE faculty.name LIKE ?;";
    
    private String checkExistUsername = "SELECT users.id " +
            " FROM users WHERE users.username LIKE ?;";
    
    private String createUser = "INSERT INTO users (\"id\", \"fullname\", \"username\", \"password\", \"email\") " +
            " VALUES(nextval('gen_id'), ?, ?, ?, ?);";
    
    
    private String checkExistLector = "SELECT lectors.fullname FROM lectors WHERE lectors.fullname LIKE ?";
    private String checkExistUniversity = "SELECT university.id FROM university WHERE university.name LIKE ?;";
    private String checkExistFaculty = "SELECT faculty.id FROM faculty WHERE faculty.name LIKE ?;";
    
    private String createUniversity = "INSERT INTO university (\"id\", \"name\") VALUES(nextval('gen_university_id'), ?);";
    private String createFaculty = "INSERT INTO faculty (\"id\", \"name\", \"university_id\") VALUES(nextval('gen_faculty_id'), ?, ?);";
    private String createLector = "INSERT INTO lectors "
            + "(\"id\",\"fullname\",\"university_id\",\"faculty_id\",\"degree\",\"subjects\",\"goals\",\"photo\") "
            + " VALUES(nextval('gen_lectors_id'), ?,?,?,?,?,?,?)";
    
    private String loadLectorsInf = "SELECT lectors.id, lectors.fullname, faculty.name, university.name \n"
            + "FROM lectors INNER JOIN faculty ON(lectors.faculty_id = faculty.id) "
            + "INNER JOIN university ON(faculty.university_id = university.id) ";
    private String loadPhoto = "SELECT lectors.photo FROM lectors WHERE lectors.id = ?;";

    public DatabaseConnector() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User login(String userName, String userPass) {
        User user = null;
        try {
            PreparedStatement st = con.prepareStatement(findUser);
            st.setString(1, userName);
            st.setString(2, userPass);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
            }
            st.close();
        } catch (SQLException e) {
            System.err.println(con == null ? "connection is null" : "connection is init");
            e.printStackTrace();
        }
        return user;
    }
    
    public String signup(String fullname, String username, String password, String email) {
        try {
            PreparedStatement st = con.prepareStatement(checkExistUsername);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                st = con.prepareStatement(createUser);
                st.setString(1, fullname);
                st.setString(2, username);
                st.setString(3, password);
                st.setString(4, email);
                st.executeUpdate();
                return null;
            } return "User with the '" + username + "' username already exist. Please choose another username.";
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return "Error database. Please check logs server.";
        }
    }
    
    public String createLector(String[] fields, byte[] photo) {
        try {
            String fullname = fields[0] + " " + fields[1] + " " + fields[2];
            PreparedStatement st = con.prepareStatement(checkExistLector);
            st.setString(1, fullname);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                return insertLector(fields, fullname, photo);
            } else return "Lector " + fields[1] + " " + fields[0] + " " + fields[2] + " already exist";
        } catch (SQLException e) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, e);
            return "Error database. Please check logs server.";
        }
    }
    
    public ResultSet loadLectors(String searchQuery) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(loadLectorsInf +
                    (searchQuery != null ? " WHERE lectors.fullname LIKE '%" + searchQuery + "%';" : "") );
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public byte[] loadLectorPhoto(int id) {
        try {
            PreparedStatement st = con.prepareStatement(loadPhoto);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBytes("photo");
            } else return null;
        } catch (SQLException e) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private String insertLector(String[] fields, String fullname, byte[] photo) throws SQLException {
        int idUniversity;
        int idFaculty;
        PreparedStatement st;
        idUniversity = checkExistAndCreateUniversity(fields);
        idFaculty = checkExistAndCreateFaculty(fields, idUniversity);
        st = con.prepareStatement(createLector);
        st.setString(1, fullname);
        st.setInt(2, idUniversity);
        st.setInt(3, idFaculty);
        st.setString(4, fields[5]);
        st.setString(5, fields[6]);
        st.setString(6, fields[7]);
        st.setBytes(7, photo);
        st.executeUpdate();
        return "Lector " + fields[1] + " " + fields[0] + " " + fields[2] + " successfully created.";
    }

    private int checkExistAndCreateUniversity(String[] fields) throws SQLException {
        int idUniversity;
        //Checking for existing University
        PreparedStatement st = con.prepareStatement(checkExistUniversity);
        st.setString(1, fields[3]);
        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            st = con.prepareStatement(createUniversity);
            st.setString(1, fields[3]);
            st.executeUpdate();
            //find university id for create new faculty(id, name, university_id)
            st = con.prepareStatement(findUniversityID);
            st.setString(1, fields[3]);
            rs = st.executeQuery();
            rs.next();
        }
        idUniversity = rs.getInt("id");
        rs.close();
        return idUniversity;
    }

    private int checkExistAndCreateFaculty(String[] fields, int idUniversity) throws SQLException {
        PreparedStatement st;
        ResultSet rs;
        int idFaculty;
        //Checking for existing Faculty
        st = con.prepareStatement(checkExistFaculty);
        st.setString(1, fields[4]);
        rs = st.executeQuery();
        if (!rs.next()) {
            st = con.prepareStatement(createFaculty);
            st.setString(1, fields[4]);
            st.setInt(2, idUniversity);
            st.executeUpdate();
            //find faculty id for create new lector(id, name, university_id, faculty_id, goals ...etc)
            st = con.prepareStatement(findFacultyID);
            st.setString(1, fields[4]);
            rs = st.executeQuery();
            rs.next();
        }
        idFaculty = rs.getInt("id");
        rs.close();
        return idFaculty;
    }
}
