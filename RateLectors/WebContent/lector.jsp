<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*;" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="context" value="${pageContext.servletContext.contextPath}" />
<%
	ResultSet rsComments = (ResultSet) request.getServletContext().getAttribute("rsComments");
	ResultSet rsLector = (ResultSet) request.getServletContext().getAttribute("rsLector");
	ResultSet rsRate = (ResultSet) request.getServletContext().getAttribute("rsRate");
	boolean admin = (boolean) request.getServletContext().getAttribute("isAdmin");
	rsLector.next();
	boolean haveRate = false;
	if (rsRate.next()) haveRate = true;
	int userID = (int) request.getServletContext().getAttribute("userID");
%>
<html lang="en-US">
<head>
  <title>
  	<%
  		String name[] = rsLector.getString(2).split(" ");
      	out.println(name[1] + " " + name[0] + " " + name[2]);
     %>
  </title>
  <link rel="shortcut icon" href="${context}/img/favicon.ico"/>
  <link rel="stylesheet" href="${context}/files/bootstrap.css"/>
  <link rel="stylesheet" href="${context}/files/bootstrap-responsive.css"/>
  <link rel="stylesheet" href="${context}/files/styles.css"/>
  <link rel="stylesheet" href="${context}/files/styles-lector.css"/>
  <script type="text/javascript" src="${context}/files/jquery-1.10.2.min.js"></script>
  <script type="text/javascript">
		$(function(){
		  $('#profiletabs ul li a').on('click', function(e){
		    e.preventDefault();
		    var newcontent = $(this).attr('href');
		    
		    $('#profiletabs ul li a').removeClass('sel');
		    $(this).addClass('sel');
		    
		    $('#content section').each(function(){
		      if(!$(this).hasClass('hidden')) { $(this).addClass('hidden'); }
		    });
		    
		    $(newcontent).removeClass('hidden');
		  });
		});
	</script>
</head>
<body>
  <div class="">
  <div id="w">
    <div id="content" class="clearfix">
      <div id="userphoto">
      	<% out.println("<img src=\"http://localhost:8080/Rate/rate/?image=" + rsLector.getInt(1) + "\">"); %>
      </div>
      <h1><%
      name = rsLector.getString(2).split(" ");
      out.println(name[1] + " " + name[0] + " " + name[2]);
      if (admin) {
    	  out.println("<a class=\"editlect\" href=\"http://localhost:8080/Rate/rate/?editlector=" + rsLector.getInt(1) + "\" />");
      		out.println("<img src=\"http://localhost:8080/Rate/img/edit.png\" />");
    	  out.println("</a>");
      }
      %></h1>
      <nav id="profiletabs">
        <ul class="clearfix">
          <li><a href="#activity"  class="sel">Comments</a></li>
          <li><a href="#settings">Bio</a></li>
        </ul>
      </nav>
      
      <section id="activity">
        <p>Most recent comments:</p>
        
        <%
	        if (rsComments == null) return;
	        while (rsComments.next()) {
	            out.println("<div class=\"comment\">");
	                out.println("<div class=\"userphoto\">");
	                	out.println("<img  src=\"http://localhost:8080/Rate/rate/?imageuser=" + rsComments.getInt(1) + "\"/>");
	                out.println("</div>");
	                out.println("<div class=\"body-comment\">");
	                	out.println("<p class=\"username\">");
	                		out.println(rsComments.getString(2));
	                	out.println("</p>");
	                	out.println("<p class=\"text-comment\">");
                			out.println(rsComments.getString(4));
                		out.println("</p>");
                		out.println("<p class=\"mark\">");
                			int mark = (int) rsComments.getFloat(3);
                			for(int i = 0; i < mark; i++)
            					out.println("<img src=\"http://localhost:8080/Rate/img/star.png\" alt=\"" + mark + "\">");
            			out.println("</p>");
            			out.println("<p class=\"comment-date\">");
            				out.println(rsComments.getDate(5).toString());
            			out.println("</p>");
	                out.println("</div>");
	            out.println("</div>");
	        }
		%>
        
        <form method="post" action="http://localhost:8080/Rate/rate/">
	        <div class="comment-create">
	        	<% out.println("<img  src=\"http://localhost:8080/Rate/rate/?imageuser=" + userID + "\"/>"); %>
	        	<div class="comment-area">
	        			<% out.println("<input type=\"text\" name=\"user\"" + 
	        					" style=\"display: none;\" value=\"" + userID + "\"/>"); %>
	        			<% out.println("<input type=\"text\" name=\"lector\"" +
	        					" style=\"display: none;\" value=\"" + rsLector.getInt(1) + "\" />"); %>
				        <textarea name="msg" id="usrmsg"></textarea>
			    </div>
			    <input class="comment-submit btn btn-large btn-primary transitional" type="submit" name="comment" value="Post"/>
	       	</div>
       	</form>
      </section>
      
      <section id="settings" class="hidden">
        
        <p class="setting"><span>Faculty</span> <% out.println(rsLector.getString(4)); %></p>
        
        <p class="setting"><span>University</span> <% out.println(rsLector.getString(3)); %></p>
        
        <p class="setting"><span>Degree</span> <% out.println(rsLector.getString(5)); %></p>
        
        <p class="setting"><span>Subjects</span> <% out.println(rsLector.getString(6)); %></p>
        
        <% 
        	String goals = rsLector.getString(7);
        	if (goals.length() != 0)
        		out.println("<p class=\"setting\"><span>Goals</span>" + goals + "</p>"); 
        %>

        <p class="rating">
        	<% 
        		int mark = 0;
        		int j = 0;
        		if (haveRate) {
        			mark = rsRate.getInt(2);  
        			j = mark;
        		}
        		for (int i = 0; i < 5; j--, i++)
        			if (j > 0) {
        				out.println("<a href=\"http://localhost:8080/Rate/rate/?ratelect=" + (i+1)
        						+ "&lectorid=" + rsLector.getInt(1)
        						+ "\" title=\"" + mark + "\" class=\"mark\" " + 
        					"style=\"opacity: 1;\"></a>");
        			} else {
        				out.println("<a href=\"http://localhost:8080/Rate/rate/?ratelect=" + (i+1) 
        						+ "&lectorid=" + rsLector.getInt(1)
        						+ "\" title=\"" + mark + "\" class=\"mark\"></a>");
        			}
        	%>
        </p>

      </section>
    </div><!-- @end #content -->
  </div><!-- @end #w -->
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