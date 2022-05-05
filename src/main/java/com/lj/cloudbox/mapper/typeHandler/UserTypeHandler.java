package com.lj.cloudbox.mapper.typeHandler;

import com.lj.cloudbox.pojo.User;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.INTEGER) // 数据库中该字段存储的类型
@MappedTypes(User.class) // 需要转换的对象
public class UserTypeHandler  implements TypeHandler<User> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, User user, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,user.getUid());
    }

    @Override
    public User getResult(ResultSet resultSet, String s) throws SQLException {
        String uid = resultSet.getString(s);
        return new User().selectById(Integer.parseInt(uid));
    }

    @Override
    public User getResult(ResultSet resultSet, int i) throws SQLException {
        return new User().selectById(resultSet.getInt(i));
    }

    @Override
    public User getResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i,User.class);
    }
}
