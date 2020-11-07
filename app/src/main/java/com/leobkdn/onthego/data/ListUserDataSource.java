package com.leobkdn.onthego.data;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.modify_user.list.Users_class;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListUserDataSource {
    private static final String secret = "@M1@j0K37oU?";
    private static final String hostName = "192.168.1.15";
    private static final String instance = "LEOTHESECOND";
    private static final String port = "1433";
    private static final String dbName = "OnTheGo";
    private static final String dbUser = "user";
    private static final String dbPassword = "userpass";
    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private ArrayList<Users_class> Users;
    private int sum = 0 ; // lưu số lượng users query được.
    private String stringResult = " Hello world";

    public ArrayList<Users_class> getListUsers() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // bắt đầu query
                String sqlQuery = "select * from [User]";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                ResultSet rs = statement.executeQuery();
                if (!rs.next()) throw new SQLException("Table trống");
                else {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String name = rs.getString(2);
                        String email = rs.getString(3);
                        Users.add(new Users_class(name, email, id));
                    }
                    return Users;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return Users;
    }
    public Result<String> editInfo(LoggedInUser user) {
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {

                String sqlQuery = "update [User] set [name] = ?, email = ?, birthday = ?, [address] = ? where id in (select userId from [User_Token] where token = ?)";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                int userId = Integer.parseInt(payload.getString("uid"));
//                statement.setInt(1, userId);
//                statement.setString(1, key);
                statement.setString(1, user.getDisplayName());
                statement.setString(2, user.getEmail());
                statement.setDate(3, new java.sql.Date(user.getBirthday().getTime()));
                statement.setString(4, user.getAddress());
                statement.setString(5, user.getToken());
                int recordUpdated = statement.executeUpdate();
                if (recordUpdated > 0) {
                    // if user updated
                    // return success
                    stringResult = "Cập nhật thành công!";
                } else {
                    stringResult = "Không tìm thấy người dùng!";
                }
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (JWTVerificationException e) {
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(stringResult);
    }
}
