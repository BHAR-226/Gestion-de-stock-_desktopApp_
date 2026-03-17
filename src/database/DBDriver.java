package database;

import java.sql.*;


public class DBDriver {
    private String url;
    private String username;
    private String passwd;

    private Connection connection;

    public DBDriver(String url , String username , String passwd ){
        try {
            this.url = url;
            this.username = username;
            this.passwd = passwd;

            this.connection =  DriverManager.getConnection(url , username , passwd);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public ResultSet sendQueryBase(String query){
        try{
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;

        }catch (Exception e){
            System.err.println(e);
            return null;
        }
    }

    public int sendUpdate(String sql, Object... params) {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate(); // Returns number of affected rows
        } catch (Exception e) {
            System.err.println(e);
            return 0;
        }
    }


    public ResultSet sendQuery(String sql, Object... params) {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);

            // Insert parameters
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();

        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }


    public void closeConnection(){
        try{
            this.connection.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}