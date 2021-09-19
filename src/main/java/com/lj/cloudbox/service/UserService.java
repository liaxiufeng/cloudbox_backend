package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.utils.date.DateUtils;
import com.lj.cloudbox.utils.file.FileSizeFormatUtil;
import com.lj.cloudbox.utils.ProjectSettings;
import com.lj.cloudbox.utils.TokenUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    FileService fileService;

    @Autowired
    ProjectSettings projectSettings;

    public User getUser(String token){
        if (!StringUtils.hasLength(token)) return null;
        try {
            String uid = TokenUtil.decode(token);
            if (StringUtils.hasLength(uid)){
                return userMapper.selectById(Integer.parseInt(uid));
            }else {
                return null;
            }
        }catch (NumberFormatException e){
            return null;
        }
    }

    public void packaging(User user) {
        packaging_detail(user);
        packaging_space(user);
    }

    public void packaging_detail(User user) {
        try {
            user.setAccountAge(DateUtils.getAge(user.getAccountBirthday()));
            user.setAge(DateUtils.getAge(user.getBirthday()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void packaging_space(User user) {
        String realHomePath = user.getHomeFile().getName();
        long usedSize = FileUtils.sizeOf(new File(realHomePath));
        String totalSize = projectSettings.getSpace();
        user.setUsedSpace(FileSizeFormatUtil.formatFileSize(usedSize));
        user.setTotalSpace(totalSize);
        double usedPercent = (double) usedSize / FileSizeFormatUtil.formatFileSize(totalSize);
        BigDecimal b = new BigDecimal(usedPercent);
        usedPercent = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        user.setUsedPercent(usedPercent);
        Long freeSpace = FileSizeFormatUtil.formatFileSize(totalSize) - usedSize;
        user.setFreeSpaceLong(freeSpace);
        user.setFreeSpace(FileSizeFormatUtil.formatFileSize(freeSpace));
    }

    public void createHome(User user){
        user.setHomeFile(fileService.createHome(user.getUid()));
        user.updateById();
    }
}
