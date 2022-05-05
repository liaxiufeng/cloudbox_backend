package com.lj.cloudbox.service;

import com.lj.cloudbox.exception.InvalidRecordsException;
import com.lj.cloudbox.mapper.UserMessageMapper;
import com.lj.cloudbox.mapper.UserRelationMapper;
import com.lj.cloudbox.pojo.UserRelation;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.utils.date.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserRelationService {
    @Autowired
    UserRelationMapper userRelationMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserMessageMapper userMessageMapper;

    public List<Object[]> getRelationsById(Integer uid) {
        List<UserRelation> userRelations = userRelationMapper.getUserRelations(uid);
        return packaging(userRelations,uid);
    }

    public  List<Object[]> searchRelationUser(Integer uid,String searchStr){
        List<UserRelation> userRelations = userRelationMapper.searchRelationUser(uid, searchStr);
        return packaging(userRelations,uid);
    }

    public List<Object[]> packaging(List<UserRelation> userRelations,Integer uid){
        List<Object[]> users = new LinkedList<>();
        for (UserRelation userRelation : userRelations) {
            Integer rid = userRelation.getRid();
            Date time = userRelation.getTime();
            String timeStr = DateUtils.parse_total(time);
            if (userRelation.getProposer().getUid().equals(uid)) {
                User user = userRelation.getVerifier();
                userService.packaging_detail(user);
                users.add(new Object[]{rid, user, timeStr});
            }
            if (userRelation.getVerifier().getUid().equals(uid)) {
                User user = userRelation.getProposer();
                userService.packaging_detail(user);
                users.add(new Object[]{rid, user, timeStr});
            }
        }
        return users;
    }

    public void deleteRelation(Integer uid,Integer rid) throws InvalidRecordsException {
        UserRelation userRelation = userRelationMapper.selectById(rid);
        User proposer = userRelation.getProposer();
        User verifier = userRelation.getVerifier();
        Integer uid1 = proposer.getUid();
        Integer uid2 = verifier.getUid();
        if (uid1.equals(uid) || uid2.equals(uid)){
            userMessageMapper.deleteRelationMessage(uid1, uid2);
            userRelationMapper.deleteById(rid);
        }else {
            throw new InvalidRecordsException("无效请求");
        }

    }
}
