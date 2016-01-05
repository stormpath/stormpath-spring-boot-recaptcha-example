/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.boot.examples.filter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReCaptchaFilter implements Filter {
    // See https://www.google.com/recaptcha for setup
    private static final String RECAPTCHA_SECRET = "<Your Secret Key from google recaptcha>";

    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String RECAPTCHA_RESPONSE_PARAM = "g-recaptcha-response";

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (
            !(req instanceof HttpServletRequest) ||
            !("POST".equalsIgnoreCase(((HttpServletRequest)req).getMethod()))
        ) {
            chain.doFilter(req, res);
            return;
        }

        PostMethod method = new PostMethod(RECAPTCHA_URL);
        method.addParameter("secret", RECAPTCHA_SECRET);
        method.addParameter("response", req.getParameter(RECAPTCHA_RESPONSE_PARAM));
        method.addParameter("remoteip", req.getRemoteAddr());

        HttpClient client = new HttpClient();
        client.executeMethod(method);
        BufferedReader br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String readLine;
        StringBuffer response = new StringBuffer();
        while(((readLine = br.readLine()) != null)) {
            response.append(readLine);
        }

        JSONObject jsonObject = new JSONObject(response.toString());
        boolean success = jsonObject.getBoolean("success");

        if (success) {
            chain.doFilter(req, res);
        } else {
            ((HttpServletResponse)res).sendError(HttpStatus.BAD_REQUEST.value(), "Bad ReCaptcha");
        }
    }

    @Override
    public void destroy() {

    }
}
