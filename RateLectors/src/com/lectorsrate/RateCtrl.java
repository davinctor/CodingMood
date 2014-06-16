package com.lectorsrate;

import com.lectorsrate.logic.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/rate/")
public class RateCtrl extends javax.servlet.http.HttpServlet {

    private User user;
    private DatabaseConnector connector;

    @Override
    public void init() {
        try {
            super.init();
            log("init is success");
            connector = new DatabaseConnector();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private void setForward(String pagePath, HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(pagePath);
        try {
            dispatcher.forward(request, response);
        } catch(ServletException e) {
            log("setForward : ServletException" + request.getQueryString());
        } catch(IOException e) {
            log("setForward : IOException" + request.getQueryString());
        }
    }

    private void setAttribute(String name, Object obj) {
        getServletContext().setAttribute(name, obj);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("login") != null && user == null) {
            loginUser(request, response);
        } else if (request.getParameter("comment") != null && user != null) {
        	createComment(request, response);
        } else  if (request.getParameter("search") != null && user != null) {
        	//findUser(request, response);
    	} else if (ServletFileUpload.isMultipartContent(request)) {
			try {
				FileItemFactory factory = new DiskFileItemFactory();
	            ServletFileUpload upload = new ServletFileUpload(factory);
	            List<FileItem> items = upload.parseRequest(request);
	            Iterator<FileItem> iter = items.iterator();
	            if (user != null)
	            	while (iter.hasNext()) {
	                	FileItem item = (FileItem) iter.next();
	                	if (item.isFormField() && item.getFieldName().equals("editlector")) {
	                		editLector(request, response, items.iterator());
	                		return;
	                	}
	                	if (item.isFormField() && item.getFieldName().equals("addlector")) {
	                		addLector(request, response, items.iterator());
	                		return;
	                	}
	            	}
	            else
	            	addUser(request, response, iter);
			} catch (FileUploadException e) {
					e.printStackTrace();
			}

        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        user = connector.login(username, password);
        if (user == null) {
            setAttribute("message", "Incorrect username or password.");
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/?login");
            setForward("/info.jspx", request, response);
        } else
        	goHome(request, response);
    }
    
    private void createComment(HttpServletRequest request, HttpServletResponse response) {
    	int userID = Integer.parseInt(request.getParameter("user"));
    	int lectorID = Integer.parseInt(request.getParameter("lector"));
    	String comment = request.getParameter("msg");
    	connector.createComments(userID, lectorID, comment);
    	lectorPage(lectorID, request, response);
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response,
    		Iterator<FileItem> iter) {
        String[] fields = new String[5];
        byte[] photo = null;
        int i = 0;
        while (iter.hasNext()) {
        	FileItem item = (FileItem) iter.next();
        	if (item.isFormField()) {
        		String fieldName = item.getFieldName();
        		if     (fieldName.equals("firstname") || fieldName.equals("lastname") ||
        				fieldName.equals("email") || fieldName.equals("username") ||
        				fieldName.equals("password") ) {
        					fields[i] = item.getString();
        					i++;
        		}
        	} else photo = item.get();
        }
        String result = connector.signup(fields, photo);
        if (result == null) {
            setAttribute("message", "Registration is successful, " + fields[3] + ".");
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/?login");
            setForward("/info.jspx", request, response);
        } else {
            setAttribute("message", result);
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/?signup");
            setForward("/info.jspx", request, response);
        }
    }

    private void addLector(HttpServletRequest request, HttpServletResponse response, Iterator<FileItem> iter) {
        String[] fields = new String[8];
        byte[] photo = null;
        int i = 0;
        while (iter.hasNext()) {
        	FileItem item = (FileItem) iter.next();
        	if (item.isFormField()) {
        		String fieldName = item.getFieldName();
        		if     (fieldName.equals("firstname") || fieldName.equals("lastname") ||
        				fieldName.equals("patronymic") || fieldName.equals("university") ||
        				fieldName.equals("faculty") || fieldName.equals("degree") ||
        				fieldName.equals("subjects") || fieldName.equals("goals") ) {
        					fields[i] = item.getString();
        					i++;
        		}
        	} else photo = item.get();
        }
        log(photo == null ? "photo is null" : "photo is not null");
        String result = connector.createLector(fields, photo);
        setAttribute("message", result);
        setAttribute("linkBack", "http://localhost:8080/Rate/rate/?addnewlector");
        setForward("/info.jspx", request, response);
    }
    
    private void editLector(HttpServletRequest request, HttpServletResponse response, Iterator<FileItem> iter) {
        String[] fields = new String[9];
        byte[] photo = null;
        int i = 0;
        while (iter.hasNext()) {
        	FileItem item = (FileItem) iter.next();
        	if (item.isFormField()) {
        		String fieldName = item.getFieldName();
        		if     (fieldName.equals("firstname") || fieldName.equals("lastname") ||
        				fieldName.equals("patronymic") || fieldName.equals("university") ||
        				fieldName.equals("faculty") || fieldName.equals("degree") ||
        				fieldName.equals("subjects") || fieldName.equals("goals")  ||
        				fieldName.equals("editlector") ) {
        					fields[i] = item.getString();
        					i++;
        		}
        	} else photo = item.get();
        }
        log("photo is  " + photo.length);
        String result = connector.editLector(fields, photo);
        setAttribute("message", result);
        setAttribute("linkBack", "http://localhost:8080/Rate/rate/?editlector="
        		+ Integer.parseInt(fields[8]));
        setForward("/info.jspx", request, response);
    }
    
    private void findLector(HttpServletRequest request, HttpServletResponse response) {
    	String[] fields = new String[6];
    	for(int i = 0; i < fields.length; i++)
    		fields[i] = "";
    	fields[0] = request.getParameter("fullname");
    	fields[1] = request.getParameter("university");
    	fields[2] = request.getParameter("faculty");
    	fields[3] = request.getParameter("degree");
    	fields[4] = request.getParameter("subjects");
    	fields[5] = request.getParameter("goals");
    	ResultSet rs = null;/*connector.findLector(fields);*/
    	setAttribute("rs", rs);
        setAttribute("user", user);
        setAttribute("isAdmin", user.isAdmin());
        setForward("/home.jsp", request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            if (request.getParameter("image") != null) {
                response.setContentType("image/jpeg");
                ServletOutputStream os = response.getOutputStream();
                byte[] data = connector.loadLectorPhoto(Integer.parseInt(request.getParameter("image")));
                os.write(data);
            } else if (request.getParameter("imageuser") != null) {
            	response.setContentType("image/jpeg");
                ServletOutputStream os = response.getOutputStream();
                byte[] data = connector.loadUserPhoto(Integer.parseInt(request.getParameter("imageuser")));
                os.write(data);
            } else {
                HttpSession session = request.getSession(false);

                if (user == null && request.getParameter("signup") != null) {
                    setForward("/signup.jspx", request, response);
                    return;
                }
                if (session == null || user == null || request.getParameter("login") != null) {
                    setForward("/login.jspx", request, response);
                    return;
                }
                if (request.getParameter("addnewlector") != null) {
                	if (user.isAdmin())
                		setForward("/addlector.jspx", request, response);
                	else {
                		setAttribute("message", "Access denied.");
                        setAttribute("linkBack", "http://localhost:8080/Rate/rate/");
                        setForward("/info.jspx", request, response);
                	}
                    return;
                }
                if (request.getParameter("lector") != null) {
                	int idLector = Integer.parseInt(request.getParameter("lector"));
                	lectorPage(idLector, request, response);
                	return;
                }
                if (request.getParameter("ratelect") != null) {
                	int mark = Integer.parseInt(request.getParameter("ratelect"));
                	int lectorID = Integer.parseInt(request.getParameter("lectorid"));
                	log(user.getId() + " " + lectorID + " " + mark);
                	connector.rateLectors(user.getId(), lectorID, mark);
                	lectorPage(lectorID, request, response);
                	return;
                }
                if (request.getParameter("editlector") != null) {
                	int lectorID = Integer.parseInt(request.getParameter("editlector"));
                	if (user.isAdmin()) {
	                	ResultSet rs = connector.loadLector(lectorID);
	                	setAttribute("rs", rs);
	                	setAttribute("isAdmin", user.isAdmin());
	                	setForward("/editlector.jsp", request, response);
                	} else {
                		setAttribute("message", "Access denied.");
                        setAttribute("linkBack", "http://localhost:8080/Rate/rate/?lector="
                        		+ lectorID);
                        setForward("/info.jspx", request, response);
                	}
                	return;
                }
                if (request.getParameter("search") != null) {
                	setAttribute("isAdmin", user.isAdmin());
                	setForward("/search.jsp", request, response);
                	return;
                }
                if (request.getParameter("logout") != null) {
                    user = null;
                    setForward("/login.jspx", request, response);
                    return;
                }
                goHome(request, response);
            }
    }

	private void lectorPage(int idLector, HttpServletRequest request,
			HttpServletResponse response) {
		ResultSet rsLector = connector.loadLector(idLector);
		ResultSet rsComments = connector.loadComments(idLector);
		ResultSet rsRate = connector.loadRate(user.getId(), idLector);
		setAttribute("rsLector", rsLector);
		setAttribute("rsComments", rsComments);
		setAttribute("rsRate", rsRate);
		setAttribute("userID", user.getId());
		setAttribute("isAdmin", user.isAdmin());
		setForward("/lector.jsp", request, response);
	}

    private void goHome(HttpServletRequest request, HttpServletResponse response) {
        ResultSet rs = connector.loadLectors(null);
        setAttribute("rs", rs);
        setAttribute("user", user);
        setAttribute("isAdmin", user.isAdmin());
        setForward("/home.jsp", request, response);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
