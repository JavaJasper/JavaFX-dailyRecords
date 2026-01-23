package com.example.javafxdailyrecords.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.javafxdailyrecords.mapper.DailyReportMapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBatisPlusConfig {
    private static final Logger logger = Logger.getLogger(MyBatisPlusConfig.class.getName());
    private static final SqlSessionFactory sqlSessionFactory;
    private static volatile SqlSession sqlSession;

    private MyBatisPlusConfig() {
    }

    private static Set<Class<?>> scanPackage(String packageName) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while(resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File file = new File(resource.toURI());
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for(File f : files) {
                            String fileName = f.getName();
                            if (fileName.endsWith(".class") && !fileName.contains("$")) {
                                String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                                Class<?> clazz = Class.forName(className);
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    public static SqlSession getSqlSession() {
        if (sqlSession == null) {
            synchronized(MyBatisPlusConfig.class) {
                if (sqlSession == null) {
                    sqlSession = sqlSessionFactory.openSession(true);
                }
            }
        }
        return sqlSession;
    }

    public static <T> T getMapper(Class<T> mapperClass) {
        try {
            MybatisConfiguration config = (MybatisConfiguration)getSqlSession().getConfiguration();
            MapperRegistry registry = config.getMapperRegistry();
            if (!registry.hasMapper(mapperClass)) {
                throw new RuntimeException("❌ Mapper未注册：" + mapperClass.getName());
            } else {
                T mapper = (T)getSqlSession().getMapper(mapperClass);
                if (mapper == null) {
                    throw new RuntimeException("❌ 获取Mapper失败：" + mapperClass.getName());
                } else {
                    logger.info("✅ 成功获取Mapper：" + mapperClass.getName());
                    return mapper;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("获取Mapper异常：" + mapperClass.getName(), e);
        }
    }

    public static void close() {
        if (sqlSession != null) {
            try {
                sqlSession.close();
                logger.info("✅ MyBatis-Plus资源已关闭");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "关闭SqlSession失败", e);
            }
        }

    }

    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            if (inputStream == null) {
                throw new RuntimeException("配置文件不存在：" + resource);
            } else {
                MybatisConfiguration mpConfig = new MybatisConfiguration();
                mpConfig.setMapUnderscoreToCamelCase(true);
                mpConfig.setLogImpl(StdOutImpl.class);
                MapperRegistry registry = mpConfig.getMapperRegistry();

                for(Class<?> clazz : scanPackage("com.example.javafxdailyrecords.mapper")) {
                    if (clazz.isInterface()) {
                        registry.addMapper(clazz);
                        logger.info("✅ 手动注册Mapper：" + clazz.getName());
                    }
                }

                MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
                interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
                mpConfig.addInterceptor(interceptor);
                MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
                SqlSessionFactory tempFactory = builder.build(inputStream);
                Configuration originalConfig = tempFactory.getConfiguration();
                mpConfig.setEnvironment(originalConfig.getEnvironment());
                mpConfig.setVariables(originalConfig.getVariables());
                sqlSessionFactory = builder.build(mpConfig);
                inputStream.close();
                sqlSession = sqlSessionFactory.openSession(true);
                boolean isRegistered = registry.hasMapper(DailyReportMapper.class);
                logger.info("✅ MyBatis-Plus初始化完成");
                logger.info("✅ Mapper注册状态：" + isRegistered);
                logger.info("✅ SqlSession创建成功");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ MyBatis-Plus初始化失败", e);
            throw new RuntimeException("MyBatis-Plus初始化异常", e);
        }
    }
}
