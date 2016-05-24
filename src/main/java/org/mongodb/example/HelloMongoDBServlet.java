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

import com.mongodb.MongoClient;
import org.bson.BsonDocument;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.mongodb.client.model.Aggregates.sample;
import static java.util.Collections.singletonList;

public class HelloMongoDBServlet extends HttpServlet {

    private MongoClient mongoClient;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("Initializing MongoClient");
        mongoClient = new MongoClient();
    }

    @Override
    public void destroy() {
        System.out.println("Destroying MongoClient");
        mongoClient.close();
        super.destroy();
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

        return mongoClient.getDatabase("test").getCollection("test", BsonDocument.class)
                .aggregate(singletonList(sample(1)))
                .first()
                .getNumber("_id").intValue();
    }

    private void writeOutputFile(final int magicNumber, final HttpServletRequest request, final PrintWriter out) {
        out.println("<!DOCTYPE html>");  // HTML 5
        out.println("<html><head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("<title>" + "Hello World From MongoDB" + "</title></head>");
        out.println("<body>");
        out.println("<h1>" + "Hello World From MongoDB " + magicNumber + "</h1>");  // Prints "Hello, world!"
        // Set a hyperlink image to refresh this page
        out.println("<a href='" + request.getRequestURI() + "'><img src='images/return.gif'></a>");
        out.println("</body></html>");
    }
}
