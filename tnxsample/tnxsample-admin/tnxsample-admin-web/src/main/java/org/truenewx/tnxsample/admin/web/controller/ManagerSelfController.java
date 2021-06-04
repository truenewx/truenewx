package org.truenewx.tnxsample.admin.web.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.service.ManagerService;
import org.truenewx.tnxsample.admin.service.model.ManagerSelfCommand;
import org.truenewx.tnxsample.admin.web.model.LeastManager;
import org.truenewx.tnxsample.admin.web.rpc.FssMetaClient;
import org.truenewx.tnxsample.admin.web.util.ProjectWebUtil;

@Caption("当前个人管理")
@RestController
@RequestMapping("/manager/self")
public class ManagerSelfController {
    @Autowired
    private ManagerService managerService;
    @Autowired
    private FssMetaClient fssMetaClient;

    @Caption("获取个人已获权限集")
    @GetMapping("/authorities")
    @ConfigAuthority
    public Collection<? extends GrantedAuthority> authorities() {
        return loadManager().getAuthorities();
    }

    @Caption("获取基本信息最少量")
    @GetMapping("/least")
    @ConfigAuthority
    public LeastManager least() {
        Manager manager = loadManager();
        LeastManager lm = new LeastManager(manager);
        String headImageUrl = manager.getHeadImageUrl();
        if (StringUtils.isNotBlank(headImageUrl)) {
            lm.setHeadImageUrl(this.fssMetaClient.resolveReadUrl(headImageUrl, true));
        }
        return lm;
    }

    private Manager loadManager() {
        int managerId = ProjectWebUtil.getManagerId();
        return this.managerService.load(managerId);
    }

    @Caption("获取个人信息")
    @GetMapping("/info")
    @ConfigAuthority
    @ResultFilter(included = { "id", "username", "top", "fullName", "headImageUrl" })
    public Manager info() {
        return loadManager();
    }

    @Caption("修改个人信息")
    @ConfigAuthority
    @PostMapping("/info")
    public void updateInfo(@Valid @RequestBody ManagerSelfCommand command) {
        int managerId = ProjectWebUtil.getManagerId();
        this.managerService.updateSelf(managerId, command);
    }

    @Caption("修改密码")
    @ConfigAuthority
    @PostMapping("/password")
    public void updatePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        int managerId = ProjectWebUtil.getManagerId();
        this.managerService.updatePassword(managerId, oldPassword, newPassword);
    }

}
