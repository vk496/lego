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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.json.JSONObject;

/**
 *
 * @author vk496
 */
@WebServlet(name = "ResourceEndpoint", urlPatterns = {"/get_data"})
public class ServerResourceEndpoint extends HttpServlet {

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

        try {
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.BODY);

            String accessToken = oauthRequest.getAccessToken();

            //Token exist?
            if (!ServerAuthorization.existValidToken(oauthRequest.getAccessToken())) {
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm(ServerContent.RESOURCE_SERVER_NAME)
                        .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                        .buildHeaderMessage();

                response.sendError(oauthResponse.getResponseStatus(), oauthResponse.getBody());
                response.getOutputStream().flush();
                return;
            }
            

            //Token expired?
            //if(token_expired)
            //
            //Toekn is sufficient?
            //if(scope...)
            Map<String, String> sensitive_data;
            String username = ServerAuthorization.getObjByToken_code(accessToken).getUsername();

            if (username != null) {
                sensitive_data = ServerContent.getLDAPattributes(username);
            } else {
                sensitive_data = new LinkedHashMap<>();

            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print((new JSONObject(sensitive_data)).toString());
            response.getOutputStream().flush();

        } catch (OAuthSystemException | OAuthProblemException ex) {
            Logger.getLogger(ServerResourceEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
