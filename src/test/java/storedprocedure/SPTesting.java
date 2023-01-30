package storedprocedure;
import com.mysql.cj.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.sql.*;

/*
Syntax for calling stored procedures
{call procedure_name()}  accepts no parameters and returns no value
{call procedure_name(?,?)} Accepts 2 parameters and return no value
{?= call procedure_name()} Accepts no parameter and return value
{?= call procedure_name(?)} Accepts one parameter and returns value
 */

public class SPTesting {

    Connection con = null;
    Statement stmt = null;
    ResultSet resultSet = null;
    CallableStatement callableStatement;
    ResultSet resultSet1;
    ResultSet resultSet2;

    @BeforeClass
    void setup() throws SQLException {
        con= DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels", "root","deEpdyv3@data");

    }


    @AfterClass
    void tearDown() throws SQLException {
        con.close();
    }

    @Test(priority = 1)
    void test_storedProcedureExist() throws SQLException {
        stmt=con.createStatement();
        resultSet=stmt.executeQuery("show procedure status where name = 'SelectAllCustomers'");
        resultSet.next();
//        resultSet.next() points to the current record
        Assert.assertEquals(resultSet.getString("Name"), "SelectAllCustomers");



    }

    @Test(priority = 2)
    void test_SelectAllCustomers() throws SQLException {
       callableStatement= con.prepareCall("{CALL SelectAllCustomers()}");
       resultSet1 =  callableStatement.executeQuery();

       Statement stmt = con.createStatement();
       resultSet2 = stmt.executeQuery("SELECT * FROM customers");
       Assert.assertEquals(compareResultSets(resultSet1, resultSet2), true);
    }

    public boolean compareResultSets(ResultSet resultSet1, ResultSet resultSet2) throws SQLException {
        while (resultSet1.next()){
            resultSet2.next();
            int count = resultSet1.getMetaData().getColumnCount();
            for(int i=1; i<= count; i++){
                if(!StringUtils.nullSafeEqual(resultSet1.getString(i), resultSet2.getString(i))){
                    return false;
                }
            }
        }
        return true;
    }

}
