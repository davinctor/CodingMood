<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*;" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="context" value="${pageContext.servletContext.contextPath}" />
<html>
<head>
<title>Home</title>
<link rel="shortcut icon" href="${context}/img/favicon.ico"/>

    <!-- Stylesheets
    ================================================== -->
    <link rel="stylesheet" href="${context}/files/bootstrap.css"/>
    <link rel="stylesheet" href="${context}/files/bootstrap-responsive.css"/>
    <link rel="stylesheet" href="${context}/files/styles.css"/>
    <link rel="stylesheet" href="${context}/files/styles-home.css"/>
	<style>
		body {
			padding: 20px;
			border: silver 1px solid;
			border-top: none;
			border-bottom: none;
			box-shadow: 0 0 10px rgba(0,0,0,0.5);
		}
	</style>
</head>

<body>
	<hr class="soften inverse"/>
    <hr class="soften"/>
    <h1 class="text-center header">Latest added</h1>
<div class="gallery">
<%
		boolean admin = (boolean) request.getServletContext().getAttribute("isAdmin");
        ResultSet rs = (ResultSet) request.getServletContext().getAttribute("rs");
        if (rs == null) return;
        while (rs.next()) {
            out.println("<figure>");
                out.println("<img src=\"http://localhost:8080/Rate/rate/?image=" + rs.getInt(1) + "\"/>");
                out.println("<figcaption>");
                    out.println("<span class=\"nameLector\"><a href=\"http://localhost:8080/Rate/rate/?lector=" + rs.getInt(1) + "\">");    
                    	String name[] = rs.getString(2).split(" ");
                        out.println(name[1] + " " + name[0] + "<br>");
                        out.println(name[2]);
                    out.println("</a></span>");
                    out.println("<span class=\"universityLector\">");
                        out.println(rs.getString(4));
                    out.println("</span>");
                    out.println("<span class=\"facultyLector\">");
                    out.println(rs.getString(3));
                out.println("</span>");
                out.println("</figcaption>");
            out.println("</figure>");
        }
%>
    <hr class="soften" style="margin-bottom: 0;"/>
	<div class="navig"></div>
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

