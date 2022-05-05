package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.FileItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FileMapper  extends BaseMapper<FileItem> {

    /**
     * 获得某文件夹下的所有一级子文件
     * @param fid 文件id
     * @return 子文件包装成FileBean对象的列表
     */
    @Select("SELECT * from file_item where parent = #{fid}")
    List<FileItem> getChildren(@Param("fid")Integer fid);

    @Select("SELECT count(*) from file_item where parent = #{fid}")
    Integer childrenNumber(@Param("fid")Integer fid);

    /**
     * 查询新建文件名字是否已经存在
     * @param uid 用户id
     * @param parent 新建文件所在文件夹的文件编号
     * @param isFolder 新建文件是否为文件夹
     * @param newName 新文件名
     * @return true：名字合法，反之不合法
     */
    @Select("SELECT count(*)=0 from file_item where uid = #{uid} and parent = #{parent} and is_folder = #{isFolder} and name = #{newName}")
    Boolean checkNewName(@Param("uid")Integer uid,@Param("parent")Integer parent,@Param("isFolder")Boolean isFolder, @Param("newName")String newName);

    /**
     *  通过对象的方式判断新文件名是否合法
     * @param fileItem 新文件对象
     * @return 略
     */
    @Select("SELECT * from file_item where uid = #{uid} and parent = #{parent} and is_folder = #{isFolder} and name = #{name}")
    FileItem sameName(FileItem fileItem);

    @Update("update file_item set name = #{newName} where fid = #{fid}")
    Integer reName(@Param("fid")Integer fid,@Param("newName")String newName);

    @Select("SELECT * from file_item where uid = #{uid} and is_heart = 1")
    List<FileItem> getHeart(@Param("uid")Integer uid);

    @Update("update file_item set is_heart = 1 where uid = #{uid} and fid = #{fid}")
    void addHear(@Param("uid")Integer uid,@Param("fid")Integer fid);

    @Update("update file_item set is_heart = 0 where uid = #{uid} and fid = #{fid}")
    void removeHear(@Param("uid")Integer uid,@Param("fid")Integer fid);

    @Update("update file_item set is_heart = 0 where uid = #{uid}")
    void removeHeartAll(@Param("uid")Integer uid);
}
