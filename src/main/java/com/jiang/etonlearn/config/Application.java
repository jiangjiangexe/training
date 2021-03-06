package com.jiang.etonlearn.config;


import com.jiang.etonlearn.entity.Activity;
import com.jiang.etonlearn.repository.ActivityRepository;
import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@PropertySource(value = {"classpath:datasource.properties",
        "classpath:messages.properties",
        "classpath:messages_en_US.properties",
        "classpath:messages_zh_TW.properties"})
@ImportResource("classpath:spring-cache-config.xml")
@MapperScan(basePackages = {"com.jiang.etonlearn.repository"})
public class Application {

    @Autowired
    Environment env;

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("datasource.driver"));
        dataSource.setUrl(env.getProperty("datasource.url"));
        dataSource.setUsername(env.getProperty("datasource.username"));
        dataSource.setPassword(env.getProperty("datasource.password"));
        return dataSource;
    }

    @Bean
    public MessageSource messageSource() {
        return new ReloadableResourceBundleMessageSource();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfigLocation(new ClassPathResource("mybatis.xml"));
        sessionFactory.setTypeAliasesPackage("com.jiang.etonlearn.entity");
        return sessionFactory;
    }


    @Transactional
    public static void main(String[] args) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        final ActivityRepository activityRepository = context.getBean(ActivityRepository.class);
        final List<Activity> activityList = activityRepository.queryByName("er");
        for (Activity activity : activityList) {
            activity.setName("DIOGA");
            activityRepository.update(activity);
        }
        activityRepository.deleteById(1);
    }

    // TODO 多國語言
    // TODO 登入資訊重複jwtToken
}
