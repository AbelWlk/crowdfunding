package com.wlk.crowd.mvc.config;

import com.wlk.crowd.entity.Admin;
import com.wlk.crowd.entity.Role;
import com.wlk.crowd.service.AdminService;
import com.wlk.crowd.service.AuthService;
import com.wlk.crowd.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrowdUserDetailService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthService authService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1.根据账号名称查询 Admin 对象
        Admin admin = adminService.getAdminByLoginAcct(username);
        // 2.获取 adminId
        Integer adminId = admin.getId();
        // 3.根据 adminId 查询角色信息
        List<Role> assignedRoleList = roleService.getAssignedRole(adminId);
        // 4.根据 adminId 查询权限信息
        List<String> authNameList = authService.getAssignedAuthNameByAdminId(adminId);
        // 5.创建集合对象用来存储 GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 6.遍历 assignedRoleList 存入角色信息
        for (Role role : assignedRoleList) {
            // 注意：不要忘了加前缀！
            String roleName = "ROLE_" + role.getName();
            SimpleGrantedAuthority simpleGrantedAuthority = new
                    SimpleGrantedAuthority(roleName);
            authorities.add(simpleGrantedAuthority);
        }
        // 7.遍历 authNameList存入权限信息
        for (String authName : authNameList) {
            SimpleGrantedAuthority simpleGrantedAuthority = new
                    SimpleGrantedAuthority(authName);
            authorities.add(simpleGrantedAuthority);
        }
        // 8.封装 SecurityAdmin 对象
        SecurityAdmin securityAdmin = new SecurityAdmin(admin, authorities);
        return securityAdmin;
    }
}

