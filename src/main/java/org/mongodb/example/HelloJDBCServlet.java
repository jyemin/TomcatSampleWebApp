/*
 * Copyright 2016 MongoDB Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mongodb.example;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloJDBCServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            super.init(config);
            InitialContext initialContext = new InitialContext();
            Context envContext = (Context) initialContext.lookup("java:comp/env");

            dataSource = (DataSource) envContext.lookup("jdbc/Test");

        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type.
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            writeOutputFile(getMagicNumber(), request, out);
        }
    }

    private int getMagicNumber() throws ServletException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("select _id from test ORDER BY _id DESC LIMIT 1");
                try (ResultSet rs = statement.getResultSet()) {
                    rs.next();
                    return rs.getInt("_id");
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void writeOutputFile(final int magicNumber, final HttpServletRequest request, final PrintWriter out) {
        out.println("<!DOCTYPE html>");  // HTML 5
        out.println("<html><head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("<title>" + "Hello World From JDBC" + "</title></head>");
        out.println("<body>");
        out.println("<h1>" + "Hello World From JDBC " + magicNumber + "</h1>");  // Prints "Hello, world!"
        // Set a hyperlink image to refresh this page
        out.println("<a href='" + request.getRequestURI() + "'><img src='images/return.gif'></a>");
        out.println("</body></html>");
    }
}
