package com.xilin.management.school;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.xilin.management.school.web.util.Utils;

@Order(1)
@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = CustomUserDetailsService.class)
public class MySecurityConfigurer extends WebSecurityConfigurerAdapter {
	@Autowired 
	private UserDetailsService userDetailsService;
	
	/*@Autowired
    CustomSuccessHandler customSuccessHandler;*/
 
	@Autowired
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("XILINADMIN");
		builder.inMemoryAuthentication()
			.withUser("family").password("family").roles("XILINFAMILY");
		/*builder.inMemoryAuthentication()
			.withUser("manu").password("f13bb1bed03db9d68a7d9a48aafeec78").roles("NAMADManu");*/
		
    	builder.userDetailsService(userDetailsService).passwordEncoder(passwordencoder());
    	//builder.userDetailsService(userDetailsService);
    }
    
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/", "/resources/static/**", "/webjars/**", "/pages/public/**", "/public/**", "/pages/templates/**", "/index.jsf").permitAll()
			.antMatchers("/pages/admin/**").access("hasRole('ROLE_XILINADMIN')")
			.antMatchers("/pages/family/**").access("hasRole('ROLE_XILINADMIN') or hasRole('ROLE_XILINFAMILY')")
	        //.antMatchers("/pages/candiate/**").access("hasRole('ROLE_XILINADMIN') or hasRole('ROLE_XILINFAMILY') or hasRole('ROLE_NAMADCandidate')")
	        .anyRequest().authenticated()
			.and()
				.formLogin().loginPage("/pages/public/login.jsf")
				.loginProcessingUrl("/login").permitAll()
				.defaultSuccessUrl("/index.jsf")
				.failureUrl("/error.jsf")
			.and()
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/j_spring_security_logout"))
				.logoutSuccessUrl("/index").deleteCookies("JSESSIONID")
				.invalidateHttpSession(true).permitAll();
			//.and()
			//	.exceptionHandling().accessDeniedPage("/403");*/
	}
    
    @Bean(name="passwordEncoder")
    public PasswordEncoder passwordencoder(){
    	return new BCryptPasswordEncoder();
    }
    
    //Spring Boot configured this already????
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
        	.antMatchers("/resources/**", "/static/**", "/public/**", "/css/**", "/js/**", "/images/**", "/javax.faces.resource/**");
    }
    
}