package lk.ijse.myposbackend.persistence;

import lk.ijse.myposbackend.dto.CustomerDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDb {
    public String generateCustomerId(Connection connection){
        String sql = "SELECT MAX(customerId) AS last_customer_id FROM customer;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                String lastCustomerId = resultSet.getString("last_customer_id");
                System.out.println(lastCustomerId);
                if (lastCustomerId == null){
                    return "Cust-0001";
                }else {
                    int nextId = Integer.parseInt(lastCustomerId.substring(5))+1;
                    return "Cust-" + String.format("%04d",nextId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public ArrayList<CustomerDTO> getAllCustomer(Connection connection){
        String sql = "select * from customer;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<CustomerDTO> customerDTOS = new ArrayList<>();
             while (resultSet.next()){
                 CustomerDTO customerDTO = new CustomerDTO(
                         resultSet.getString("customerId"),
                         resultSet.getString("customerName"),
                         resultSet.getString("contact"),
                         resultSet.getString("address")
                 );
                 customerDTOS.add(customerDTO);
             }
             return customerDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
