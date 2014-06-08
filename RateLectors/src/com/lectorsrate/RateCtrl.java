package com.lectorsrate;

import com.lectorsrate.logic.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        } else if (request.getParameter("signup") != null && user == null) {
            addUser(request, response);
        } else if (ServletFileUpload.isMultipartContent(request) && user != null) {
            if (ServletFileUpload.isMultipartContent(request)) {
                addLector(request, response);
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
        } else {
            try {
                goHomeWithSearch(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(RateCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log(firstname + " ;" + lastname + " ;" + email + " ;" + username + " ;" + password);
        String result = connector.signup(firstname + " " + lastname, username, password, email);
        if (result == null) {
            setAttribute("message", "Registration is successful, " + username + ".");
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/?login");
            setForward("/info.jspx", request, response);
        } else {
            setAttribute("message", result);
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/?signup");
            setForward("/info.jspx", request, response);
        }
    }

    private void addLector(HttpServletRequest request, HttpServletResponse response) {
        String[] fields = new String[8];
        byte[] photo = null;
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
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
        } catch (FileUploadException e) {
            log(e.getMessage(), e);
        }
        log(photo == null ? "photo is null" : "photo is not null");
        String result = connector.createLector(fields, photo);
        setAttribute("message", result);
        setAttribute("linkBack", "http://localhost:8080/Rate/rate/?addnewlector");
        setForward("/info.jspx", request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (request.getParameter("image") != null) {
                response.setContentType("image/jpeg");
                ServletOutputStream os = response.getOutputStream();
                byte[] data = connector.loadLectorPhoto(Integer.parseInt(request.getParameter("image")));
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
                    setForward("/addlector.jspx", request, response);
                    return;
                }
                if (request.getParameter("logout") != null) {
                    user = null;
                    setForward("/login.jspx", request, response);
                    return;
                }

                goHomeWithSearch(request, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RateCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void goHomeWithSearch(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        ResultSet rs = null;
        if (request.getParameter("search") != null) {
            rs = connector.loadLectors(request.getParameter("search"));
        } else rs = connector.loadLectors(null);
        if (!rs.next() || rs == null) {
            setAttribute("message", "Empty results with query '" + request.getParameter("search") + "'");
            setAttribute("linkBack", "http://localhost:8080/Rate/rate/");
            setForward("/info.jspx", request, response);
        } else {
            setAttribute("rs", rs);
            setAttribute("user", user);
            setForward("/home.jsp", request, response);
        }
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
