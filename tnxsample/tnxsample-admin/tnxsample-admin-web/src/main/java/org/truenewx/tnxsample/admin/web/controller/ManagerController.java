package org.truenewx.tnxsample.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;
import org.truenewx.tnxsample.admin.service.ManagerService;
import org.truenewx.tnxsample.admin.service.model.ManagerCommand;
import org.truenewx.tnxsample.common.constant.ManagerRanks;
import org.truenewx.tnxsample.common.constant.UserTypes;

/**
 * 管理员管理
 */
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping("/list")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    @ResultFilter(type = Role.class, included = { "id", "name" })
    public QueryResult<Manager> list(@RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {
        return this.managerService.queryGeneral(keyword, pageSize, pageNo);
    }

    @PostMapping("/add")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    public void add(@RequestBody ManagerCommand command) {
        this.managerService.add(command);
    }

    @GetMapping("/{id}")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    @ResultFilter(type = Role.class, included = { "id", "name" })
    public Manager detail(@PathVariable("id") int id) {
        return this.managerService.load(id);
    }

    @PostMapping("/{id}/update")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    public void update(@PathVariable("id") int id, @RequestBody ManagerCommand command) {
        this.managerService.update(id, command);
    }

    @PostMapping("/{id}/update-disabled")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    public void updateDisabled(@PathVariable("id") int id, @RequestParam("disabled") boolean disabled) {
        this.managerService.updateDisabled(id, disabled);
    }

    @PostMapping("/{id}/reset-password")
    @ConfigAuthority(type = UserTypes.MANAGER, rank = ManagerRanks.TOP)
    public void resetPassword(@PathVariable("id") int id, @RequestParam("password") String password) {
        this.managerService.resetPassword(id, password);
    }

}
