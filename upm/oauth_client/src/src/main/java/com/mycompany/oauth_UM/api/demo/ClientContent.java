/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.oauth_UM.api.demo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author vk496
 */
public class ClientContent {

    public static final String APP_CLIENT_ID = System.getenv("APP_CLIENT_ID");
    public static final String APP_CLIENT_SECRET = System.getenv("APP_CLIENT_SECRET");

//    public static final String REGISTRATION_ENDPOINT = "http://localhost:8080/oauth_UM/register";
    public static final String REGISTRATION_ENDPOINT = System.getenv("OAUTH_REGISTER");

//    public static final String AUTH_ENDPOINT = "http://localhost:8080/oauth_UM/auth";
    public static final String AUTH_ENDPOINT = System.getenv("OAUTH_AUTH");

//    public static final String TOKEN_ENDPOINT = "http://localhost:8080/oauth_UM/token";
    public static final String TOKEN_ENDPOINT = System.getenv("OAUTH_TOKEN");

//    public static final String RESOURCE_ENDPOINT = "http://localhost:8080/oauth_UM/get_data";
    public static final String RESOURCE_ENDPOINT = System.getenv("OAUTH_RESOURCE");

//    public static final String APP_REDIRECT_URI = "http://127.0.0.1:8080/oauth_UM/redirect";
    public static final String APP_REDIRECT_URI = System.getenv("OAUTH_REDIRECT");

    public static final String APP_NAME = "Sample Application";
    public static final String APP_URL = "http://www.example.com";
    public static final String APP_ICON = "http://www.example.com/app.ico";
    public static final String APP_DESCRIPTION = "Description of a Sample App";

    public static Map<String, String> ACCESS_TOKEN = new LinkedHashMap<>();

}
