package com.lj.cloudbox.pojo.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    Object title;
    boolean success;
    String Reason;

    public static Result fail(Object title,String reason){
        return new Result(title,false,reason);
    }

    public static Result success(Object title,String reason){
        return new Result(title,true,reason);
    }
}
