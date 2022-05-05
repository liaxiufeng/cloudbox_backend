package com.lj.cloudbox.pojo.vo;

import com.lj.cloudbox.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserMessageTotal {
    private User proposer;
    private Integer type;
    private String lastMessage;
    private Integer notReadNumber;
    public UserMessageTotal(UserMessageSimple messageSimple){
        User user = new User().selectById(messageSimple.getProposer());
        this.setProposer(user);
        this.setLastMessage(messageSimple.getLastMessage());
        this.setType(messageSimple.getType());
        this.setNotReadNumber(messageSimple.getNotReadNumber());
    }
}