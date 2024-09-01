package lk.ijse.myposbackend.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.myposbackend.persistence.CustomerDb;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "customer", urlPatterns = "/customer")
public class CustomerController extends HttpServlet {
    Connection connection;
    @Override
    public void init() throws ServletException {
        try {

            System.out.println("Trigger init() method");

            InitialContext ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/pos");
            this.connection = pool.getConnection();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        System.out.println("Trigger doGet method");

        if (action.equals("generateCustomerId")){
            generateCustomerId(req,resp);
        } else if (action.equals("getAllCustomer")) {
            //getAllCustomer(req,resp);
        } else if (action.equals("getCustomer")) {
            String custId = req.getParameter("customerId");
            //getCustomer(req,resp,custId);
        }
    }




    private void generateCustomerId(HttpServletRequest req, HttpServletResponse resp){
        CustomerDb customerDb = new CustomerDb();
        String customerId = customerDb.generateCustomerId(connection);
        Jsonb jsonb = JsonbBuilder.create();

        var json = jsonb.toJson(customerId);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }


}


