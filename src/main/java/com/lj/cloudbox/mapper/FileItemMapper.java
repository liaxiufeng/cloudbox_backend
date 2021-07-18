package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.FileItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface FileItemMapper extends BaseMapper<FileItem> {
    @Select("select count(*) from file where uid = #{uid} and path = #{path}")
    Boolean canCreate(@Param("uid")Integer uid,@Param("path") String path);

    @Select("select count(*) from file where uid = #{uid} and path = #{path}")
    Boolean canCreate(FileItem fileItem);
}
