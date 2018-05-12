/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ServerAuthorization;
import com.mycompany.oauth_UM.api.demo.ServerContent;
import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author vk496
 */
@WebServlet(name = "LoginEndpoint", urlPatterns = {"/login"})
public class ServerLoginEndpoint extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    @Produces(MediaType.TEXT_HTML)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp").forward(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (ServerContent.checkLDAPCredentials(username, password)) {
//            ServerContent.USER_AUTHORIZATION_CODES.get(request.getSession().getId()).setUsername(username);

            ServerContent.USER_AUTHORIZATION_CODES.put(request.getSession().getId(), new ServerAuthorization(username));

//            request.setAttribute("username", username);
//            request.getRequestDispatcher("/auh").forward(request, response);
            response.sendRedirect("auth");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("<html>"
                    + "<head></head>"
                    + "<body"
                    + "<h1>LOGIN ERROR</h1>"
                    + "<br>"
                    + "<a href=\"login\">Volver a la página del login</a>"
                    + "</body>"
                    + "</html>");
        }

    }

}
