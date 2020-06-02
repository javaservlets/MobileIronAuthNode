package com.example.mobileiron;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UserInfo {
    private final static String DEBUG_FILE = "MobileIron";
    protected Debug debug = Debug.getInstance(DEBUG_FILE);

    public UserInfo() { // only setting vals n via the constructor to make scratch testing as 'real' as possible
        log(" empty constructor:userInfo: " );
    }

    public String getStatus(String query_url, String usr, String pwd, String device_id) { //
        HttpPost http_post;
        String status = "";
        String compliance_status = "";
        try {
            HttpClient httpclient = HttpClients.createDefault();
            http_post = new HttpPost(query_url);
            http_post.setHeader("Accept", "application/json");
            http_post.setHeader("Accept", "*/*");
            http_post.setHeader("Cache-Control", "no-cache");
            http_post.setHeader("Content-Type", "application/json");
            http_post.setHeader("Connection", "keep-alive");
            http_post.setHeader("Authorization", "Basic " + Base64.encode((usr + ":" + pwd).getBytes())); // latter was retrieved on class instantiation

            StringEntity body =new StringEntity("{\n\"identifiers\":[\"" + device_id + "\"]\n}");
            http_post.setEntity(body);

            HttpResponse response = httpclient.execute(http_post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                    String entity_str = EntityUtils.toString(responseEntity);
                    log("  deviceInfo. getStatus: " + entity_str);

                    if (entity_str.equals("[]")) { //graph api returns complianceState: compliant
                        compliance_status = "unknown";
                    } else if (entity_str.toLowerCase().contains("compliant\":false")) {
                        compliance_status = "noncompliant";
                    } else if (entity_str.toLowerCase().contains("compliant\":true")) {
                        compliance_status = "compliant";
                    } else {
                        compliance_status = "error";
                    }
                } else {
                    compliance_status = "connection error";
                }
                log("compliance state? " + compliance_status);

        } catch (Exception e) {
            log(" getStatus.error: " + e.toString());
        } finally {
            return status;
        }
    }

    public void log(String str) {
        debug.error("+++  userInfo:    " + str);
    }
}