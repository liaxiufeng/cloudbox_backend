package com.lj.cloudbox.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserMessageSimple {
    private Integer proposer;
    private Integer type;
    private String lastMessage;
    private Integer notReadNumber;

    public UserMessageTotal packaging(){
        return new UserMessageTotal(this);
    }
}
