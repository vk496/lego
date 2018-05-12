/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.endpoint;

import com.mycompany.oauth_UM.api.demo.ServerAuthorization;
import com.mycompany.oauth_UM.api.demo.ServerContent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

/**
 *
 * @author vk496
 */
@WebServlet(name = "TokenEndpoint", urlPatterns = {"/token"})
public class ServerTokenEndpoint extends HttpServlet {

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

        OAuthTokenRequest oauthRequest = null;

        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

        try {
            oauthRequest = new OAuthTokenRequest(request);

            //Check Client redentials
            if (!oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID).equals(ServerContent.CLIENT_ID)
                    && !oauthRequest.getParam(OAuth.OAUTH_CLIENT_SECRET).equals(ServerContent.CLIENT_SECRET)) {

                OAuthResponse oauth_resp = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .buildJSONMessage();

                response.sendError(oauth_resp.getResponseStatus(), oauth_resp.getBody());
                return;
            }

            //Check Client authorization
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE)) {
                if (!ServerContent.USER_AUTHORIZATION_CODES.containsKey(request.getSession().getId())
                        || !ServerContent.USER_AUTHORIZATION_CODES.get(request.getSession().getId()).getAuthorization_code().equals(oauthRequest.getParam(OAuth.OAUTH_CODE))) {

                    OAuthResponse oauth_resp = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                            .setErrorDescription("invalid authorization code")
                            .buildJSONMessage();

                    response.sendError(oauth_resp.getResponseStatus(), oauth_resp.getBody());
                    return;

                }

            }

            String access_token = oauthIssuerImpl.accessToken();
            
            String code = oauthRequest.getParam(OAuth.OAUTH_CODE);

//            ServerContent.USER_AUTHORIZATION_CODES.get(request.getSession().getId()).setToken_code(access_token);
            ServerAuthorization.getObjByAuthz_code(code).setToken_code(access_token);
            
            
            OAuthResponse oauth_response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(access_token)
                    .setExpiresIn("3600")
                    .buildJSONMessage();

            response.setStatus(oauth_response.getResponseStatus());
            response.getOutputStream().print(oauth_response.getBody());
            response.getOutputStream().flush();
            return;

        } catch (OAuthSystemException | OAuthProblemException ex) {
            Logger.getLogger(ServerTokenEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
