package com.dachen;

import com.dachen.commons.support.spring.convert.MappingFastjsonHttpMessageConverter;
import com.dachen.health.common.filter.GuestInterceptor;
import com.dachen.health.common.filter.LoggerInterceptor;
import com.dachen.health.common.filter.VersionInterceptor;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorAssistant;
import com.dachen.health.friend.entity.po.DoctorFriend;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.entity.po.PatientFriend;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO;
import com.dachen.health.system.entity.po.FeedBack;
import com.dachen.health.user.entity.po.DrugVerifyRecord;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.util.PropertiesUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@ComponentScan("com.dachen")
@EnableWebMvc
@ImportResource(locations={"classpath:spring/applicationContext.xml", "classpath:spring/nosql.xml"})
@EnableEurekaClient
@EnableScheduling
@EnableRetry
@EnableAsync
public class HealthApplication extends WebMvcConfigurerAdapter implements EnvironmentAware, ApplicationContextAware {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mongo.health.uri}")
    private String mongoUri;

    @Value("${mongo.health.dbName}")
    private String mongoDbName;

    @Value("${app.name}")
    private String appName;
    @Value("${spring.rabbitmq.exchanges.names}")
    private String exchangesNames;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    protected static final List<Class> mapClasses = new ArrayList<>();

    static {
        mapClasses.add(User.class);
        mapClasses.add(Tag.class);
        mapClasses.add(DoctorPatient.class);
        mapClasses.add(DoctorFriend.class);
        mapClasses.add(DoctorAssistant.class);
        mapClasses.add(PatientFriend.class);
        mapClasses.add(DrugVerifyRecord.class);
        mapClasses.add(FeedBack.class);
        mapClasses.add(Company.class);
        mapClasses.add(ConsultOrderPO.class);
        mapClasses.add(DoctorTimePO.class);
    }

    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        return new MongoClient(new MongoClientURI(this.getMongoUri()));
    }

    @Bean
    public Morphia morphia() {
        Morphia morphia = new Morphia();
//        morphiaMap(morphia);
        return morphia;
    }

//    private void morphiaMap(Morphia morphia) {
//        if (SdkUtils.isNotEmpty(mapClasses)) {
//            for (Class clazz : mapClasses) {
//                logger.debug("morphia. mapClasses.clazz={}", clazz);
//                morphia.map(clazz);
//            }
//        }
//    }

    @Bean(name = "dsForRW")
    public Datastore datastore(MongoClient mongoClient, Morphia morphia) {
        Datastore datastore = morphia.createDatastore(mongoClient, this.getMongoDbName());
//        datastore.ensureIndexes();
//        datastore.ensureCaps();
        return datastore;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/robert/**")
                .addResourceLocations("classpath:/robert/");
    }

    @PostConstruct
    public void reIndex() {
        String tag = "reIndex";

        Map<String, Object> handlerMap = context.getBeansWithAnnotation(Model.class);
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            Class<?> clz = entry.getValue().getClass();
            if (clz.isAnnotationPresent(Model.class)) {
                Model annotation = clz.getAnnotation(Model.class);
                Class<?> entityClass = annotation.value();
                mapClasses.add(entityClass);
            }
        }

        logger.info("{}. mapClasses={}", tag, mapClasses);

        Morphia morphia = (Morphia) context.getBean("morphia");
        for (Class clazz : mapClasses) {
            morphia.map(clazz);
        }

        Datastore datastore = (Datastore) context.getBean("dsForRW");
        datastore.ensureIndexes(true);
        datastore.ensureCaps();
    }

    @Autowired
    protected LoginInterceptorConfig loginInterceptorConfig;


    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }

    @Bean
    public AsyncTaskPool asyncTaskPool() {
        return new AsyncTaskPool();
    }

    @Bean
    public MappingFastjsonHttpMessageConverter mappingFastjsonHttpMessageConverter() {
        return new MappingFastjsonHttpMessageConverter();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, mappingFastjsonHttpMessageConverter());
        super.extendMessageConverters(converters);
    }

    @Bean
    public VersionInterceptor versionInterceptor() {
        return new VersionInterceptor();
    }

    @Bean
    public GuestInterceptor guestInterceptor() {
        return new GuestInterceptor();
    }

    @Bean
    public LoggerInterceptor loggerInterceptor() {
        return new LoggerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(versionInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(loggerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(guestInterceptor()).addPathPatterns(loginInterceptorConfig.getGuestIncludePathPatterns());
        super.addInterceptors(registry);
    }

	@Override
	public void setEnvironment(Environment environment) {
		PropertiesUtil.env = environment;
	}

    public String getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

    public String getMongoDbName() {
        return mongoDbName;
    }

    public void setMongoDbName(String mongoDbName) {
        this.mongoDbName = mongoDbName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    @Bean
    public RestTemplate httpTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(3000);
        factory.setConnectTimeout(3000);
        return new RestTemplate(factory);
    }

    @Bean(name="remoteRestTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(3000);
        factory.setConnectTimeout(3000);
        return new RestTemplate(factory);
    }
}
