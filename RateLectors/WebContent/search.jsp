<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*;" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="context" value="${pageContext.servletContext.contextPath}" />
<%
    boolean admin = (boolean) request.getServletContext().getAttribute("isAdmin");
%>
<html>
<head>
<title>Search lector</title>
<link rel="shortcut icon" href="${context}/img/favicon.ico"/>
<link rel="stylesheet" href="${context}/files/bootstrap.css"/>
<link rel="stylesheet" href="${context}/files/bootstrap-responsive.css"/>
<link rel="stylesheet" href="${context}/files/styles.css"/>
</head>

<body style="border:none;" >
    
    <div class="signup" style="background: none; padding: 0 0 0 35%; margin: 0; width: 80%;">
        
        <h1 class="text-center header" style="width: 100%; margin-top: 20px;">Find lector</h1>
        
        <form id="login-form" method="post" action="http://localhost:8080/Rate/rate/" 
              style="margin: 0 auto; width: 80%; padding: 20px; border: silver 1px solid;
               	border-top: none; border-bottom: none; box-shadow: 0 0 10px rgba(0,0,0,0.5);">
            <input type="text" name="search" style="display: none;"/>
            <div class="control-group">
                <div class="controls">
                    <div class="input-prepend">
                        <label class="control-label add-on">Fullname</label>
                        <input name="fullname" autofocus="autofocus" required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">University</label>
                        <input name="university" required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Faculty</label>
                        <input name="faculty" required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <select size="1" style="width:100%; border-radius: 4px;" name="degree">
                            <option value="assistant">Assistant</option>
                            <option value="teacher">Senior teacher</option>
                            <option value="candidate">Candidate of Sciences</option>
                            <option value="doctor">Doctor of Sciences</option>
                        </select>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Subjects</label>
                        <input name="subjects" required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Goals</label>
                        <input name="goals" type="text" />
                    </div>
                </div>
            </div>

            <div class="control-group">
                <div class="controls text-center">
                    <input type="submit" class="btn btn-large btn-primary transitional"
                           style="width: 100%;" value="Find"/>
                </div>
            </div>

        </form>
    </div>
    <div class="sidebar">
        <a href="http://localhost:8080/Rate/rate/" class="navig-icon home"></a>
        <%
        	if (admin)
        		out.println("<a href=\"http://localhost:8080/Rate/rate/?addnewlector\" class=\"navig-icon new\"></a>");
        %>
        <a href="http://localhost:8080/Rate/rate/?search" class="navig-icon search"></a>
        <a href="http://localhost:8080/Rate/rate/?logout" class="navig-icon logout"></a>
    </div>
</body>
</html>
