package com.lj.cloudbox.controller;

import com.lj.cloudbox.exception.InvalidRecordsException;
import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.mapper.UserRelationMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.UserRelation;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("relation")
public class UserRelationController {
    @Autowired
    UserRelationMapper userRelationMapper;
    @Autowired
    UserRelationService userRelationService;
    @Autowired
    UserMapper userMapper;

    @GetMapping("relations")
    public MSG getUserRelations(@RequestAttribute("user") User user) {
        return MSG.success("成功", userRelationService.getRelationsById(user.getUid()));
    }

    @DeleteMapping("relation")
    public void deleteRelation(@RequestAttribute("user") User user,
                               @RequestBody Integer[] rids) throws InvalidRecordsException {
        for (Integer rid:rids){
            userRelationService.deleteRelation(user.getUid(),rid);
        }
    }

    @PutMapping("relation")
    public MSG addRelation(@RequestAttribute("user") User user,
                           @RequestParam("searchStr") String searchStr) {
        User searchUser = userMapper.searchUser(searchStr);
        if (searchUser == null) return MSG.fail("不存在这个用户！");
        UserRelation relation = userRelationMapper.getRelation(user.getUid(), searchUser.getUid());
        if (relation != null)  return MSG.fail("该用户已经是你的好友了");
        UserRelation userRelation = new UserRelation();
        userRelation.setProposer(user);
        userRelation.setVerifier(searchUser);
        userRelation.setTime(new Date());
        userRelation.insert();
        return MSG.success("成功添加好友:" + searchUser.getUsername());
    }

    @GetMapping("search")
    public MSG searchRelationUser(@RequestAttribute("user") User user,
                                  @RequestParam("searchStr") String searchStr){
        return MSG.success("搜索成功",userRelationService.searchRelationUser(user.getUid(), searchStr));
    }
}
