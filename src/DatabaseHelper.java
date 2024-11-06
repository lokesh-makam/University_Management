import java.sql.*;

public class DatabaseHelper {
    public static Connection connection=null;
    public   PreparedStatement ps=null;
    static {
        try{
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "root", "sli19181");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        ps = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps.executeQuery();
    }
    public int executeUpdate(String query, Object... params) throws SQLException {
        ps = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps.executeUpdate();
    }
    public void closeResultConnection(ResultSet rs)throws SQLException{
        if (rs != null) rs.close();
    }
    public void closeConnection() throws SQLException {
        if (ps != null) ps.close();
        if (connection != null) connection.close();
    }
}
