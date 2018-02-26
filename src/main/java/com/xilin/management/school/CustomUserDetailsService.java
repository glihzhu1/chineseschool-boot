package com.xilin.management.school;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.xilin.management.school.web.util.TransientUser;
import com.xilin.management.school.web.util.Utils;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    //@Autowired
    //private UserRepository userRepository;
	
	@Value("${user.api.server.rest.uri}")
	public String uri;
	
	@Value("${user.api.server.rest.username}")
	public String apiusername;
	
	@Value("${user.api.server.rest.password}")
	public String apipassword;
	
	@Autowired
    public CustomUserDetailsService() {
        super();
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
    	TransientUser tuser = null;
    	//final User user = userRepository.findByUsername(username);
    	
    	final String userLoadUri = uri +
				"/allusers/loginId/" + username;
    	
    	RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipassword);
        //String msg= restTemplate.getForObject(uri, String.class);
        ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(userLoadUri, 
        		  HttpMethod.GET, null, String.class);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        if(response.getStatusCode() != HttpStatus.OK) {
        	System.out.println("no user found");
        }
        else {
        	try {
        		//System.out.println("msg="+response.getBody());
        		tuser = TransientUser.fromJsonToTransientUser(response.getBody());
        		System.out.println("msg="+tuser.toString());
        	}
        	catch (Exception e) {
        		//logger.error(e.getMessage());
        		e.printStackTrace();
        	}
        }
    	
        if (tuser == null) {
            throw new UsernameNotFoundException(username);
        }
        
        String pwd = tuser.getPasswordHash().trim();
        
        UserDetails userDetail = 
        		new User(tuser.getLoginId(), pwd,
        		tuser.getUserActive(),
                true,
                true,
                true,
                getAuthorities(tuser.getAppRole().trim()));
        return userDetail;
        //return new MyUserPrincipal(user);
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
    	/*List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;*/
        
        return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
    }
}