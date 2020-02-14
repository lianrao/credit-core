<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" /> 
  <meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
  <meta name="viewport" content="width=device-width, initial-scale=1" /> 


  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags --> 
  <meta name="description" content="" /> 
  <meta name="author" content="" /> 
  <link rel="icon" href="../../favicon.ico" /> 
  <title>Blog Template for Bootstrap</title> 
  <!-- Bootstrap core CSS --> 
  <!-- Ionicons --> 
  <!-- Theme style --> 
  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. --> 
  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries --> 
  <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]--> 

    <style type="text/css">
   
    </style> 
</head>
<body>
 
        <% HttpSession s= request.getSession(); %>
        sessionid : <% out.println(s.getId()); %>
  
</body>
</html>