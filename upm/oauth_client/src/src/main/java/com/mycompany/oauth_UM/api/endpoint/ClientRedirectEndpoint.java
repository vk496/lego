/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ClientContent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;

/**
 *
 * @author vk496
 */
@WebServlet(name = "RedirectEndpoint", urlPatterns = {"/redirect"})
public class ClientRedirectEndpoint extends HttpServlet {

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

        OAuthAuthzResponse oar = null;
        OAuthClientRequest oauth_req = null;

        try {
            oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);

            oauth_req = OAuthClientRequest
                    .tokenLocation(ClientContent.TOKEN_ENDPOINT)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(ClientContent.APP_CLIENT_ID)
                    .setClientSecret(ClientContent.APP_CLIENT_SECRET)
                    .setRedirectURI(ClientContent.APP_REDIRECT_URI)
                    .setCode(oar.getCode())
                    .buildQueryMessage();
        } catch (OAuthProblemException | OAuthSystemException ex) {
            Logger.getLogger(ClientRedirectEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            sendErrorMessage(response, ex.toString());
            return;
        }

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthAccessTokenResponse OAuthTokenResponse;

        try {
            OAuthTokenResponse = oAuthClient.accessToken(oauth_req);

            ClientContent.ACCESS_TOKEN.put(request.getSession().getId(), OAuthTokenResponse.getAccessToken());

        } catch (OAuthSystemException | OAuthProblemException ex) {
            Logger.getLogger(ClientRedirectEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            sendErrorMessage(response, ex.toString());
            return;
        }

        response.sendRedirect("/oauth_UM");

    }

    private void sendErrorMessage(HttpServletResponse response, String message) throws IOException {
        response.getOutputStream().print("Sorry, we had some errors. Message: <br><br>" + message);

    }

}
