package com.lj.cloudbox.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RelationMessageSimple {
    private Integer mid;
    private Integer type;
    private Integer proposer;
    private Integer verifier;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date proposeTime;
    private Boolean isRead;
    private String message;
}
