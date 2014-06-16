<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*;" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="context" value="${pageContext.servletContext.contextPath}" />
<%
	ResultSet rs = (ResultSet) request.getServletContext().getAttribute("rs");
	rs.next();
%>
    <html>
<head>
<title>
	<%
  		String name[] = rs.getString(2).split(" ");
      	out.println(name[1] + " " + name[0] + " " + name[2]);
     %>
</title>
<link rel="shortcut icon" href="${context}/img/favicon.ico"/>
<link rel="stylesheet" href="${context}/files/bootstrap.css"/>
<link rel="stylesheet" href="${context}/files/bootstrap-responsive.css"/>
<link rel="stylesheet" href="${context}/files/styles.css"/>
</head>

<body>
    
    <div class="signup" style="background: none; padding: 0 0 0 35%; margin: 0; width: 80%;">
        
        <h1 class="text-center header" style="width: 100%; margin-top: 10px; padding: 12px;">Edit <%out.println(name[1] + " " + name[0]);%></h1>
        
        <form id="login-form" method="post" action="http://localhost:8080/Rate/rate/" 
              enctype="multipart/form-data" style="margin: 0 auto; width: 80%; padding: 20px; border: silver 1px solid;
               	border-top: none; border-bottom: none; box-shadow: 0 0 10px rgba(0,0,0,0.5);">
            <div class="control-group">
                <div class="controls">
                    <div class="input-prepend">
                        <label class="control-label add-on">First Name</label>
                        <input name="firstname" autofocus="autofocus" 
                        value="<% out.println(name[0]); %>"
                        required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Last Name</label>
                        <input name="lastname" 
                        	value="<% out.println(name[1]); %>"
                        	required="required" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Patronymic</label>
                        <input name="patronymic" required="required"
                        	value="<% out.println(name[2]); %>" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">University</label>
                        <input name="university" required="required" 
                        	value="<% out.println(rs.getString(3)); %>" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Faculty</label>
                        <input name="faculty" required="required"
                        	value="<% out.println(rs.getString(4)); %>" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <select size="1" style="width:100%; border-radius: 4px;" name="degree">
                        <% 
                        	if (rs.getString(5).equals("Assistant"))
                            	out.println("<option value=\"Assistant\" selected>Assistant</option>");
                        	else
                        		out.println("<option value=\"Assistant\">Assistant</option>");
                        
                        	if (rs.getString(5).equals("Senior teacher"))
                        		out.println("<option value=\"Senior teacher\" selected>Senior teacher</option>");
                    		else
                    			out.println("<option value=\"Senior teacher\">Senior teacher</option>");
                        	
                        	if (rs.getString(5).equals("Candidate of Sciences"))
                        		out.println("<option value=\"Candidate of Sciences\" selected>Candidate of Sciences</option>");
                    		else
                    			out.println("<option value=\"Candidate of Sciences\">Candidate of Sciences</option>");
                        	
                        	if (rs.getString(5).equals("Doctor of Sciences"))
                        		out.println("<option value=\"Doctor of Sciences\" selected>Doctor of Sciences</option>");
                    		else
                    			out.println("<option value=\"Doctor of Sciences\">Doctor of Sciences</option>");
                        %>
                        </select>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Subjects</label>
                        <input name="subjects" required="required"
                        	value="<% out.println(rs.getString(6)); %>" type="text"/>
                    </div>
                    <div class="input-prepend">
                        <label class="control-label add-on">Goals</label>
                        <input name="goals" type="text"
                        	value="<% out.println(rs.getString(7)); %>" />
                    </div>
                    <div class="input-prepend">
                        <div style="border-radius: 4px">
                        	<div class="upload">
                            	<input type="file" accept="image/*" name="imageFile"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
			<input type="text" name="editlector" 
            	value="<% out.println(rs.getInt(1)); %>" style="display: none;"/>
            <div class="control-group">
                <div class="controls text-center">
                    <input type="submit" class="btn btn-large btn-primary transitional"
                           style="width: 100%;" value="Save"/>
                    
                    <!--<hr class="soften inverse"/>
                    <hr class="soften"/>-->
                    
                </div>
            </div>

        </form>
    </div>
    <div class="sidebar">
        <a href="http://localhost:8080/Rate/rate/" class="navig-icon home"></a>
        <a href=http://localhost:8080/Rate/rate/?addnewlector" class="navig-icon new"></a>
        <a href="http://localhost:8080/Rate/rate/?search" class="navig-icon search"></a>
        <a href="http://localhost:8080/Rate/rate/?logout" class="navig-icon logout"></a>
    </div>
</body>
</html>
