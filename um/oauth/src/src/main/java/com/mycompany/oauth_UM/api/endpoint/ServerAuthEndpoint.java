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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

/**
 *
 * @author vk496
 */
@WebServlet(name = "AuthEndpoint", urlPatterns = {"/auth"})
public class ServerAuthEndpoint extends HttpServlet {

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

        final OAuthAuthzRequest oauthRequest;
        String redirectURI = "";
        try {

            String username = null;
            if (ServerContent.USER_AUTHORIZATION_CODES.containsKey(request.getSession().getId())) {
                username = ServerContent.USER_AUTHORIZATION_CODES.get(request.getSession().getId()).getUsername();
            }

            if (username == null) {
                oauthRequest = new OAuthAuthzRequest(request);

                Map<String, String> hashMap = new HashMap<String, String>() {
                    {
                        put(OAuth.OAUTH_REDIRECT_URI, oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI));
                        put(OAuth.OAUTH_RESPONSE_TYPE, oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE));
                    }
                };

                ServerContent.AUTH_REQ.put(request.getSession().getId(), hashMap);
                response.sendRedirect("login");
                return;
            }

            if (!ServerContent.AUTH_REQ.containsKey(request.getSession().getId())) {
                throw new IllegalStateException("authorization data not exist after user logged in");
            }

            Map<String, String> original_parameters = ServerContent.AUTH_REQ.get(request.getSession().getId());

            //Recepci�n del oauthRequest
            //Direcci�n de respuesta
            redirectURI = original_parameters.get(OAuth.OAUTH_REDIRECT_URI);

            //Aqu� hay que implementar la validaci�n de que la aplicaci�n cliente ha sido registrada previamente
            //Se comprueba si que se est� solicitando un Authorization code
            String responseType = original_parameters.get(OAuth.OAUTH_RESPONSE_TYPE);

            if (responseType.equals(ResponseType.CODE.toString())) {

                System.out.println("Recibe peticion de codigo");

                //Genera Authorization code
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                
                String authorization_code = oauthIssuerImpl.authorizationCode();

                //Genera una respuesta con el Authorization Code response
                OAuthResponse resp = OAuthASResponse
                        .authorizationResponse(request, HttpServletResponse.SC_OK)
                        .location(redirectURI)
                        .setCode(authorization_code)
                        .buildQueryMessage();
                
                
//                ServerAuthorization auth_obj = new ServerAuthorization(oauthIssuerImpl.authorizationCode(), username);
                ServerContent.USER_AUTHORIZATION_CODES.get(request.getSession().getId()).setAuthorization_code(authorization_code);

                System.out.println("Genera OauthResponse");

                response.setStatus(resp.getResponseStatus());
                response.sendRedirect(resp.getLocationUri());

            }
        } catch (OAuthProblemException | OAuthSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
