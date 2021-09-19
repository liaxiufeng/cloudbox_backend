package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.FileBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper  extends BaseMapper<FileBean> {
    @Select("SELECT * from file_item where parent = #{fid}")
    List<FileBean> getChildren(@Param("fid")Integer fid);

    @Select("SELECT count(*)=0 from file_item where uid = #{uid} and parent = #{parent} and is_folder = #{isFolder} and name = #{newName}")
    Boolean checkNewName(@Param("uid")Integer uid,@Param("parent")Integer parent,@Param("isFolder")Boolean isFolder, @Param("newName")String newName);

    @Select("SELECT * from file_item where uid = #{uid} and parent = #{parent} and is_folder = #{isFolder} and name = #{name}")
    FileBean sameName(FileBean fileBean);

    @Update("update file_item set name = #{newName} where fid = #{fid}")
    Integer reName(@Param("fid")Integer fid,@Param("newName")String newName);
}
