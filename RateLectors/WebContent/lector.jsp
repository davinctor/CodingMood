<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*;" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="context" value="${pageContext.servletContext.contextPath}" />
<html lang="en-US">
<head>
  <title>User Profile with Content Tabs - Design Shack Demo</title>
  <link rel="stylesheet" href="${context}/files/bootstrap.css"/>
    <link rel="stylesheet" href="${context}/files/bootstrap-responsive.css"/>
    <link rel="stylesheet" href="${context}/files/styles.css"/>
    <link rel="stylesheet" href="${context}/files/styles-lector.css"/>
  <script type="text/javascript" src="js/jquery-1.10.2.min.js"></script>
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
      <div id="userphoto"><img src="images/2.jpg" alt="default avatar"></div>
      <h1>Minimal User Profile Layout</h1>

      <nav id="profiletabs">
        <ul class="clearfix">
          <li><a href="#activity"  class="sel">Comments</a></li>
          <li><a href="#settings">Bio</a></li>
        </ul>
      </nav>
      
      <section id="activity">
        <p>Most recent comments:</p>
        
         <div class="comment">
        	<div class="userphoto">
        		<img  src="images/avatar.png"/>
        	</div>
        	<div class="body-comment">
	        	<p class="username">Johny</p>
	        	<p class="text-comment">@10:15PM - Submitted a news article</p>
	        	<p class="mark">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        	</p>
		        <p class="comment-date">20-11-2014</p>
	        </div>
        </div>
        
        <div class="comment">
        	<div class="userphoto">
        		<img  src="images/avatar.png"/>
        	</div>
        	<div class="body-comment">
	        	<p class="username">Johny</p>
	        	<p class="text-comment">@10:15PM - Submitted a news article</p>
	        	<p class="mark">
	        		<img src="images/star.png" alt="1">
	        	</p>
		        <p class="comment-date">20-11-2014</p>
	        </div>
        </div>
        
         <div class="comment">
        	<div class="userphoto">
        		<img  src="images/avatar.png"/>
        	</div>
        	<div class="body-comment">
	        	<p class="username">Johny</p>
	        	<p class="text-comment">@10:15PM - Submitted a news article</p>
	        	<p class="mark">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="5">
	        		<img src="images/star.png" alt="4">
	        	</p>
		        <p class="comment-date">20-11-2014</p>
	        </div>
        </div>
        
         <div class="comment">
        	<div class="userphoto">
        		<img  src="images/avatar.png"/>
        	</div>
        	<div class="body-comment">
	        	<p class="username">Johny</p>
	        	<p class="text-comment">@10:15PM - Submitted a news article</p>
	        	<p class="mark">
	        		<img src="images/star.png" alt="2">
	        		<img src="images/star.png" alt="2">
	        	</p>
		        <p class="comment-date">20-11-2014</p>
	        </div>
        </div>
        
         <div class="comment">
        	<div class="userphoto">
        		<img  src="images/avatar.png"/>
        	</div>
        	<div class="body-comment">
	        	<p class="username">Johny</p>
	        	<p class="text-comment">@10:15PM - Submitted a news article</p>
	        	<p class="mark">
	        		<img src="images/star.png" alt="3">
	        		<img src="images/star.png" alt="3">
	        		<img src="images/star.png" alt="3">
	        	</p>
		        <p class="comment-date">20-11-2014</p>
	        </div>
        </div>

        <form method="post" action="">
	        <div class="comment-create">
	        	<img  src="images/avatar.png"/>
	        	<div class="comment-area">
				        <textarea name="msg" id="usrmsg"></textarea>
			    </div>
			    <input class="comment-submit" type="submit" name="comment" value="Post"/>
	       	</div>
       	</form>
      </section>
      
      <section id="settings" class="hidden">
        <p>Edit your user settings:</p>
        
        <p class="setting"><span>E-mail Address</span> lolno@gmail.com</p>
        
        <p class="setting"><span>Language</span> English(US)</p>
        
        <p class="setting"><span>Profile Status</span> Public</p>
        
        <p class="setting"><span>Update Frequency</span> Weekly</p>
        
        <p class="setting"><span>Connected Accounts</span> None</p>

        <p class="rating">
        		<a href="#" title="1" class="mark"/>
        		<a href="#" title="2" class="mark"/>
        		<a href="#" title="3" class="mark"/>
        		<a href="#" title="4" class="mark"/>
        		<a href="#" title="5" class="mark"/>
        </p>

      </section>
    </div><!-- @end #content -->
  </div><!-- @end #w -->
</div>
<div class="sidebar">
        <a href="http://localhost:8080/Rate/rate/" class="navig-icon home"></a>
        <a href="http://localhost:8080/Rate/rate/?addnewlector" class="navig-icon new"></a>
        <a href="http://localhost:8080/Rate/rate/?search" class="navig-icon search"></a>
        <a href="http://localhost:8080/Rate/rate/?logout" class="navig-icon logout"></a>
    </div>
</body>
</html>