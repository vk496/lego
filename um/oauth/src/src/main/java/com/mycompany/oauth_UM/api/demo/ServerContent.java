/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.demo;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;

/**
 *
 * @author vk496
 */
public class ServerContent {

    public static final String RESOURCE_SERVER_NAME = "UM OAuth Resource Server";

    public static final String CLIENT_ID = System.getenv("CLIENT_ID_INIT");
    public static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET_INIT");
    
    public static final String ISSUED_AT = "0123456789";
    public static final Long EXPIRES_IN = 987654321l;

//    public static final String LDAP_SERVER_PATH = "ldap://192.168.251.6:389";
    public static final String LDAP_SERVER_PATH = System.getenv("LDAP_SERVER_PATH");
    
//    public static final String LDAP_AUTH_USER = "cn=admin,dc=um,dc=es";
    public static final String LDAP_AUTH_USER = System.getenv("LDAP_AUTH_USER");
    
//    public static final String LDAP_AUTH_PASSWORD = "S3cuurreeee_lDAP_P@@@@sssswWWo00o0o0orDDD";
    public static final String LDAP_AUTH_PASSWORD = System.getenv("LDAP_AUTH_PASSWORD");
    
//    public static final String LDAP_SEARCH_BASE = "dc=um,dc=es";
    public static final String LDAP_SEARCH_BASE = System.getenv("LDAP_SEARCH_BASE");
    
//    public static final String LDAP_SEARCH_FILTER = "(&(&(|(objectclass=inetOrgPerson))(|(memberof=cn=owncloud,ou=Servicios,dc=um,dc=es)))(|(cn=%uid)(|(mailPrimaryAddress=%uid)(mail=%uid))))";
    public static final String LDAP_SEARCH_FILTER = System.getenv("LDAP_SEARCH_FILTER");


    public static final Map<String, ServerAuthorization> USER_AUTHORIZATION_CODES = new LinkedHashMap<>();

    public static final Map<String, Map<String, String>> AUTH_REQ = new LinkedHashMap<>();

    public static boolean checkLDAPCredentials(String username, String password) {

        try {
            Attributes attrs = getLdapQuerry(username);

            if (attrs == null) {
                return false;
            }

            Attribute userPassword = attrs.get("userPassword");
            String pwd = new String((byte[]) userPassword.get());

            return pwd.equals(password);

        } catch (NamingException ex) {
            Logger.getLogger(ServerContent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static Map<String, String> getLDAPattributes(String username) {
        Map<String, String> res = new LinkedHashMap<>();

        try {
            Attributes attrs = getLdapQuerry(username);
            if (attrs == null) {
                return res;
            }

            for (NamingEnumeration attr = attrs.getAll(); attr.hasMore();) {
                Attribute attribute = (Attribute) attr.next();
                System.out.println("Attribute id: " + attribute.getID());

                for (NamingEnumeration val = attribute.getAll(); val.hasMore();) {
                    String value = val.next().toString();
                    System.out.println("Attribute value: " + value);

                    res.put(attribute.getID(), value);
                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(ServerContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    private static Attributes getLdapQuerry(String username) {

        try {
            // Set up the environment for creating the initial context
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ServerContent.LDAP_SERVER_PATH);
            //
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, ServerContent.LDAP_AUTH_USER); //we have 2 \\ because it's a escape char
            env.put(Context.SECURITY_CREDENTIALS, ServerContent.LDAP_AUTH_PASSWORD);

            LdapContext ctx = new InitialLdapContext(env, null);
            ctx.setRequestControls(null);
            NamingEnumeration<?> namingEnum = ctx.search(ServerContent.LDAP_SEARCH_BASE, ServerContent.LDAP_SEARCH_FILTER.replace("%uid", username), getSimpleSearchControls());
            while (namingEnum.hasMore()) {
                SearchResult result = (SearchResult) namingEnum.next();
                Attributes attrs = result.getAttributes();

                return attrs;
            }
        } catch (NamingException ex) {
            Logger.getLogger(ServerContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        //String[] attrIDs = {"objectGUID"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

}
