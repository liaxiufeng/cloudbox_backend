package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.UserMessageMapper;
import com.lj.cloudbox.pojo.vo.UserMessageSimple;
import com.lj.cloudbox.pojo.vo.UserMessageTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserMessageService {
    @Autowired
    UserMessageMapper userMessageMapper;

    public List<UserMessageTotal> getMessageTotalList(Integer uid){
        List<UserMessageSimple> messageSimpleList = userMessageMapper.getMessageSimpleList(uid);
        if (messageSimpleList.size() == 0) return new ArrayList<>();
        List<UserMessageTotal> res = new LinkedList<>();
        for (UserMessageSimple messageSimple:messageSimpleList){
            res.add(messageSimple.packaging());
        }
        return res;
    }
}
