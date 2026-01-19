package org.example;

import java.sql.*;

public class test {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/hanpyo_db";
    String user = "root";
    String password = "mypass123";

    String sql = "SELECT * FROM users";

        try (
    Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
        ) {
        while (rs.next()) {
            System.out.println(
                    rs.getLong("id") + " " +
                            rs.getString("name") + " " +
                            rs.getString("email")
            );
        }
    } catch (
    SQLException e) {
        e.printStackTrace();
    }
}}
