package com.example;

import java.sql.*;

public class StoredProcedureConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/test_db_poc";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static CallableStatement callableStatement;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection established successfully!");

            callInsertProcedure();

            countRecords();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }
    private static void callInsertProcedure() throws SQLException {
        String procedureCall = "{ CALL INSERT_MILLION_RECORDS() }";
        
        callableStatement = connection.prepareCall(procedureCall);
        callableStatement.execute();
        System.out.println("Procedure called successfully.");
    }

    private static void countRecords() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM large_table";
        
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
        
        if (resultSet.next()) {
            int count = resultSet.getInt("total");
            System.out.println("Total records in large_table: " + count);
        }
    }

    private static void closeResources() {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (callableStatement != null) callableStatement.close();
            if (connection != null) connection.close();
            System.out.println("Resources closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}