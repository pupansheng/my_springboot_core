# 这是一个springboot的脚手架 
##功能
 1. 减少依赖 当在新项目引入此依赖 即可 它包含springboot允许的常见依赖
 2. 权限封装 本脚手架封装了springSecurity 即开即用 支持表单登录 jwt登录
 3. mybatisPlug 与多数据源的 封装 在配置文件就可配置多数据源并于myabtisPlug整合
 ##数据库脚本
 ```

CREATE TABLE `sys_permisson`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `page` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sys_role_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKhqx55f02sjpau61g88us6fiu3`(`sys_role_id`) USING BTREE,
  CONSTRAINT `FKhqx55f02sjpau61g88us6fiu3` FOREIGN KEY (`sys_role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------

CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------

CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACT


```
 ##新项目 依赖示例如下：
 ```
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>org.example</groupId>
    <artifactId>demo</artifactId>
    <version>1.0-SNAPSHOT</version>


    <dependencies>

         <dependency>
                <groupId>com.pps</groupId>
                <artifactId>pps_springboot_core</artifactId>
                <version>1.0-SNAPSHOT</version>
         </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

     

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

 ```
 ## 配置文件示例
 ```
 
server:
  tomcat:
    uri-encoding: UTF-8
  port: 38667
  connection-timeout: 7200
  servlet:
    context-path: /pps
spring:
  dataSourseList: data-source1        #数据源列表名称 与下面数据源对应  第一个默认为主数据库 可以为多个数据源
  data-source1:
    type: com.alibaba.druid.pool.DruidDataSource  #数据源类型
    database:                                     #数据源基本配置
      username: root
      password: pps123
      driver_class_name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/pps-lyt-room?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8
    mybatisConfig:
      type-aliases-ackageList:                       #别名
      mapperXmlLocation:                             #mapper.xml  位置
        - classpath:mapper/*.xml
      mapperLocation:                                #mapper位置
        - com.pps.back.frame.pupansheng.core.authority.security.mapper
        - com.pps.back.frame.pupansheng.custom.mapper
      configuration:
        map-underscore-to-camel-case: true
        #org.apache.ibatis.logging.stdout.StdOutImpl日志只会打印到控制台
        #org.apache.ibatis.logging.slf4j.Slf4jIml会把日志打印到指定log文件
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
        global-config:
          #主键类型  0:"数据库ID自增", 1:"用户输入ID",2:"全局唯一ID (数字类型唯一ID)", 3:"全局唯一ID UUID";
          db-config:
            id-type: input

  mvc:
    format:
      date-time: yyyy-MM-dd HH:mm:ss
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss # 统一设置日期格式
    time-zone: GMT+8
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB
      location: d:/uploadtest
#security自定义的框架
mysecurity:
  permitUrl:                                 #允许不登录就能访问的url
    - /images/**
    - /pc/test1/**
  loginUrl:  /login                  #表单 登录页url
  logoutUrl: /logout                 #登录退出url
  openVerifyCode: false              #是否开启验证码登录处理 验证码参数默认：verifyCode
  validateCodeParam: verifyCode      #会从session里面拿这个参数 作为验证码
  verifyCodeUrl:  /verifyCode        #获取验证码url
  failureUrl: /login/error           #关于账户权限发生错误失败跳转的地址  暂时没有用
  smsMessage: /sms/get/yanzhengma
  openRequestLog: true               #网络请求是否打印
  canCrossOrigin: true               #是否生成跨域访问filter  建议生产环境取消
  openConfigUser: true               #是否开启本地测试用户  若开启 则configUsers不能为空 为一个数组 并且权限固定为ROLE_ADMIN
  configUser: '[{"username":"admin","password":"123"},{"username":"n1","password":"x1111111"}]'
  sms:
     openSms: false
     smsLoginUrl: /sms/login
     smsMessage:  /sms/get/yanzhengma #短信验证码获取
     mobileParameter: phone           #网络请求的电话号码参数
     codeParameter: code              #网络请求里面的验证码
     sessionParam:  code              #产生验证码时存到session的参数名
     sessionPhoneParam: phone         #存到session里面的应该为一个map  这是map的电话号码参数
     sessionCodeParam:  code          #存到session里面的应该为一个map  这是map的验证码参数
  jwt:
      enable: true                   #开启jwt登录  开启后登陆成功后返回前端token  下次带token 可以授权
      jwtLoginUrl: /jwt/login         #没有用到
      filter: /pps/jwt/**
      expire: 604800
      secret: aHR0cHM6Ly9teS5vc2NoaW5hLm5ldC91LzM2ODE4Njg=
      headerParam: authorization                 # header 带token的参数名
  resources:
    static-locations: classpath:/static/, classpath:/templates/
 
 ```
 
 ##启动文件示例
 ```
 
 @SpringBootApplication(scanBasePackages = {"com.pps.core", "com.pps.movie"})
 @EnableAsync
 @EnableScheduling
 @Import(MyBatisDataSourceProcessor.class)
 @EnableTransactionManagement
 public class SyncMovieApp {
 
 
     public static void main(String[] args) {
         SpringApplication.run(SyncMovieApp.class, args);
     }
 
 
 }

 
 
 
 ```