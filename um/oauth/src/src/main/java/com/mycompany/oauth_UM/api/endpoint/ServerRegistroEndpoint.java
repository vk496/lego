/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ServerContent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.JSONHttpServletRequestWrapper;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.OAuthServerRegistrationRequest;
import org.apache.oltu.oauth2.ext.dynamicreg.server.response.OAuthServerRegistrationResponse;

/**
 *
 * @author vk496
 */
@WebServlet(name = "RegistroEndpoint", urlPatterns = {"/register"})
public class ServerRegistroEndpoint extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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

        response.getWriter().append("Served at: ").append(request.getContextPath());
        System.out.println("Registro--doGet()");
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
    @Consumes("application/json")
    @Produces("application/json")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Registro--doPost()");
        OAuthServerRegistrationRequest oauthRequest = null;
        OAuthResponse oauth_response = null;

        try {
            oauthRequest = new OAuthServerRegistrationRequest(new JSONHttpServletRequestWrapper(request));

            oauthRequest.getType();
            oauthRequest.discover();
            oauthRequest.getClientName();
            oauthRequest.getClientUrl();
            oauthRequest.getClientDescription();
            oauthRequest.getRedirectURI();

            oauth_response = OAuthServerRegistrationResponse
                    .status(HttpServletResponse.SC_OK)
                    .setClientId(ServerContent.CLIENT_ID)
                    .setClientSecret(ServerContent.CLIENT_SECRET)
                    .setIssuedAt(ServerContent.ISSUED_AT)
                    .setExpiresIn(ServerContent.EXPIRES_IN)
                    .buildJSONMessage();

            response.setStatus(oauth_response.getResponseStatus());
            response.getWriter().write(oauth_response.getBody());

        } catch (OAuthProblemException e) {
            try {
                // TODO Auto-generated catch block

                oauth_response = OAuthServerRegistrationResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .error(e)
                        .buildJSONMessage();

                response.sendError(oauth_response.getResponseStatus(), oauth_response.getBody());

            } catch (OAuthSystemException ex) {
                Logger.getLogger(ServerRegistroEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (OAuthSystemException ex) {
            Logger.getLogger(ServerRegistroEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
