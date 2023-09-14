package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DatabaseOperation {

    Connection connection;
    public void connect_to_sql() throws ClassNotFoundException {  //connect to local
        String url = "jdbc:mysql://localhost:3306/TCP_TRANS";
        String username = "root";
        String password = "******";
        try {
	    Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database ok!");
            // 这里可以执行数据库操作
            //connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void close_connect_sql(){//close 
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    public int select_query(String username,String c_password) {//test f
	String sql = "SELECT ID_index,Password FROM register_table WHERE username = ?";
        int ID_index = 0;
	String Password = "0";
	try {
	    // 创建 PreparedStatement 对象
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
	    preparedStatement.setString(1, username);
	    ResultSet resultSet = preparedStatement.executeQuery();
	    // 遍历结果集并输出数据
            if (resultSet.next()) {
                ID_index = resultSet.getInt("ID_index");
               // String usernameResult = resultSet.getString("username");
                Password = resultSet.getString("Password");
               // System.out.println("ID: " + id + ", Username: " + usernameResult + ", Email: " + email);
    //        resultSet.close();
  //          preparedStatement.close();
//            connection.close();
	//    break;
	    }
	    else{}
         resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	//System.out.println("query ok");
        if(Password.equals(c_password)){ System.out.println("query ok"); return ID_index;}
	else { System.out.println("query no");return 0;}
    }

    
    public void Log_in_insert(String username,String password)  {//use this method to insert data for register
        try {
            // 获取当前系统时间
            Date currentDate = new Date();
	    java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
            String insertQuery = "INSERT INTO  register_table (username, sub_date,Password) VALUES (?, ? ,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(3,password );
	    preparedStatement.setDate(2,sqlDate);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

