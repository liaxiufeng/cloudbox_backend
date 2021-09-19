package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.FileMapper;
import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.vo.FileTree;
import com.lj.cloudbox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserMapper userMapper;

    @GetMapping("files")
    public MSG filesList(@RequestAttribute("user") User user,
                         @RequestParam("fid") Integer fid) {
        if (fid < 0) return MSG.fail("无效的文件编号");
        String userHome = user.getHomeFile().getName();
        if (fid == 0) {
            Integer homeFileId = user.getHomeFile().getFid();
            return MSG.success("获取文件成功", fileService.getChildrenFiles(userHome, homeFileId));
        } else {
            return MSG.success("获取文件成功", fileService.getChildrenFiles(userHome, fid));
        }
    }

    @GetMapping("file")
    public MSG fileGet(@RequestAttribute("user") User user,
                       @RequestParam("fid") Integer fid) {
        return MSG.success("");
    }

    @PutMapping("rename")
    public MSG renameFile(@RequestAttribute("user") User user,
                          @RequestParam("fid") Integer fid,
                          @RequestParam("newName") String newName) {
        return fileService.reName(user, fid, newName);
    }

    @DeleteMapping("delete")
    public MSG deleteFiles(@RequestAttribute("user") User user,
                           @RequestBody Integer[] fids) {
        return MSG.success("完成", fileService.delete(user, fids));
    }

    @PostMapping("move")
    public MSG checkMove(@RequestAttribute("user") User user,
                         @RequestBody Integer[] srcFids,
                         @RequestParam("dest") Integer destFid,
                         @RequestParam("isCopy") Boolean isCopy) {
        return fileService.checkMove(user, srcFids, destFid, isCopy);
    }

    @PostMapping("move/conflict")
    public MSG moveFiles(@RequestAttribute("user") User user,
                         @RequestBody Integer[] srcFids,
                         @RequestParam("dest") Integer destFid,
                         @RequestParam("isCopy") Boolean isCopy,
                         @RequestParam("override") Boolean override) {
        return fileService.moveFiles(user, srcFids, destFid, isCopy, override);
    }

    @PostMapping("fileUpLoad")
    public MSG fileUpLoad(@RequestPart("file") MultipartFile file,
                          @RequestParam("location") String location,
                          @RequestAttribute("user") User user,
                          @RequestParam(value = "fileName", required = false, defaultValue = "") String fileName,
                          @RequestParam("override") Boolean override) {
        return null;
    }

    @PostMapping("filesUpLoad")
    public MSG filesUpLoad(@RequestPart("files") MultipartFile[] files,
                           @RequestParam("location") String location,
                           @RequestAttribute("user") User user,
                           @RequestParam("override") Boolean override) {
        return null;
    }

    @GetMapping("tree")
    public List<FileTree> getTreeFile(@RequestAttribute("user") User user,
                                      @RequestParam("fid") Integer fid) {
        if (fid == 0) {
            return fileService.getTreeFiles(user.getHomeFile()).getChildren();
        }
        return fileService.getTreeFiles(fileMapper.selectById(fid)).getChildren();
    }
}
