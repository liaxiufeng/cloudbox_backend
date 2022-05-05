package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.FileMapper;
import com.lj.cloudbox.mapper.RecentOpenFileMapper;
import com.lj.cloudbox.pojo.FileItem;
import com.lj.cloudbox.pojo.RecentOpenFile;
import com.lj.cloudbox.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecentOpenFileService {
    @Autowired
    RecentOpenFileMapper recentOpenFileMapper;

    @Autowired
    FileMapper fileMapper;

    public void open(FileItem fileItem){
        if (fileItem.getParent() == null) return;
        RecentOpenFile existRecord = recentOpenFileMapper.existRecord(fileItem.getUid(), fileItem.getFid());
        if (existRecord == null){
            RecentOpenFile recentOpenFile = new RecentOpenFile();
            recentOpenFile.setFileItem(fileItem);
            recentOpenFile.setUid(fileItem.getUid());
            recentOpenFile.setIsFolder(fileItem.getIsFolder());
            recentOpenFile.setTime(new Date());
            recentOpenFile.insert();
        }else {
            existRecord.setTime(new Date());
            existRecord.updateById();
        }
    }

    public Map<String, List<FileItem>> getRecent(User user){
        Map<String, List<FileItem>> recent = new HashMap<>();
        Integer uid = user.getUid();
        Integer limited = 4;
        List<RecentOpenFile> recentFilesLimited = recentOpenFileMapper.getRecentFilesLimited(uid, limited);
        List<RecentOpenFile> recentFoldersLimited = recentOpenFileMapper.getRecentFoldersLimited(uid, limited);
        List<FileItem> fileRecent = new ArrayList<>();
        List<FileItem> folderRecent = new ArrayList<>();
        for (RecentOpenFile recentOpenFile:recentFilesLimited){
            FileItem fileItem = recentOpenFile.getFileItem();
//            fileBean.setLastUpdateDate(DateUtils.parse_total(recentOpenFile.getTime()));
            fileRecent.add(fileItem);
        }
        for (RecentOpenFile recentOpenFolder:recentFoldersLimited){
            FileItem fileItem = recentOpenFolder.getFileItem();
//            fileBean.setLastUpdateDate(DateUtils.parse_total(recentOpenFolder.getTime()));
            folderRecent.add(fileItem);
        }
        recent.put("fileRecent",fileRecent);
        recent.put("folderRecent",folderRecent);
        return recent;
    }
}
