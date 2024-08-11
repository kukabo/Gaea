import org.example.common.utils.DBUtil;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchDataTest {

    @Test
    public void batchInsertData() throws SQLException {
        long timeMillis = System.currentTimeMillis();
        String sql = "INSERT INTO t_user (account, password, nickname) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = DBUtil.getPreparedStatement(sql, false);
        try {
            int count = 0;
            for (int i = 1; i < 1000003; i++) {
                preparedStatement.setString(1, "zhangsan" + i);
                preparedStatement.setString(2, "123456-" + i);
                preparedStatement.setString(3, "zhangsan" + i);
                preparedStatement.addBatch();
                //分批提交
                if (i % 10000 == 0) {
                    count++;
                    int[] ints = preparedStatement.executeBatch();
                    DBUtil.commit();
                    System.out.println("第"+count+"批提交成功条数"+ints.length);
                }
            }
            int[] ints = preparedStatement.executeBatch();
            DBUtil.commit();
            System.out.println("最后一次插入成功，条数"+ints.length + "，耗时" + (System.currentTimeMillis() - timeMillis) + "ms");
        } catch (Exception e) {
            System.out.println("异常报错");
            DBUtil.rollback();
            throw new RuntimeException(e);
        }
        finally {
            System.out.println("关闭连接");
            preparedStatement.close();
            DBUtil.closeConnection();
        }
    }

    @Test
    public void batchSelectData() {

    }

    @Test
    public void batchUpdateData() {

    }

}
