package org.ip_lookup;

import java.sql.*;
import java.util.ArrayList;

public class Connections {
    private Connection conn;
    public Connections(){
        String url = System.getenv("MYSQL_URL");
        String user = System.getenv("MYSQL_USER");
        String pass = System.getenv("MYSQL_PASS");
        try {
            this.conn = DriverManager.getConnection(url, user, pass);
            System.out.println("berhasil koneksi ke database");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void post(ArrayList data){

    }

    public void put(String where, ArrayList data){
        //
    }
    public void delete(String id){

    }

    public boolean get(String asn){
        String query = "SELECT * FROM as_classification WHERE as_number = ?";
        try (
                PreparedStatement pr = this.conn.prepareStatement(query);
                ){
            pr.setString(1, asn);
            try(ResultSet rs = pr.executeQuery()) {
                if(rs.next()){
                    return rs.getBoolean("rom");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void close(){
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
