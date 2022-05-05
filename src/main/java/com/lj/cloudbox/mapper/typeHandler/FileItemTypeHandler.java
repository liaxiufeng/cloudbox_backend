package com.lj.cloudbox.mapper.typeHandler;


import com.lj.cloudbox.pojo.FileItem;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@MappedJdbcTypes(JdbcType.INTEGER) // 数据库中该字段存储的类型
@MappedTypes(FileItem.class) // 需要转换的对象
public class FileItemTypeHandler implements TypeHandler<FileItem> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, FileItem fileItem, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, fileItem.getFid());
    }

    @Override
    public FileItem getResult(ResultSet resultSet, String s) throws SQLException {
        String fid = resultSet.getString(s);
        return new FileItem().selectById(Integer.parseInt(fid));
    }

    @Override
    public FileItem getResult(ResultSet resultSet, int i) throws SQLException {
        return new FileItem().selectById(resultSet.getInt(i));
    }

    @Override
    public FileItem getResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i, FileItem.class);
    }
}
