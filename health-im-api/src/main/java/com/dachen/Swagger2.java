package com.dachen;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 2017/6/7.
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createRestApi() {

        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("access-token").description("令牌")
                .defaultValue("b19a16577ead41eab95fbb005d9b4769")
                .modelRef(new ModelRef("string"))
                .parameterType("header").required(false).build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            //.apis(RequestHandlerSelectors.basePackage("com.dachen.health"))
            .paths(Predicates.or(getPredicate()))
            .build()
            .apiInfo(apiInfo())
            .globalOperationParameters(pars);

    }

    private Predicate<String>[] getPredicate() {
        String[] paths = getPaths();
        Predicate<String>[] predicates = new Predicate[paths.length];
        for (int i = 0; i < paths.length; i++) {
            predicates[i] = PathSelectors.regex(paths[i]);
        }
        return predicates;
    }

    /**
     * 配置要显示的API路径
     * @return
     */
    private String[] getPaths() {
        return new String[]{
                "/m/circle/doctor/.*",
                "/user/dept/.*",
                "/user/role/.*",
                "/user/customer/.*",
                "/role",
                "/role/.*",
                "/permission",
                "/userFile/.*",
                "/user/admin/.*",
                "/user/v2/disable",
                "/doctor/afterCheck/.*",
                "/adapter/.*",
                "/lightApp/.*",
                "/user/nologin/isNewAccount",
                "/user/nologin/registerH5V2",
                "/user/college/.*",
                "/base/college/.*",
                "/abnormal/.*"
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("health服务API文档")
            .description("访问方式：http://{domain}/Health/*****")
            .version("1.0")
            .build();
    }
}
