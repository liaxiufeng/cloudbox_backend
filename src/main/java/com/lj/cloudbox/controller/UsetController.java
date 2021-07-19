package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.MSG;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class UsetController {

    @Autowired
    UserMapper userMapper;

    @PostMapping("/login")
    public MSG login(User user) {
        if (userMapper.canLogin(user)) {
            User loginUser = userMapper.loginReturnUser(user);
            String uid = loginUser.getUid() + "";
            String token = TokenUtil.sign( uid);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return MSG.success("登录成功", map);
        }
        return MSG.fail("登录失败");
    }

    @GetMapping("/user")
    @ResponseBody
    public MSG loginUser(@RequestHeader(value = "token",required = false,defaultValue = "")String token) {
        if (!StringUtils.hasLength(token)) return MSG.fail("未登录");
        try {
            String uid = TokenUtil.decode(token);
            if (StringUtils.hasLength(uid)){
                User user = userMapper.selectById(Integer.parseInt(uid));
                return MSG.success("已登录", user);
            }else {
                return MSG.fail("未登录");
            }
        }catch (NumberFormatException e){
            return MSG.fail("未登录");
        }
    }

    @GetMapping("/isLogin")
    @ResponseBody
    public MSG isLogin(@RequestHeader(value = "token",required = false,defaultValue = "")String token) {
        if (!StringUtils.hasLength(token)) return MSG.fail("未登录");
        try {
            String uid = TokenUtil.decode(token);
            return StringUtils.hasLength(uid) ? MSG.success("已登录") : MSG.fail("未登录");
        }catch (NumberFormatException e){
            return MSG.fail("未登录");
        }
    }

}
