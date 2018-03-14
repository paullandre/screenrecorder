package paulscreenrecorder;

/**
 *
 * @author Paul Andre Francisco
 */

import java.sql.*;

public class DatabaseConnection
{
    String URL;
    Connection con;
    boolean isConnected;

    public boolean isDBConnected()
    {
        return isConnected;
    }

    public Connection pg_con()
    {
        return con;
    }

    public Connection mysql_con()
    {
        return con;
    }
    
    public Connection getConnection_postgresql ()
    {
        URL="jdbc:postgresql://localhost:5432/screen_recorder";
        //URL gamelan = new URL("http", "www.w3schools.com", "80", "tags/tag_div.asp");
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Printing The Exception :");
            System.out.println (e);
            System.out.println("Printing The Stack Trace :");
            e.printStackTrace ();
        }
        try
        {
            System.out.println("Checking username and password...");
            con = DriverManager.getConnection (URL,"postgres","damang08");
            System.out.println(URL);
            isConnected = true;
            if (isConnected = true)
            {
                //System.out.println(con.getTransactionIsolation());
                System.out.println("Connected to POSTGRESQL Database!!!");
                System.out.println("\n");
            }          
        }
        catch (SQLException e)
        {
            System.out.println (e);
            e.printStackTrace ();
        }
        return con;
    }

    public Connection getConnection_mysql ()
    {
        URL="jdbc:mysql://10.100.5.18:3306/cti";
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Printing The Exception :");
            System.out.println (e);
            System.out.println("Printing The Stack Trace :");
            e.printStackTrace ();
        }
        try
        {
          System.out.println("Checking username and password...");
          con = DriverManager.getConnection (URL,"cti","link2cti");
          System.out.println(URL);
          if (isConnected = true)
          {
              //System.out.println(con.getTransactionIsolation());
              System.out.println("Connected to MYSQL Database!!!");
          }
        }
        catch (SQLException e)
        {
          System.out.println (e);
          e.printStackTrace ();
        }
        return con;
    }
}