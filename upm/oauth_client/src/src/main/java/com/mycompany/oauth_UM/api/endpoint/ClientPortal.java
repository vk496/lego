/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ClientContent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONObject;

/**
 *
 * @author vk496
 */
@WebServlet(name = "Portal", urlPatterns = {"/"})
public class ClientPortal extends HttpServlet {

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

        OAuthClientRequest oauth_req = null;
        OAuthResourceResponse resourceResponse = null;
        
        
        try {
            if (!ClientContent.ACCESS_TOKEN.containsKey(request.getSession().getId())) {

                oauth_req = OAuthClientRequest
                        .authorizationLocation(ClientContent.AUTH_ENDPOINT)
                        .setRedirectURI(ClientContent.APP_REDIRECT_URI)
                        .setClientId(ClientContent.APP_CLIENT_ID)
                        .setResponseType(OAuth.OAUTH_CODE)
                        .buildQueryMessage();

                request.setAttribute("oauth_link", oauth_req.getLocationUri());
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
//        processRequest(request, response);
            } else {
                OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

                oauth_req = new OAuthBearerClientRequest(ClientContent.RESOURCE_ENDPOINT)
                        .setAccessToken(ClientContent.ACCESS_TOKEN.get(request.getSession().getId()))
                        .buildQueryMessage();
                oauth_req.setHeader(OAuth.HeaderType.CONTENT_TYPE, "application/x-www-form-urlencoded");

                resourceResponse = oAuthClient.resource(oauth_req, OAuth.HttpMethod.POST, OAuthResourceResponse.class);

                if (resourceResponse.getResponseCode() == HttpServletResponse.SC_OK) {

                    request.setAttribute("response", resourceResponse.getBody());
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                } else {
                    //bad error with access token
                }

            }

        } catch (OAuthSystemException | OAuthProblemException ex) {
            Logger.getLogger(ClientPortal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
