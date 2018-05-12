<%-- 
    Document   : login
    Created on : 10-may-2018, 1:29:25
    Author     : vk496
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>UM login page</title>
    </head>
    <body>
        <form action="login" method="post">  
            Email<input type="text" name="username"/><br/><br/>  
            Password:<input type="password" name="password"/><br/><br/>  
            <input type="submit" value="login"/>  
        </form>  
    </body>
</html>
