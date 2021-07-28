package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.service.UserService;
import com.lj.cloudbox.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public MSG login(User user) {
        User loginUser = userMapper.login(user);
        if (loginUser != null) {
            String uid = loginUser.getUid() + "";
            String token = TokenUtil.sign(uid);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return MSG.success("登录成功", map);
        }
        return MSG.fail("登录失败");
    }

    @GetMapping("/user")
    @ResponseBody
    public MSG getLoginUser(@RequestAttribute("user")User user) {
        return user == null ? MSG.fail("未登录") : MSG.success("已登录", user);
    }


    @PostMapping("/register")
    public MSG register(User user) {
        return MSG.success("注册成功");
    }
}
