package com.pps.core.authority.security.config;

import com.alibaba.fastjson.JSON;
import com.pps.core.authority.security.component.common.CustomAuthenticationDetailsSource;
import com.pps.core.authority.security.component.common.CustomPermissionEvaluator;
import com.pps.core.authority.security.component.common.MyInvalidSessionStrategy;
import com.pps.core.authority.security.component.filter.CrossOriginFilter;
import com.pps.core.authority.security.component.filter.RequestInfoFilter;
import com.pps.core.authority.security.component.handler.*;
import com.pps.core.authority.security.component.logincomponent.form.CustomAuthenticationProviderForPc;
import com.pps.core.authority.security.component.logincomponent.jwt.JwtAuthenticationFilter;
import com.pps.core.authority.security.component.logincomponent.sms.SmsCodeAuthenticationSecurityConfig;
import com.pps.core.authority.security.privider.CustomLoginService;
import com.pps.core.authority.security.property.JWTUtil;
import com.pps.core.authority.security.property.MySecurityProperty;
import com.pps.core.common.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author
 * @discription;
 * @time 2020/5/13 16:58
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProviderForPc customAuthenticationProviderForPc;
    @Autowired
    private CustomAuthenticationDetailsSource customAuthenticationDetailsSource;
    @Autowired
    private MySecurityProperty mySecurityProperty;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    Environment environment;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired(required = false)
    CustomLoginService customService;

    /**
     * ???????????????PermissionEvaluator  ????????????????????????
     */
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(customAuthenticationProviderForPc);//???????????????

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String context=environment.getProperty("server.servlet.context-path");
        log.info("------------------------------------------------------------------------------------------------");
        log.info("web?????????????????????{}",context);
        log.info("------------------------------------------------------------------------------------------------");
        String [] permitUrl = mySecurityProperty.getPermitUrl();
        Boolean openVerifyCode = mySecurityProperty.getOpenVerifyCode();

        if(openVerifyCode){

            if(ValidateUtil.isNotEmpty(mySecurityProperty.getVerifyCodeUrl())){
                List<String> newUrl=new ArrayList<>( Arrays.asList(permitUrl));
                newUrl.add(mySecurityProperty.getVerifyCodeUrl());
                permitUrl=newUrl.toArray(new String[newUrl.size()]);
            }else {

                throw new RuntimeException("????????????????????????  ???????????????????????????????????????");
            }
        }

        if(mySecurityProperty.getSms()!=null&&ValidateUtil.isNotEmpty(mySecurityProperty.getSms().getSmsMessage())){
            List<String> newUrl=new ArrayList<>( Arrays.asList(permitUrl));
            newUrl.add(mySecurityProperty.getSms().getSmsMessage());
            permitUrl=newUrl.toArray(new String[newUrl.size()]);
        }


        log.info("??????????????????---------------------------------------------------------------------------------------------");
                log.info("?????????springSecurity: ");
                log.info("??????????????????url?????????----------------------------------------------------------------------------");
                for(String u:permitUrl) {
                    log.info(u);
                }
                log.info("-----------------------------------------------------------------------------------------------");
                log.info("??????????????????{}",mySecurityProperty.getLoginUrl());
                log.info("??????????????????{}", mySecurityProperty.getLogoutUrl());
                log.info("???????????????????????????{}",mySecurityProperty.getFailureUrl());


                //???????????????????????? o???Authentication
                BiFunction<Object, Map,Map> formSuccessfunction=(o,m)->{

                    Authentication authentication=(Authentication)o;
                    Map response=new HashMap();
                    response.put("userName",authentication.getPrincipal());
                    response.put("authList",authentication.getAuthorities());
                    if(jwtUtil.isEnable()){
                        log.info("?????????jwt???????????????????????????token--------------");
                        String token = jwtUtil.generateToken(JSON.toJSONString(response));
                        log.info("token:  "+token);
                        log.info("token???????????????"+ JSON.toJSONString(response));
                        m.put("token",token);
                    }
                    //?????????????????????
                    if(customService!=null){
                        Map userExtraInfo = customService.getUserExtraInfo((String) authentication.getPrincipal());
                        response.put("userInfo",userExtraInfo);
                    }
                    m.putAll(response);
                    return  m;

                };
                //???????????????????????? o???Authentication
                BiFunction<AuthenticationException, Map,Map> formFailfunction=(e, m)->{
                  if(customService!=null){
                      customService.LoginFail(e,m);
                  }
                  return  m;
               };
                BiConsumer<HttpServletRequest, HttpServletResponse> loginOutDone=(res, response)->{
                 if(customService!=null){
                     customService.logoutCustom(res,response);
                 }
               };


        http.apply(smsCodeAuthenticationSecurityConfig).and().
                 authorizeRequests()
                // ????????????????????????url???????????????
                .antMatchers(permitUrl).permitAll()
                //?????????????????????????????????
                .anyRequest().authenticated().and()
                //??????????????? customAuthenticationDetail
                .formLogin().authenticationDetailsSource(customAuthenticationDetailsSource)
                //???????????? ????????????????????????????????????
                .loginPage(mySecurityProperty.getLoginUrl()).loginProcessingUrl(mySecurityProperty.getLoginUrl()).successHandler(new MyAuthenticationSuccessHandler("??????",mySecurityProperty,jwtUtil,formSuccessfunction))
                //?????????????????????????????????
                .failureUrl(mySecurityProperty.getFailureUrl()).failureHandler(new MyAuthentionFailHandler("??????",formFailfunction)).and()
                //?????????????????????
                .logout().logoutUrl(mySecurityProperty.getLogoutUrl()).logoutSuccessHandler(new MyLogoutSuccessHandler(loginOutDone)).deleteCookies("JSESSIONID").invalidateHttpSession(true).and()
                //???????????????????????? ?????????   ?????? ????????????  ??????????????????
                .exceptionHandling().authenticationEntryPoint(new NoAuthenticationEntryPoint()).accessDeniedHandler(new MyAccessDenHandler()).and()
                //session????????????????????????
                .sessionManagement().invalidSessionStrategy(new MyInvalidSessionStrategy());


        //?????????????????????  ??????????????????????????????
        if(mySecurityProperty.getOpenRequestLog()) {

            log.info("???????????????????????????????????? ?????????????????????????????????????????????");
            http.addFilterBefore(new RequestInfoFilter(), SecurityContextPersistenceFilter.class);
        }

        if(jwtUtil.isEnable()) {
            log.info("?????????jwt????????? ????????????jwt?????????");
            JwtAuthenticationFilter jwtAuthenticationFilter=new JwtAuthenticationFilter();
            jwtAuthenticationFilter.setJwtUtil(jwtUtil);
            jwtAuthenticationFilter.setMySecurityProperty(mySecurityProperty);
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
        if(mySecurityProperty.getCanCrossOrigin()) {
            log.info("???????????????????????? ?????????????????????????????????");
            CrossOriginFilter crossOriginFilter=new CrossOriginFilter();
            http.addFilterBefore(crossOriginFilter, SecurityContextPersistenceFilter.class);
        }
        if(customService!=null){
            customService.HttpSecurityCustom(http);
        }
        // ??????CSRF??????
        http.csrf().disable();
        log.info("security????????????--------------------------------------------------------------------------------------------");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ?????????????????????????????????????????????????????????
        web.ignoring().antMatchers("/css/**", "/js/**");
    }



}
