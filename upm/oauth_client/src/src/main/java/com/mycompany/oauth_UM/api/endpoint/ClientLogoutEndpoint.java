/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ClientContent;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author vk496
 */
@WebServlet(name = "LogoutEndpoint", urlPatterns = {"/logout"})
public class ClientLogoutEndpoint extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        String JSESSION_ID= request.getSession().getId();
        ClientContent.ACCESS_TOKEN.remove(request.getSession().getId());

        response.sendRedirect(request.getContextPath());
    }

}
