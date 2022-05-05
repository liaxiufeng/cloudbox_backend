package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.RecentOpenFileMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.service.RecentOpenFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("recentOpen")
public class RecentOpenFileController {
    @Autowired
    RecentOpenFileMapper recentOpenFileMapper;
    @Autowired
    RecentOpenFileService recentOpenFileService;

    @GetMapping("recent")
    public MSG getRecentFiles(@RequestAttribute("user") User user){
        return MSG.success("获取成功",recentOpenFileService.getRecent(user));
    }

    @DeleteMapping("recent")
    public void deleteRecentAll(@RequestAttribute("user") User user,
                                @RequestParam("isFolder")Boolean isFolder){
        recentOpenFileMapper.deleteRecentAll(user.getUid(),isFolder ? 1 : 0);
    }

}
