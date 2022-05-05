package com.lj.cloudbox.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.UUID;


@Component
@ConfigurationProperties(prefix = "settings")
@Data
public class ProjectSettings {
    private String root;
    private String dump;
    private String space;

    public String createHome() {
        String fileRoot = pathUnit(root);
        String dumpRoot = pathUnit(dump);
        File userHome;
        File dumpHome;
        UUID uuid = null;
        String homeFileName;
        do {
            uuid = UUID.randomUUID();
            homeFileName = uuid.toString();
            userHome = new File(pathConcat(fileRoot, homeFileName));
            dumpHome = new File(pathConcat(dumpRoot, homeFileName));
        } while (userHome.exists() || dumpHome.exists());
        userHome.mkdirs();
        dumpHome.mkdirs();
        return homeFileName;
    }

    public String pathUnit(String path) {
        if (path == null || "".equals(path)) return "";
        return path.replaceAll("\\\\", "/").replaceAll("^[/]+", "").replaceAll("[/]+$", "");
    }

    public String pathConcat(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            String unitPath = pathUnit(path);
            if (StringUtils.hasLength(unitPath)) {
                sb.append(unitPath);
                sb.append("/");
            }
        }
        String res = sb.toString();
        return StringUtils.hasLength(res) ? res.substring(0, res.length() - 1) : "";
    }

    private String pathConcatWithRoot(String... paths) {
        return pathConcat(addHead(paths, root));
    }

    private String pathConcatWithDump(String... paths) {
        return pathConcat(addHead(paths, dump));
    }

    //头部插入函数
    private String[] addHead(String[] arr, String value) {
        String[] res = new String[arr.length + 1];
        System.arraycopy(arr, 0, res, 1, arr.length);
        res[0] = value;
        return res;
    }

    public String getRealPath(String... paths) {
        return pathConcatWithRoot(paths);
    }
}
