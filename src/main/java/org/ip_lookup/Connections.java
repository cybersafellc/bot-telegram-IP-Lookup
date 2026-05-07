package org.ip_lookup;

import java.sql.DriverManager;
import java.sql.Connection;

public class Connections {
    public Connections(){
        // we are test connection to mysql with the below credentials on this constructor
        String url = System.getenv("MYSQL_URL");
        String user = System.getenv("MYSQL_USER");
        String pass = System.getenv("MYSQL_PASS");
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("berhasil koneksi ke database");
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
