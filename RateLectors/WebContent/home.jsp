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

</head>

<body>
    <h1 class="text-center header">Latest added</h1>
    
<div class="gallery">
<%
        ResultSet rs = (ResultSet) request.getServletContext().getAttribute("rs");
        if (rs == null) return;
        while (true) {
            out.println("<figure>");
                out.println("<img src=\"http://localhost:8080/Rate/rate/?image=" + rs.getInt(1) + "\"/>");
                out.println("<figcaption>");
                    out.println("<span class=\"nameLector\">");
                    	String name[] = rs.getString(2).split(" ");
                        out.println(name[1] + " " + name[0] + "<br>");
                        out.println(name[2]);
                    out.println("</span>");
                    out.println("<span class=\"universityLector\">");
                        out.println(rs.getString(4));
                    out.println("</span>");
                    out.println("<span class=\"facultyLector\">");
                    out.println(rs.getString(3));
                out.println("</span>");
                out.println("</figcaption>");
            out.println("</figure>");
            if (!rs.isLast())
                rs.next();
            else break;
        }
%>
	<div class="navig"></div>
</div>
<div class="sidebar">
        <a href="http://localhost:8080/Rate/rate/" class="navig-icon home"></a>
        <a href="http://localhost:8080/Rate/rate/?addnewlector" class="navig-icon new"></a>
        <a href="http://localhost:8080/Rate/rate/?search" class="navig-icon search"></a>
        <a href="http://localhost:8080/Rate/rate/?logout" class="navig-icon logout"></a>
    </div>
</body>
</html>

