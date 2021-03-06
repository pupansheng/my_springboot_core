package com.pps.core.authority.security.component.logincomponent.form;

import com.alibaba.fastjson.JSON;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.core.authority.security.entity.SysRole;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysRoleDao;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.core.authority.security.property.MySecurityProperty;
import com.pps.core.common.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 * @discription;
 * @time 2020/5/13 16:51
 */
@Service(value = "customUserDetailsServiceForPc")
@Slf4j
public class CustomUserDetailsServiceForPc implements UserDetailsService {

    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    MySecurityProperty mySecurityProperty;



    @Override
    public UserDetails loadUserByUsername(String username) throws ServiceException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        List<SysUser> user=new ArrayList<>();
        List<SysRole> sysRole=new ArrayList<>();
        if(mySecurityProperty.getOpenConfigUser()){

            String configUser = mySecurityProperty.getConfigUser();
            if(ValidateUtil.isEmpty(configUser)){
                log.info("??????????????????????????????  ????????????????????????????????????");
            }else {
                List<Map> maps = JSON.parseArray(configUser, Map.class);
                List<Map> userMap = maps.stream().filter(p -> p.get("username").equals(username)).collect(Collectors.toList());
                if(ValidateUtil.isNotEmpty(userMap)){
                    SysUser sysUser=new SysUser();
                    sysUser.setName((String)userMap.get(0).get("username"));
                    sysUser.setPassword((String)userMap.get(0).get("password"));
                    user.add(sysUser);
                    SysRole sysRole1=new SysRole();
                    sysRole1.setId(1L);
                    sysRole1.setName("ROLE_ADMIN");
                    sysRole.add(sysRole1);
                }
            }
        }

        // ?????????????????????????????????
        if(ValidateUtil.isEmpty(user)) {
            QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUser::getName,username);
            user = sysUserDao.selectList(queryWrapper);
        }
        // ????????????????????????
        if(user==null||user.size()<=0) {
                throw new UsernameNotFoundException("???????????????");
        }
        // ????????????
        if(user.size()>1){
            throw new UsernameNotFoundException("?????????????????? ????????????????????????");
        }

        SysUser logUser=user.get(0);

        //??????????????????
        if(ValidateUtil.isEmpty(sysRole)) {
            QueryWrapper<SysRole> queryWrapper=new QueryWrapper<>();
            queryWrapper.lambda().eq(SysRole::getUserId,logUser.getId());
            sysRole = sysRoleDao.selectList(queryWrapper);
        }
        //????????????
        sysRole.stream().forEach(p->{

            authorities.add(new SimpleGrantedAuthority(p.getName()));

        });
        // ??????UserDetails?????????
        return new User(logUser.getName(), logUser.getPassword(), authorities);
    }
}
