package com.pps.core.authority.security.component.common;

/**
 * @author
 * @discription; 自定义权限控制
 * @time 2020/5/14 23:19
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.entity.SysPermisson;
import com.pps.core.authority.security.entity.SysRole;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysPermissonDao;
import com.pps.core.authority.security.mapper.SysRoleDao;
import com.pps.core.authority.security.mapper.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @description: 对于页面级别的权限控制器
   @params:
 * @return:
 * @author:
 * @time: 2020/8/25 16:47
 */

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    SysPermissonDao sysPermissonDao;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        // 获得loadUserByUsername()方法的结果
        String username = (String) authentication.getPrincipal();


        // 获得loadUserByUsername()中注入的角色        待优化
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getName,username);
        SysUser currentUser = sysUserDao.selectOne(queryWrapper);

        // 遍历用户所有角色
        QueryWrapper<SysRole> sysRoleQueryWrapper=new QueryWrapper<>();
        sysRoleQueryWrapper.lambda().eq(SysRole::getUserId,currentUser.getId());
        List<SysRole> sysRoles = sysRoleDao.selectList(sysRoleQueryWrapper);
        for(SysRole role:sysRoles){

            QueryWrapper<SysPermisson> permissonQueryWrapper=new QueryWrapper<>();
            permissonQueryWrapper.lambda().eq(SysPermisson::getSysRoleId,role.getId());
            List<SysPermisson> sysPermissons=sysPermissonDao.selectList(permissonQueryWrapper);
            for(SysPermisson permisson:sysPermissons){
                if(targetUrl.equals(permisson.getPage())
                        && permisson.getActionArray().contains(targetPermission)) {
                    return true;
                }
            }

        }

        return  false;

    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
