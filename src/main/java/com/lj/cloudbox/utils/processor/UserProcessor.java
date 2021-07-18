package com.lj.cloudbox.utils.processor;

import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.UserVo.UserVo;
import com.lj.cloudbox.utils.Constant;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;

@Data
@Component
public class UserProcessor {
    public User process(User user){
        UserVo userVo = new UserVo(user);
        String nextPath = userVo.getHomePath();
        File file = new File(Constant.PATH + nextPath);
        if (!file.exists()) return userVo;
        userVo.setUsedSpace(file.length());
        return userVo;
    }
}
