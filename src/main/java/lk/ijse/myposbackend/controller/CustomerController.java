package lk.ijse.myposbackend.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.myposbackend.dto.CustomerDTO;
import lk.ijse.myposbackend.dto.ItemDTO;
import lk.ijse.myposbackend.persistence.CustomerDb;
import lk.ijse.myposbackend.persistence.ItemDb;


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
            getAllCustomer(req,resp);
        } else if (action.equals("getAllCustomerIds")) {
            //String custId = req.getParameter("customerId");
            getAllCustomerIds(req,resp);
        } else if (action.equals("getCustomerDetails")) {
            String custId = req.getParameter("customerId");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("customerId : "+custId);
            getCustomerDetails(req,resp,custId);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType()!= null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);

            System.out.println("doPost customerDTO : "+customerDTO.toString());

            var customerDb = new CustomerDb();
            boolean result = customerDb.saveCustomer(connection, customerDTO);

            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("CustomerController information saved successfully!");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to saved customer information!");
            }
        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);

            var customerDb = new CustomerDb();
            boolean result = customerDb.updateCustomer(connection, customerDTO);

            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("CustomerController information updated successfully!");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to saved customer information!");
            }
        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String custId = req.getParameter("customerId");

        System.out.println("CustomerId : "+custId);
        var customerDb = new CustomerDb();
        boolean result = customerDb.deleteCustomer(connection, custId);

        if (result){
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("CustomerController information deleted successfully!");
        }else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to saved customer information!");
        }
    }

    private void getAllCustomer(HttpServletRequest req, HttpServletResponse resp){
        var customerDb = new CustomerDb();
        ArrayList<CustomerDTO> allCustomer = customerDb.getAllCustomer(connection);

        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(allCustomer);

        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }


    private void getAllCustomerIds(HttpServletRequest req, HttpServletResponse resp){

        var customerDb = new CustomerDb();
        ArrayList<String> arrayList = customerDb.getAllCustomerIds(connection);  // Strong typing

        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(arrayList);

        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getCustomerDetails(HttpServletRequest req, HttpServletResponse resp, String id){
        var customerDb = new CustomerDb();
        CustomerDTO customerDetails = customerDb.getCustomerDetails(connection,id);

        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(customerDetails);

        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}


