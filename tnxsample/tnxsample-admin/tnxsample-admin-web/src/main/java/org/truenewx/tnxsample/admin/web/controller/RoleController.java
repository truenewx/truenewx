package org.truenewx.tnxsample.admin.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;
import org.truenewx.tnxsample.admin.service.RoleService;
import org.truenewx.tnxsample.admin.service.model.RoleCommand;
import org.truenewx.tnxsample.admin.web.model.ListRole;
import org.truenewx.tnxsample.common.constant.ManagerRanks;
import org.truenewx.tnxsample.common.constant.UserTypes;

/**
 * 角色管理
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @ConfigAuthority(type = UserTypes.MANAGER,
            rank = ManagerRanks.TOP)
    @ResultFilter(type = Manager.class, included = { "id", "jobNo", "username", "fullName" })
    public List<ListRole> list(@RequestParam(name = "name", required = false) String name) {
        List<Role> roles = this.roleService.findByName(name);
        return roles.stream().map(role -> {
            ListRole lr = new ListRole(role);
            QueryResult<Manager> qr = this.roleService.queryManagers(role.getId(), 10, 1);
            lr.setManagerNum(qr.getPaged().getTotal());
            lr.setManagers(qr.getRecords());
            return lr;
        }).collect(Collectors.toList());
    }

    @PostMapping("/add")
    @ConfigAuthority(type = UserTypes.MANAGER,
            rank = ManagerRanks.TOP)
    public void add(@RequestBody RoleCommand command) {
        this.roleService.add(command);
    }

    @GetMapping("/{id}")
    @ConfigAuthority(type = UserTypes.MANAGER,
            rank = ManagerRanks.TOP)
    @ResultFilter(type = Manager.class, included = { "id", "jobNo", "username", "fullName" })
    public Role detail(@PathVariable("id") int id) {
        return this.roleService.load(id);
    }

    @PostMapping("/{id}/update")
    @ConfigAuthority(type = UserTypes.MANAGER,
            rank = ManagerRanks.TOP)
    public void update(@PathVariable("id") int id, @RequestBody RoleCommand command) {
        this.roleService.update(id, command);
    }

    @PostMapping("/{id}/delete")
    @ConfigAuthority(type = UserTypes.MANAGER,
            rank = ManagerRanks.TOP)
    public void delete(@PathVariable("id") int id) {
        this.roleService.delete(id);
    }

}
