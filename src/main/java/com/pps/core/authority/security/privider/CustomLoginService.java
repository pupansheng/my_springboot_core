
package com.pps.core.authority.security.privider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义权限相关动作
 * @author Pu PanSheng, 2021/5/11
 * @version OPRA v1.0
 */
public interface CustomLoginService {


    /**
     * 登陆成功 返回的额外的数据
     * @param username
     * @return
     */
    Map   getUserExtraInfo(String username);

    /**
     * 登陆失败自定义处理
     * @param username
     * @return
     */
    Map   LoginFail(String username);

    /**
     * 登陆退出自定义
     * @param request
     * @param response
     */
    void   logoutCustom(HttpServletRequest request, HttpServletResponse response);

}
