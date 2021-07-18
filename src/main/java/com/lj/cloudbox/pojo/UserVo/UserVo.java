package com.lj.cloudbox.pojo.UserVo;

import com.lj.cloudbox.pojo.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserVo extends User {
    public UserVo(User user){
        if (user == null)return;
        this.setUid(user.getUid());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setBirthday(user.getBirthday());

        this.setAge(user.getAge());
        this.setRecommendWord(user.getRecommendWord());
        this.setHomePath(user.getHomePath());
        this.setAccountAge(user.getAccountAge());
    }
    Long usedSpace;
}
