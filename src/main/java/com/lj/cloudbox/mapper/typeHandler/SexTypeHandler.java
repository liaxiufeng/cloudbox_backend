package com.lj.cloudbox.mapper.typeHandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.INTEGER) // 数据库中该字段存储的类型
@MappedTypes(String.class) // 需要转换的对象
public class SexTypeHandler   implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        if ("男".equals(s)){
            preparedStatement.setInt(i,1);
        }else if ("女".equals(s)){
            preparedStatement.setInt(i,2);
        }else {
            preparedStatement.setInt(i,1);
        }
    }

    @Override
    public String getResult(ResultSet resultSet, String s) throws SQLException {
        int sex = resultSet.getInt(s);
        return sex == 2 ? "女":"男";
    }

    @Override
    public String getResult(ResultSet resultSet, int i) throws SQLException {
        int sex = resultSet.getInt(i);
        return sex == 2 ? "女":"男";
    }

    @Override
    public String getResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i, String.class);
    }
}
