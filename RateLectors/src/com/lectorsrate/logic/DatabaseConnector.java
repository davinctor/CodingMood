package com.lectorsrate.logic;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {

    private final String url = "jdbc:postgresql://localhost:5432/lectors_rate";
    private final String username = "postgres";
    private final String password = "admin";

    private Connection con;

    private String findUser = "SELECT users.id, users.admin " +
            "FROM users WHERE users.username LIKE ? AND users.password LIKE ? " +
            "GROUP BY users.id LIMIT 1;";
    
    private String findUniversityID = "SELECT university.id FROM university WHERE university.name LIKE ?;";
    private String findFacultyID = "SELECT faculty.id FROM faculty WHERE faculty.name LIKE ?;";
    
    private String checkExistUsername = "SELECT users.id " +
            " FROM users WHERE users.username LIKE ?;";
    
    private String createUser = "INSERT INTO users (\"id\", \"fullname\", \"username\", \"password\", \"email\", \"photo\") " +
            " VALUES(nextval('gen_id'), ?, ?, ?, ?, ?);";
    
    
    private String checkExistLector = "SELECT lectors.fullname FROM lectors WHERE lectors.fullname LIKE ?";
    private String checkExistUniversity = "SELECT university.id FROM university WHERE university.name LIKE ?;";
    private String checkExistFaculty = "SELECT faculty.id FROM faculty WHERE faculty.name LIKE ?;";
    
    private String createUniversity = "INSERT INTO university (\"id\", \"name\") VALUES(nextval('gen_university_id'), ?);";
    private String createFaculty = "INSERT INTO faculty (\"id\", \"name\", \"university_id\") VALUES(nextval('gen_faculty_id'), ?, ?);";
    private String createLector = "INSERT INTO lectors "
            + "(\"id\",\"fullname\",\"university_id\",\"faculty_id\",\"degree\",\"subjects\",\"goals\",\"photo\", \"date_add\") "
            + " VALUES(nextval('gen_lectors_id'), ?,?,?,?,?,?,?, current_date)";
    
    private String updateLectorPart1 = "UPDATE lectors SET "
    		+ "fullname = ?, university_id = ?, faculty_id = ?, degree = ?,"
    		+ "subjects = ?, goals = ?, date_add = current_date ";
    private String updateLectorPart2 = ", photo = ? ";
    private String updateLectorPart3 = " WHERE id = ?;";
    
    private String loadLectorsInf = "SELECT lectors.id, lectors.fullname, faculty.name, university.name \n"
            + "FROM lectors INNER JOIN faculty ON(lectors.faculty_id = faculty.id) "
            + "INNER JOIN university ON(faculty.university_id = university.id) "
            + "ORDER BY lectors.date_add asc, university.name, faculty.name, lectors.fullname;";
    private String loadLectorInf = "SELECT lectors.id, lectors.fullname, university.name, "
    		+ "faculty.name, lectors.degree, lectors.subjects, lectors.goals, lectors.rate "
    		+ "FROM lectors INNER JOIN university ON(lectors.university_id = university.id) "
    		+ "INNER JOIN faculty ON(lectors.faculty_id = faculty.id) "
    		+ "WHERE lectors.id = ?;";
    
    private String loadLectPhoto = "SELECT lectors.photo FROM lectors WHERE lectors.id = ?;";
    private String loadUsrPhoto = "SELECT users.photo FROM users WHERE users.id = ?;";
    
    private String loadComments = "SELECT users.id, users.username, rate.mark, comments.comment, comments.date_comment "
    		+ "FROM comments LEFT JOIN users ON(comments.user_id = users.id) "
    		+ "LEFT JOIN rate ON(rate.user_id = comments.user_id) "
    		+ "WHERE comments.lector_id = ? AND comments.lector_id = rate.lector_id;";
    
    private String loadRatingLectorByUser = "SELECT rate.user_id, rate.mark FROM rate "
    		+ "WHERE rate.user_id = ? AND rate.lector_id = ?;";
    
    private String createComment = "INSERT INTO comments (\"id\", \"user_id\", \"lector_id\", \"comment\", \"date_comment\") "
    		+ "VALUES(nextval('gen_comments_id'), ?, ?, ?, current_date);";
    
    
    private String checkExistRate = "SELECT * FROM rate WHERE user_id = ? AND lector_id = ?;";
    private String createRateLector = "INSERT INTO rate (\"id\", \"user_id\", \"lector_id\", \"mark\") "
    		+ "VALUES(nextval('gen_rate_id'), ?,?,?);";
    private String updateRateLector = "UPDATE rate SET mark = ? WHERE user_id = ? AND lector_id = ?;";
    
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
                user.setId(rs.getInt(1));
                user.setAdmin(rs.getBoolean(2));
            }
            st.close();
        } catch (SQLException e) {
            System.err.println(con == null ? "connection is null" : "connection is init");
            e.printStackTrace();
        }
        return user;
    }
    
    public String signup(String[] fields, byte[] photo) {
        try {
            PreparedStatement st = con.prepareStatement(checkExistUsername);
            st.setString(1, fields[3]);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                st = con.prepareStatement(createUser);
                st.setString(1, fields[0] + " " + fields[1]);
                st.setString(2, fields[3]);
                st.setString(3, fields[4]);
                st.setString(4, fields[2]);
                st.setBytes(5, photo);
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
    
    public void createComments(int userID, int lectorID, String comment) {
    	try {
            PreparedStatement st = con.prepareStatement(createComment);
            st.setInt(1, userID);
            st.setInt(2, lectorID);
            st.setString(3, comment);
            st.executeUpdate();
            rateLectors(userID, lectorID, 0);
        } catch (SQLException e) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public ResultSet loadLectors(String searchQuery) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(loadLectorsInf +
                    (searchQuery != null ? searchQuery : "") );
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /*public ResultSet findLector(String[] fields) {
    	String querySearch = " WHERE ";
    	querySearch += "lectors.fullname LIKE %" + fields[0] + "%";
    	int universityID = 0;
    	int facultyID = 0;
    	try {
			universityID = checkExistAndCreateUniversity(fields[1]);
			facultyID = checkExistAndCreateFaculty(fields[2], universityID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	if (universityID != 0) 
    		querySearch += " AND lectors.university_id = " + universityID + " ";
    	if (facultyID != 0)
    		querySearch += " AND lectors.faculty_id = " + facultyID + " ";
    	querySearch += " AND lectors.degree LIKE " + fields[3] + " ";
    	querySearch += " AND lectors.subjects LIKE " + fields[4] + " ";
    	querySearch += " AND lectors.goals LIKE " + fields[5] + " ";
    	return loadLectors(querySearch);
    }*/
    
    public ResultSet loadLector(int lectorID) {
    	try {
            PreparedStatement st = con.prepareStatement(loadLectorInf);
            st.setInt(1, lectorID);
            return st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet loadComments(int lectorID) {
    	try {
            PreparedStatement st = con.prepareStatement(loadComments);
            st.setInt(1, lectorID);
            return st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet loadRate(int userID, int lectorID) {
    	try {
            PreparedStatement st = con.prepareStatement(loadRatingLectorByUser);
            st.setInt(1, userID);
            st.setInt(2, lectorID);
            return st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public byte[] loadLectorPhoto(int id) {
        try {
            PreparedStatement st = con.prepareStatement(loadLectPhoto);
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
    
    public byte[] loadUserPhoto(int id) {
        try {
            PreparedStatement st = con.prepareStatement(loadUsrPhoto);
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
        idUniversity = checkExistAndCreateUniversity(fields[3]);
        idFaculty = checkExistAndCreateFaculty(fields[4], idUniversity);
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

    private int checkExistAndCreateUniversity(String university) throws SQLException {
        int idUniversity;
        //Checking for existing University
        PreparedStatement st = con.prepareStatement(checkExistUniversity);
        st.setString(1, university);
        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            st = con.prepareStatement(createUniversity);
            st.setString(1, university);
            st.executeUpdate();
            //find university id for create new faculty(id, name, university_id)
            st = con.prepareStatement(findUniversityID);
            st.setString(1, university);
            rs = st.executeQuery();
            rs.next();
        }
        idUniversity = rs.getInt("id");
        rs.close();
        return idUniversity;
    }

    private int checkExistAndCreateFaculty(String faculty, int idUniversity) throws SQLException {
        PreparedStatement st;
        ResultSet rs;
        int idFaculty;
        //Checking for existing Faculty
        st = con.prepareStatement(checkExistFaculty);
        st.setString(1, faculty);
        rs = st.executeQuery();
        if (!rs.next()) {
            st = con.prepareStatement(createFaculty);
            st.setString(1, faculty);
            st.setInt(2, idUniversity);
            st.executeUpdate();
            //find faculty id for create new lector(id, name, university_id, faculty_id, goals ...etc)
            st = con.prepareStatement(findFacultyID);
            st.setString(1, faculty);
            rs = st.executeQuery();
            rs.next();
        }
        idFaculty = rs.getInt("id");
        rs.close();
        return idFaculty;
    }
    
    public void rateLectors(int userID, int lectorID, int mark) {
    	try {
            PreparedStatement st = con.prepareStatement(checkExistRate);
            st.setInt(1, userID);
            st.setInt(2, lectorID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
            	st.close();
            	st = con.prepareStatement(updateRateLector);
            	st.setInt(1, mark);
            	st.setInt(2, userID);
            	st.setInt(3, lectorID);
            } else {
            	st.close();
            	st = con.prepareStatement(createRateLector);
            	st.setInt(1, userID);
            	st.setInt(2, lectorID);
            	st.setInt(3, mark);
            }
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String editLector(String[] fields, byte[] photo) {
    	try {
	    	int idUniversity;
	        int idFaculty;
	        PreparedStatement st;
	        idUniversity = checkExistAndCreateUniversity(fields[3]);
	        idFaculty = checkExistAndCreateFaculty(fields[4], idUniversity);
	        String query = updateLectorPart1 +
	        		(photo.length != 0 ? updateLectorPart2 : "") + updateLectorPart3;
	        String fullname = fields[0] + " " + fields[1] + " " + fields[2];
	        st = con.prepareStatement(query);
	        st.setString(1, fullname);
	        st.setInt(2, idUniversity);
	        st.setInt(3, idFaculty);
	        st.setString(4, fields[5]);
	        st.setString(5, fields[6]);
	        st.setString(6, fields[7]);
	        if (photo.length != 0) {
	        	st.setBytes(7, photo);
	        	st.setInt(8, Integer.parseInt(fields[8]));
	        }
	        else st.setInt(7, Integer.parseInt(fields[8]));
	        st.executeUpdate();
	        return "Lector " + fullname + " successfully edited.";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "Error database. Please check logs server.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error database. Please check logs server.";
		}
    }
    
}
