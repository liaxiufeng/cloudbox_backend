package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.FileMapper;
import com.lj.cloudbox.pojo.FileBean;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.msg.Result;
import com.lj.cloudbox.pojo.vo.FileTree;
import com.lj.cloudbox.utils.date.DateUtils;
import com.lj.cloudbox.utils.file.FileSizeFormatUtil;
import com.lj.cloudbox.utils.ProjectSettings;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class FileService {
    @Autowired
    ProjectSettings projectSettings;

    @Autowired
    UserService userService;

    @Autowired
    FileMapper fileMapper;

/*
    基本功能性方法
*/

    /**
     * 获取文件的路径文件
     *
     * @param fileBean 文件
     * @return 文件的路径上的文件，前一个为后一个的父级文件
     */
    public List<FileBean> getPathFiles(FileBean fileBean) {
        if (fileBean == null) return null;
        List<FileBean> location = new LinkedList<>();
        FileBean temp = fileBean;
        try {
            while (temp.getParent() != 0) {
                location.add(temp);
                temp = fileMapper.selectById(temp.getParent());
            }
        } catch (NullPointerException e) {
            Collections.reverse(location);
            return location;
        }
        Collections.reverse(location);
        return location;
    }

    /**
     * 通过路径文件得到路径字符串
     *
     * @param location 路径文件
     * @return 路径字符串
     */
    public String getPath(List<FileBean> location) {
        if (location == null) return null;
        String[] locationStr = new String[location.size()];
        for (int i = 0; i < location.size(); i++) {
            locationStr[i] = location.get(i).getName();
        }
        return projectSettings.pathConcat(locationStr);
    }

    /**
     * 获取文件的路径字符串
     *
     * @param fileBean 文件
     * @return 路径字符串
     */
    public String getPath(FileBean fileBean) {
        return getPath(getPathFiles(fileBean));
    }

    /**
     * 创建一个新的用户的家目录，文件名为随机的uuid
     *
     * @param uid 用户id
     * @return 家文件
     */
    public FileBean createHome(Integer uid) {
        String home = projectSettings.createHome();
        FileBean homeFile = new FileBean();
        homeFile.setName(home);
        homeFile.setUid(uid);
        homeFile.setIsFolder(true);
        homeFile.insert();
        return homeFile;
    }

    /**
     * 包装文件的大小或其他信息
     *
     * @param userHome    用户家文件名
     * @param fileBean    文件
     * @param locationStr 路径字符串
     */
    public void packaging(String userHome, FileBean fileBean, String locationStr) {
        String realPath = projectSettings.getRealPath(userHome, locationStr, fileBean.getName());
        File file = new File(realPath);
        if (!file.exists()) return;
        long fileSize = FileUtils.sizeOf(file);
        fileBean.setSize(FileSizeFormatUtil.formatFileSize(fileSize));
        fileBean.setIsEmpty(fileSize == 0);
        fileBean.setLastUpdateDate(DateUtils.parse_total(file.lastModified()));
        fileBean.setIsHeart(false);
    }

    /**
     * 获取文件树
     *
     * @param fileBean 文件
     * @return 文件树
     */
    public FileTree getTreeFiles(FileBean fileBean) {
        if (fileBean == null) return null;
        FileTree fileTree = new FileTree();
        fileTree.setFile(fileBean);
        packagingTreeFiles(fileTree);
        return fileTree;
    }

    private void packagingTreeFiles(FileTree parent) {
        FileBean fileBean = parent.getFile();
        if (fileBean.getIsFolder()) {
            List<FileBean> children = fileMapper.getChildren(fileBean.getFid());
            if (children == null || children.size() == 0) {
                parent.setHasChild(false);
                parent.setChildrenNumber(0);
                return;
            }
            LinkedList<FileTree> fileTrees = new LinkedList<>();
            int childrenNumber = 0;
            for (FileBean child : children) {
                if (child == null) continue;
                FileTree fileTree = new FileTree();
                fileTree.setFile(child);
                packagingTreeFiles(fileTree);
                fileTrees.add(fileTree);
                childrenNumber += fileTree.getChildrenNumber() + 1;
            }
            parent.setHasChild(true);
            parent.setChildren(fileTrees);
            parent.setChildrenNumber(childrenNumber);
        } else {
            parent.setHasChild(false);
            parent.setChildrenNumber(0);
        }
    }

    /**
     * 通过id数组获取文件数组
     *
     * @param fids id数组
     * @return 文件数组
     */
    public List<FileBean> getFiles(Integer[] fids) {
        for (Integer fid : fids) {
            if (fid < 0) return null;
        }
        return fileMapper.selectBatchIds(Arrays.asList(fids));
    }

    /**
     * 判断操作的文件是否为登录用户的文件夹
     *
     * @param uid   登录用户id
     * @param files 文件列表
     * @return 是否为同一个用户
     */
    public Boolean isSameUser(Integer uid, List<FileBean> files) {
        for (FileBean fileBean : files) {
            if (!uid.equals(fileBean.getUid())) return false;
        }
        return true;
    }

    public Boolean isSameUser(Integer uid, FileBean... files) {
        for (FileBean fileBean : files) {
            if (!uid.equals(fileBean.getUid())) return false;
        }
        return true;
    }

    public File realFile(FileBean homeFile,FileBean fileBean){
        String path = getPath(fileBean);
        String realPath = projectSettings.getRealPath(homeFile.getName(), path);
        return new File(realPath);
    }
/*
    业务类方法
*/

    /**
     * 获取目标文件下的文件列表
     *
     * @param userHome 用户家文件名
     * @param fid      目标文件
     * @return 文件列表及其他信息
     */
    public Map<String, Object> getChildrenFiles(String userHome, Integer fid) {
        FileBean parent = fileMapper.selectById(fid);
        if (parent == null) return null;
        List<FileBean> locationFiles = getPathFiles(parent);

        String locationStr = getPath(locationFiles);
        List<FileBean> children = fileMapper.getChildren(fid);
        for (FileBean child : children) {
            packaging(userHome, child, locationStr);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("locationFiles", locationFiles);
        result.put("location", locationStr);
        result.put("fileData", children);
        return result;
    }

    /**
     * 复制或剪切文件时，检查有无命名冲突，如果无，直接进行文件操作
     *
     * @param user    用户
     * @param srcFids 源文件id数组
     * @param destFid 目标文件id
     * @param isCopy  是否为复制，false为剪切
     * @return MSG消息类型
     */
    public MSG checkMove(User user, Integer[] srcFids, Integer destFid, Boolean isCopy) {
        Integer uid = user.getUid();
        List<FileBean> srcFiles = getFiles(srcFids);
        if (srcFiles == null) return MSG.fail("源文件的文件不存在！");
        FileBean destFile;
        if (destFid == 0) {
            destFile = user.getHomeFile();
        } else {
            destFile = fileMapper.selectById(destFid);
        }
        if (destFile == null) return MSG.fail("目的文件不存在！");
        if (!destFile.getIsFolder()) return MSG.fail("目的文件不是一个文件夹！");
        for (FileBean fileBean : srcFiles) {
            Integer parent = fileBean.getParent();
            if (parent == null) return MSG.fail("该文件不支持移动！");
            if (parent.equals(destFile.getFid())) return MSG.fail("文件已经存在该路径下");
        }
        if (uid.equals(destFile.getUid()) && isSameUser(uid, srcFiles)) {
            List<FileBean> conflict = new LinkedList<>();
            checkMoveFileCheckNameConflict(srcFiles, destFile, conflict);
            if (conflict.size() == 0) {
                return MSG.success("无冲突", moveFiles(user, srcFiles, destFile, isCopy, true));
            } else {
                return MSG.conflict("存在命名冲突", conflict);
            }
        } else {
            return MSG.fail("不能跨用户进行文件操作！");
        }
    }

    private void checkMoveFileCheckNameConflict(List<FileBean> srcFiles, FileBean destFile, List<FileBean> conflict) {
        if (srcFiles == null) return;
        for (FileBean srcFile : srcFiles) {
            checkMoveFileCheckNameConflict(getTreeFiles(srcFile), getTreeFiles(destFile), conflict);
        }
    }

    private void checkMoveFileCheckNameConflict(FileTree srcFile, FileTree destFile, List<FileBean> conflict) {
        FileBean srcFileBean = srcFile.getFile();
        if (!destFile.getHasChild()) return;
        FileBean destFileBean = destFile.getFile();
        List<FileTree> srcChildren = srcFile.getChildren();
        List<FileTree> destChildren = destFile.getChildren();
        if (!srcFileBean.getIsFolder() && destChildren != null){
            FileBean srcCopy = new FileBean(srcFileBean);
            srcCopy.setParent(destFileBean.getFid());
            for (FileTree destChild : destChildren) {
                if (srcCopy.equals(destChild.getFile())) {
                    conflict.add(srcFileBean);
                }
            }
        }
        if (srcChildren != null && destChildren != null){
            for (FileTree srcChild : srcChildren) {
                for (FileTree destChild : destChildren) {
                    checkMoveFileCheckNameConflict(srcChild, destChild, conflict);
                }
            }
        }

    }

    /**
     * 复制或剪切多个源文件到一个目标文件中
     *
     * @param user     登录用户
     * @param srcFids  多个源文件id
     * @param destFid  目标文件id
     * @param isCopy   是否为复制，否为剪切
     * @param override 命名冲突是否为覆盖，否为忽略
     * @return MSG消息
     */
    public MSG moveFiles(User user, Integer[] srcFids, Integer destFid, Boolean isCopy, Boolean override) {
        Integer uid = user.getUid();
        List<FileBean> srcFiles = getFiles(srcFids);
        if (srcFiles == null) return MSG.fail("源文件的文件不存在！");
        FileBean destFile;
        if (destFid == 0) {
            destFile = user.getHomeFile();
        } else {
            destFile = fileMapper.selectById(destFid);
        }
        if (destFile == null) return MSG.fail("目的文件不存在！");
        if (uid.equals(destFile.getUid()) && isSameUser(uid, srcFiles)) {
            return MSG.success("完成", moveFiles(user, srcFiles, destFile, isCopy, override));
        } else {
            return MSG.fail("不能跨用户进行文件操作！");
        }
    }


    private Map<String, List<Result>> moveFiles(User user, List<FileBean> srcFiles, FileBean destFile, Boolean isCopy, Boolean override) {
        String destPath = getPath(destFile);
        String destRealPath = projectSettings.getRealPath(user.getHomeFile().getName(), destPath);
        File destRealFile = new File(destRealPath);
        List<Result> success = new LinkedList<>();
        List<Result> fail = new LinkedList<>();
        for (FileBean srcFile : srcFiles) {
            String srcPath = getPath(srcFile);
            String srcRealPath = projectSettings.getRealPath(user.getHomeFile().getName(), srcPath);
            File srcRealFile = new File(srcRealPath);
            if (override) {
                moveFileByOverride(srcFile, destFile, srcRealFile, destRealFile, isCopy, success, fail);
            } else {
                FileTree src = getTreeFiles(srcFile);
                FileTree dest = getTreeFiles(destFile);
                moveFileByIgnore(src, dest, srcRealFile, destRealFile, isCopy, success, fail);
            }
        }
        Map<String, List<Result>> map = new HashMap<>();
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    private Integer moveFileByIgnore(FileTree src, FileTree dest, File srcRealFile, File destRealFile, Boolean isCopy, List<Result> success, List<Result> fail) {
        if (!destRealFile.exists() || !srcRealFile.exists() || !destRealFile.isDirectory() || src == null || dest == null)
            return 0;
        int actioned = 0;
        FileBean srcFile = src.getFile();
        FileBean destFile = dest.getFile();

        FileBean srcCopy = new FileBean(srcFile);
        srcCopy.setParent(destFile.getFid());
        FileBean sameName = fileMapper.sameName(srcCopy);
        FileTree destTemp = null;
        boolean conflict = sameName != null;
        if (conflict) {
            for (FileTree child : dest.getChildren()) {
                if (sameName.equals(child.getFile())) destTemp = child;
            }
            if (destTemp == null) destTemp = getTreeFiles(sameName);
        } else {
            boolean isSuccess = true;
            if (srcFile.getIsFolder()) {
                File file = new File(destRealFile, srcFile.getName());
                if (!file.exists()) {
                    file.mkdirs();
                }
            } else {
                try {
                    FileUtils.copyFileToDirectory(srcRealFile, destRealFile);
                } catch (IOException e) {
                    isSuccess = false;
                    e.printStackTrace();
                }
            }
            if (isSuccess) {
                srcCopy.insert();

                destTemp = new FileTree();
                destTemp.setFile(srcCopy);
                destTemp.setHasChild(false);
                destTemp.setChildrenNumber(0);

                success.add(Result.success(srcFile, (isCopy ? "复制" : "剪切") + "文件成功！"));
                actioned = 1;
            } else {
                fail.add(Result.fail(srcFile, (isCopy ? "复制" : "剪切") + "文件失败！"));
                actioned = 0;
            }
        }
        if (src.getHasChild()) {
            List<FileTree> children = src.getChildren();
            for (FileTree child : children) {
                actioned += moveFileByIgnore(child, destTemp, new File(srcRealFile, child.getFile().getName()), new File(destRealFile, srcFile.getName()), isCopy, success, fail);
            }
        }
        if (!isCopy && actioned == src.getChildrenNumber() + 1) {
            srcFile.deleteById();
            srcRealFile.delete();
        }
        return actioned;
    }

    private void moveFileByOverride(FileBean srcFile, FileBean destFile, File srcRealFile, File destRealFile, Boolean isCopy, List<Result> success, List<Result> fail) {
        try {
            if (isCopy) {
                if (srcRealFile.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(srcRealFile, destRealFile);
                } else {
                    FileUtils.copyFileToDirectory(srcRealFile, destRealFile);
                }
                copyFileBean(srcFile, destFile);
            } else {
                FileUtils.moveToDirectory(srcRealFile, destRealFile, false);
                cutFileBeanByOverride(srcFile, destFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail.add(new Result(srcFile, false, (isCopy ? "复制" : "剪切") + "文件出错"));
        }
        success.add(new Result(srcFile, true, (isCopy ? "复制" : "剪切") + "文件成功"));
    }


    private void copyFileBean(FileBean srcFile, FileBean destFile) {
        FileBean srcCopy = new FileBean(srcFile);
        srcCopy.setParent(destFile.getFid());
        FileBean sameName = fileMapper.sameName(srcCopy);
        if (sameName == null) {
            srcCopy.setFid(null);
            srcCopy.insert();
        } else {
            srcCopy = sameName;
        }
        if (srcFile.getIsFolder()) {
            List<FileBean> children = fileMapper.getChildren(srcFile.getFid());
            if (children != null && children.size() > 0) {
                for (FileBean child : children) {
                    copyFileBean(child, srcCopy);
                }
            }
        }
    }

    private void cutFileBeanByOverride(FileBean srcFile, FileBean destFile) {
        copyFileBean(srcFile, destFile);
        fileMapper.deleteById(srcFile.getFid());
    }

    /**
     * 重命名文件
     *
     * @param user    用户
     * @param fid     文件id
     * @param newName 新文件名
     * @return MSG消息
     */
    public MSG reName(User user, Integer fid, String newName) {
        if (!StringUtils.hasLength(newName)) return MSG.fail("文件名不能为空！");
        FileBean fileBean = fileMapper.selectById(fid);
        Integer parent;
        try {
            parent = fileBean.getParent();
        } catch (NullPointerException e) {
            return MSG.fail("用户的根目录不允许改名！");
        }
        if (!fileMapper.checkNewName(user.getUid(), parent, fileBean.getIsFolder(), newName))
            return MSG.fail("已存在该名字的文件或文件夹！");
        String path = getPath(fileBean);
        String realPath = projectSettings.getRealPath(user.getHomeFile().getName(), path);
        String location = realPath.replaceAll("/[^/]+$", "");
        String newPath = projectSettings.pathConcat(location, newName);
        File srcFile = new File(realPath);
        if (!srcFile.exists()) return MSG.fail("文件不存在！");
        if (fileMapper.reName(fid, newName) == 1) {
            srcFile.renameTo(new File(newPath));
            return MSG.success("文件夹重命名成功！！");
        } else {
            return MSG.fail("文件夹重命名失败！");
        }
    }

    public Map<String, List<FileBean>> delete(User user, Integer[] fids){
        Map<String, List<FileBean>> map = new HashMap<>();
        List<FileBean> success = new LinkedList<>();
        List<FileBean> fail = new LinkedList<>();
        for (Integer fid:fids){
            FileBean fileBean = fileMapper.selectById(fid);
            if (fileBean.getUid().equals(user.getUid())){
                File realFile = realFile(user.getHomeFile(), fileBean);
                delete(fileBean,realFile,success,fail);
            }else {
                fail.add(fileBean);
            }
        }
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    public void delete(FileBean fileBean,File realFile,List<FileBean> success,List<FileBean> fail){
        if (fileMapper.deleteById(fileBean.getFid()) == 1){
            if (realFile.exists()){
                FileUtils.deleteQuietly(realFile);
            }
            success.add(fileBean);
        }else {
            fail.add(fileBean);
        }
    }




//    private String getTextFile(String totalPath) throws IOException {
//        return FileUtils.readFileToString(new File(totalPath));
//    }
//
//    private String getImageFile(String totalPath) {
//        return ImageUtils.getBase64(totalPath);
//    }
//
//    public Object getFile(String totalPath) throws IOException {
//        String[] split = totalPath.split("\\.");
//        String fileSuffix = split.length != 1 ? split[split.length - 1] : "";
//        final String[] img = new String[]{"ico", "gif", "cur", "png", "jpg", "jpeg", "webp"};
//        final String[] mp3 = new String[]{};
//        final String[] mp4 = new String[]{};
//        URLConnection.guessContentTypeFromStream(new FileInputStream(totalPath));
//        if ("".equals(fileSuffix)) {
//            return null;
//        }
//        return null;
//    }
//
//  public FileBean createFile(Integer parentId, String fileName, Boolean isFolder) {
//        return null;
//    }
//    public Result uploadFile(Boolean override, User user, MultipartFile file, String location, String fileName) {
//        userService.packaging_space(user);
//        if (user.getFreeSpaceLong() <= 0) return new Result(fileName, false, "无可用空间！");
//
//        if (file.isEmpty()) return new Result(fileName, false, "文件为空！");
//        String path;
//        String fileNameTemp = StringUtils.hasLength(fileName) ? fileName : file.getOriginalFilename();
//        path = "";
//        try {
//            File saveFile = new File(path);
//            if (!override && saveFile.exists()) {
//                return new Result(fileNameTemp, false, "文件已存在！");
//            }
//            file.transferTo(saveFile);
//            return new Result(fileNameTemp, true, "文件保存成功！");
//        } catch (IOException e) {
//            return new Result(fileNameTemp, true, "文件保存失败！");
//        }
//    }
//
//    public Map<String, List<Result>> uploadFiles(Boolean override, User user, MultipartFile[] files, String location) {
//        Result saveResult;
//        List<Result> success = new LinkedList<>();
//        List<Result> fail = new LinkedList<>();
//        for (MultipartFile file : files) {
//            saveResult = uploadFile(override, user, file, location, file.getOriginalFilename());
//            if (saveResult.isSuccess()) {
//                success.add(saveResult);
//            } else {
//                fail.add(saveResult);
//            }
//        }
//        Map<String, List<Result>> map = new HashMap<>();
//        map.put("success", success);
//        map.put("fail", fail);
//        return map;
//    }
}
