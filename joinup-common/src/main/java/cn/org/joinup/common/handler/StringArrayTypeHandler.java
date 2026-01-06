package cn.org.joinup.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class StringArrayTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            List<String> parameter,
            JdbcType jdbcType
    ) throws SQLException {
        Array array = ps.getConnection()
                .createArrayOf("text", parameter.toArray());
        ps.setArray(i, array);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return toList(rs.getArray(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return toList(rs.getArray(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return toList(cs.getArray(columnIndex));
    }

    private List<String> toList(Array array) throws SQLException {
        if (array == null) {
            return null;
        }
        return Arrays.asList((String[]) array.getArray());
    }
}
