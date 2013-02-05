package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton that provides database access for GUI
 *
 * User: mark.mcdonald
 * Date: 12/2/12
 */
public class DatabaseStatementExecutor {
    private static Connection connection;

    public static void connect(){
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "gtport";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "000000";
        try {
            Class.forName(driver).newInstance();
            connection= DriverManager.getConnection(url + dbName, userName, password);
            System.out.println("Connected to the database");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        System.out.println("Attempting to Disconnect from the database");
        try {
            connection.close();
            System.out.println("Disconnected from the database");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Executes a query.
     *
     * @param statement
     * @param params
     * @return Rows from the query
     */
    public static ArrayList<HashMap<String,Object>> execute(String statement, String[] params){
        ArrayList<HashMap<String,Object>> rowResults = new ArrayList<HashMap<String, Object>>();
        try {
            /*
             Generate a query and insert the parameters
              */
            for (String param : params){
                statement = statement.replaceFirst("\\$\\w+","\"" + param + "\"");
            }
            System.out.println("Attempted statement was: " + statement);
            Statement sqlStatement = connection.createStatement();

            /*
            Parse the results of the query
             */
            ResultSet results = sqlStatement.executeQuery(statement);


            // grab metadata
            ResultSetMetaData metaData = results.getMetaData();

            // grab the column names
            int numColumns = metaData.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<String>();
            for (int i = 1; i <= numColumns; i++){
                columnNames.add(metaData.getColumnName(i));
            }

            // Create a hashmap representing each row (key = column name, value = value in column at that row)
            while (results.next()){
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (String columnName : columnNames){
                    Object value = results.getObject(columnName);
                    row.put(columnName,value);
                }
                rowResults.add(row);
            }
            sqlStatement.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return rowResults;
    }

    public static void executeUpdate(String statement, String[] params){
        List<HashMap<String,Object>> rowResults = new ArrayList<HashMap<String, Object>>();
        try {
            /*
             Generate a query and insert the parameters
              */
            for (String param : params){
                statement = statement.replaceFirst("\\$\\w+","\"" + param + "\"");
            }
            System.out.println("Attempted statement was: " + statement);
            Statement sqlStatement = connection.createStatement();

            /*
            Parse the results of the query
             */
            sqlStatement.executeUpdate(statement);

            sqlStatement.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
