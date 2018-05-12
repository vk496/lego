/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.demo;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author vk496
 */
public class ServerAuthorization {

    private String authorization_code;
    private String token_code;
    private String username;

    private static Set<ServerAuthorization> all_auths = new LinkedHashSet<>();

    public ServerAuthorization(String username) {
        this.username = username;

        all_auths.add(this);
    }

    public static ServerAuthorization getObjByAuthz_code(String authorization_code) {

        for (ServerAuthorization s : all_auths) {
            if (s.authorization_code.equals(authorization_code)) {
                return s;
            }
        }

        throw new IllegalStateException("username not have authorization");
    }

    public static ServerAuthorization getObjByUsername(String username) {

        for (ServerAuthorization s : all_auths) {
            if (s.username.equals(username)) {
                return s;
            }
        }

        throw new IllegalStateException("username not have authorization");
    }

        public static ServerAuthorization getObjByToken_code(String token_code) {

        for (ServerAuthorization s : all_auths) {
            if (s.token_code.equals(token_code)) {
                return s;
            }
        }

        throw new IllegalStateException("username not have authorization");
    }
    
    public static boolean existValidToken(String token_code) {

        for (ServerAuthorization s : all_auths) {
            if (s.token_code.equals(token_code)) {
                return true;
            }
        }

        return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthorization_code() {
        return authorization_code;
    }

    public String getToke_code() {
        return token_code;
    }

    public void setToken_code(String token_code) {
        this.token_code = token_code;
    }

    public String getUsername() {
        return username;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.authorization_code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerAuthorization other = (ServerAuthorization) obj;
        if (!Objects.equals(this.authorization_code, other.authorization_code)) {
            return false;
        }
        return true;
    }

}
