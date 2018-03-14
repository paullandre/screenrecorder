package paulscreenrecorder;

/**
 * @author Paul Andre Francisco
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.net.InetAddress;

public class Screen_Rec_Logs
{
    public volatile String logTime;

    public static void main(String args[])
    {
        //java.util.Date today = new java.util.Date();
        //Timestamp timeNow = new java.sql.Timestamp(today.getTime());
        //System.out.println(setTime(timeNow));
        //insertRecordLogin();
        //insertRecordLogout();        
    }
    
    public void setTime()
    {
        java.util.Date today = new java.util.Date();
        logTime = new java.sql.Timestamp(today.getTime()).toString();
    }

    public void insertRecordingLogs()
    {
        DatabaseConnection dbConn = new DatabaseConnection();
        Connection ado_cons = dbConn.getConnection_postgresql();
        ResultSet ado_rs = null;
        Statement ado_stmt = null;
        PreparedStatement ado_pstmt = null;
        if(dbConn.isDBConnected() == true)
        {
            //System.out.println(DatabaseConnection.isDBConnected());
            try
            {
                java.util.Date today = new java.util.Date();
                InetAddress i = java.net.InetAddress.getLocalHost();
                String users = System.getProperty("user.name");
                Timestamp timeNow = new java.sql.Timestamp(today.getTime());
                String ado_insert = "INSERT INTO screen_recordings_status (ip_address, comp_name, username, status, run_time) "
                + "VALUES ('" + i.getHostAddress()
                + "', '" + i.getHostName()
                + "', '" + users
                + "', 'Start Successful', "
                + "'" + timeNow + "');";
                System.out.print(ado_insert);

                logTime = timeNow.toString();
//                System.out.println(logTime);

                ado_stmt = ado_cons.createStatement();
                ado_stmt.executeUpdate(ado_insert);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    ado_stmt.close();
                    ado_cons.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void updateAgentIP()
    {
        DatabaseConnection dbConn = new DatabaseConnection();
        Connection ado_cons = dbConn.getConnection_postgresql();        
        Statement ado_stmt = null;        
        if(dbConn.isDBConnected() == true)
        {
            try
            {
                InetAddress ip = java.net.InetAddress.getLocalHost();
                String ado_update = "UPDATE agent_desktop_info "
                        + "SET agent_ip_address = '" + ip.getHostAddress() + "',"
                        + " ip_time_update = '" + logTime + "'"
                        + " WHERE desktop_ip ILIKE '%" + ip.getHostName() + "%'"
                        + ";";
                System.out.print(ado_update);
                ado_stmt = ado_cons.createStatement();
                ado_stmt.executeUpdate(ado_update);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    ado_stmt.close();
                    ado_cons.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertRecordLogin(String fileName)
    {        
        DatabaseConnection dbConn = new DatabaseConnection();
        Connection ado_cons = dbConn.getConnection_postgresql();
        ResultSet ado_rs = null;
        Statement ado_stmt = null;
        PreparedStatement ado_pstmt = null;
        if(dbConn.isDBConnected() == true)
        {
            //System.out.println(DatabaseConnection.isDBConnected());
            try
            {
                java.util.Date today = new java.util.Date();
                InetAddress i = java.net.InetAddress.getLocalHost();
                String users = System.getProperty("user.name");
                setTime();
                String ado_insert = "INSERT INTO screen_rec_login (ip_address, comp_name, username, transaction, time_executed, filename) "
                + "VALUES ('" + i.getHostAddress()
                + "', '" + i.getHostName()
                + "', '" + users
                + "', 'START', "
                + "'" + logTime
                + "', '" + fileName + "');";
                System.out.print(ado_insert);
                
//                logTime = timeNow.toString();
//                System.out.println(logTime);

                ado_stmt = ado_cons.createStatement();
                ado_stmt.executeUpdate(ado_insert);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    ado_stmt.close();
                    ado_cons.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }        
    }

    public void insertRecordLogout(String fileName)
    {
        DatabaseConnection dbConn = new DatabaseConnection();
        Connection ado_cons = dbConn.getConnection_postgresql();
        ResultSet ado_rs = null;
        Statement ado_stmt = null;
        PreparedStatement ado_pstmt = null;
        if(dbConn.isDBConnected() == true)
        {
            //System.out.println(DatabaseConnection.isDBConnected());
            try
            {
                java.util.Date today = new java.util.Date();
                InetAddress i = java.net.InetAddress.getLocalHost();
                String users = System.getProperty("user.name");
                Timestamp timeNow = new java.sql.Timestamp(today.getTime());
                String ado_insert = "INSERT INTO screen_rec_logout (ip_address, comp_name, username, transaction, login_time, time_executed,is_merged, filename) "
                + "VALUES ('" + i.getHostAddress()
                + "', '" + i.getHostName()
                + "', '" + users
                + "', 'STOP', "
                + "'" + logTime
                + "', '" + timeNow
                + "', 'FALSE',"
                + "'" + fileName + "');";
                System.out.print(ado_insert);

                ado_stmt = ado_cons.createStatement();
                ado_stmt.executeUpdate(ado_insert);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    ado_stmt.close();
                    ado_cons.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    } 
}
