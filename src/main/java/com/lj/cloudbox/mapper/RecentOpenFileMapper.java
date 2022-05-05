package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.RecentOpenFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecentOpenFileMapper  extends BaseMapper<RecentOpenFile> {

    List<RecentOpenFile> getRecentFiles(Integer uid);

    List<RecentOpenFile> getRecentFolders(Integer uid);

    List<RecentOpenFile> getRecentFilesLimited(@Param("uid") Integer uid,@Param("limitedNumber")  Integer limitedNumber);

    List<RecentOpenFile> getRecentFoldersLimited(@Param("uid") Integer uid,@Param("limitedNumber")  Integer limitedNumber);

    RecentOpenFile existRecord(@Param("uid") Integer uid,@Param("fid") Integer fid);

    @Delete("delete from recent_open_file where uid = #{uid} and is_folder = #{isFolder}")
    void deleteRecentAll(@Param("uid") Integer uid,@Param("isFolder")Integer isFolder);
}
