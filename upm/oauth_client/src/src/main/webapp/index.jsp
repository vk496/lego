<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.mycompany.oauth_UM.api.demo.ClientContent"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h1>Hello World!</h1>
        <%
            if (!ClientContent.ACCESS_TOKEN.containsKey(request.getSession().getId())) {
        %>                

        <div>Para usar esta aplicación, debe darle permisos</div>
        <a href="<%=request.getAttribute("oauth_link")%>">Link</a>

        <% } else {
        %>    
        <h3>Datos del usuario</h3>
        <%
            String data = (String) request.getAttribute("response");
            Map<String, String> result = new Gson().fromJson(data, Map.class);

            for (Map.Entry<String, String> entry : result.entrySet()) {
                out.println("<b>" + entry.getKey() + "</b>: " + entry.getValue() + "<br>");
            }
        %>
        <a href="oauth_UM/logout" onclick="window.location.reload(true);" >Logout</a>
        <%}%>
    </body>
</html>
