package com.lj.cloudbox.controller;

import com.lj.cloudbox.exception.InvalidRecordsException;
import com.lj.cloudbox.mapper.UserMessageMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.UserMessage;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.service.UserMessageService;
import com.lj.cloudbox.utils.CommonUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("chat")
public class UserChatController {
    @Autowired
    UserMessageMapper userMessageMapper;

    @Autowired
    UserMessageService userMessageService;

    @GetMapping("message")
    public MSG getUserRelationMessage(@RequestAttribute("user") User user,@RequestParam("chatUserId")Integer chatUserId){
        userMessageMapper.readRelationMessage(chatUserId,user.getUid());
        userMessageMapper.changeLastChat(user.getUid(),chatUserId);
        return MSG.success("获取成功",userMessageMapper.getRelationMessage(user.getUid(),chatUserId));
    }

    @GetMapping("message/freshData")
    public MSG freshUserRelationMessage(@RequestAttribute("user") User user,@RequestParam("chatUserId")Integer chatUserId){
        userMessageMapper.readRelationMessage(chatUserId,user.getUid());
        return MSG.success("获取成功",userMessageMapper.getRelationMessageSimple(user.getUid(),chatUserId));
    }

    @GetMapping("chatList")
    public MSG getChatList(@RequestAttribute("user") User user){
        return MSG.success("获取成功",userMessageService.getMessageTotalList(user.getUid()));
    }

    @GetMapping("chatList/freshData")
    public MSG getChatListSimple(@RequestAttribute("user") User user){
        return MSG.success("获取成功",userMessageMapper.getMessageSimpleList(user.getUid()));
    }

    @GetMapping("search")
    public MSG searchMessage(@RequestAttribute("user") User user,
                             @RequestParam("searchStr")String searchStr){
        return null;
    }

    @PutMapping("message")
    public void sendMessage(@RequestAttribute("user") User user,
                           @RequestBody UserMessage message) throws InvalidRecordsException {
        Integer verifierUid = message.getVerifier().getUid();
        if (!CommonUtils.haveValue(message.getType(), message.getMessage(), verifierUid))throw new InvalidRecordsException("参数不全");
        message.setIsRead(false);
        message.setProposeTime(new Date());
        message.setProposer(user);
        User verifierUser = new User();
        verifierUser.setUid(verifierUid);
        message.setVerifier(verifierUser);
        message.insert();
    }

    @Delete("message")
    public void deleteRelationMessage(@RequestAttribute("user") User user,@RequestParam("uid")Integer uid){
        userMessageMapper.deleteRelationMessage(user.getUid(),uid);
    }
    @Delete("message/all")
    public void deleteUserMessage(@RequestAttribute("user") User user){
        userMessageMapper.deleteUserMessage(user.getUid());
    }
}
