package solo.blog.config;

import jakarta.servlet.Filter;
<<<<<<< HEAD
=======
import org.aopalliance.intercept.Interceptor;
>>>>>>> b649d7ce221f93c461f63492856a25dae930a0c8
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
<<<<<<< HEAD
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import solo.blog.controller.filter.LoginCheckFilter;
import solo.blog.controller.filter.LoginFilter;
=======
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import solo.blog.controller.login.LoginArgumentResolver;
import solo.blog.controller.login.LoginCheckInterceptor;
import solo.blog.controller.login.LoginInterceptor;
import solo.blog.filter.LoginCheckFilter;
import solo.blog.filter.LoginFilter;
>>>>>>> b649d7ce221f93c461f63492856a25dae930a0c8

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
<<<<<<< HEAD
    @Bean
    public FilterRegistrationBean loginFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

=======
   // @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
>>>>>>> b649d7ce221f93c461f63492856a25dae930a0c8
        filterRegistrationBean.setFilter(new LoginFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean loginCheckFilter() {
<<<<<<< HEAD
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
=======
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
>>>>>>> b649d7ce221f93c461f63492856a25dae930a0c8
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
<<<<<<< HEAD
}
=======

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginArgumentResolver());
    }
}
>>>>>>> b649d7ce221f93c461f63492856a25dae930a0c8
