//package samples;
//
//import org.mybatis.spring.mapper.MapperScannerConfigurer;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
//import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
//import org.springframework.boot.autoconfigure.redis.RedisAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class,
//		RedisAutoConfiguration.class, DataSourceAutoConfiguration.class,
//		DataSourceTransactionManagerAutoConfiguration.class })
//@ComponentScan({ "com.dachen.health.dao.mybatis", "samples" })
//public class MyApplication {
//
//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(MyApplication.class, args);
//	}
//
//	@Bean
//	public MapperScannerConfigurer mapperScannerConfigurer0() {
//		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
//		configurer.setBasePackage("com.dachen.health.dao.mybatis");
//		configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//		return configurer;
//	}
//
//}
