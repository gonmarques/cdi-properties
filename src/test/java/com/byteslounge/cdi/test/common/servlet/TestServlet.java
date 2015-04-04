/*
 * Copyright 2015 byteslounge.com (Gonçalo Marques).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.byteslounge.cdi.test.common.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;

import com.byteslounge.cdi.test.wpm.OtherService;

/**
 * Servlet used in integration tests
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
@WebServlet(urlPatterns = "/testservlet")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private OtherService otherService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Assert.assertEquals(Integer.class, otherService.getInteger().getClass());
        Assert.assertEquals(new Integer(2), otherService.getInteger());
        req.setAttribute("result", otherService.getText());
        req.setAttribute("integer", otherService.getInteger());
        req.getRequestDispatcher("test.jsp").forward(req, resp);
    }

}
