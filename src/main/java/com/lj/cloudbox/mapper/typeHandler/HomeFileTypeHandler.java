package com.lj.cloudbox.mapper.typeHandler;


import com.lj.cloudbox.pojo.FileBean;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@MappedJdbcTypes(JdbcType.INTEGER) // 数据库中该字段存储的类型
@MappedTypes(FileBean.class) // 需要转换的对象
public class HomeFileTypeHandler implements TypeHandler<FileBean> {


    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, FileBean fileBean, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,fileBean.getFid());
    }

    @Override
    public FileBean getResult(ResultSet resultSet, String s) throws SQLException {
        FileBean fileBean = new FileBean();
        fileBean.setFid(Integer.parseInt(s));
        return fileBean;
    }

    @Override
    public FileBean getResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getObject(i,FileBean.class);
    }

    @Override
    public FileBean getResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i,FileBean.class);
    }
}
