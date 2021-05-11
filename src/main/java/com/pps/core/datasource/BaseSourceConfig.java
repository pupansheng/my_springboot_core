package com.pps.core.datasource;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 * @discription;
 * @time 2020/9/6 12:38
 */
@Slf4j
@Setter
@Getter
public class BaseSourceConfig {


    private  String type;

    private   MybatisConfig mybatisConfig=new MybatisConfig();

    private   GlobalConfig globalConfig=new GlobalConfig();

    private    MybatisConfiguration configuration=new MybatisConfiguration();

    private   DataBaseProperty database=new DataBaseProperty();

    private  TransationProperty transationProperty=new TransationProperty();




    @Getter
    @Setter
    public  static class  TransationProperty{

        private int transactionSynchronization = 0;
        private int defaultTimeout = -1;
        private boolean nestedTransactionAllowed = false;
        private boolean validateExistingTransaction = false;
        private boolean globalRollbackOnParticipationFailure = true;
        private boolean failEarlyOnGlobalRollbackOnly = false;
        private boolean rollbackOnCommitFailure = false;
        private boolean enforceReadOnly;

    }

    @Getter
    @Setter
    public static  class DataBaseProperty{


        private String filters;
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private int initialSize;
        private int minIdle;
        private int maxActive;
        private long maxWait;
        private long timeBetweenEvictionRunsMillis;
        private long minEvictableIdleTimeMillis;
        private String validationQuery;
        private boolean testWhileIdle;
        private boolean testOnBorrow;
        private boolean testOnReturn;
        private boolean poolPreparedStatements;
        private int maxPoolPreparedStatementPerConnectionSize;
    }



    @Getter
    @Setter
    public static  class  MybatisConfig{

        private String [] mapperXmlLocation;

        private  String [] mapperLocation;

        private  String [] typeAliasesPackageList;


    }


}
