package com.kitandasmart.backend.services;


import com.kitandasmart.backend.util.UserDetailsUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private final Map<String,String> emailToUserIdMap = new HashMap<>();
    public UserService() {
        emailToUserIdMap.put("admin@kitandasmart.com","admin");
    }
    public void registerUser(String email) {
        emailToUserIdMap.put(email,"client");
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(emailToUserIdMap.containsKey(email)) {
            return new UserDetailsUtils(email);
        }else {
        throw new UsernameNotFoundException(email);
        }
        }

}
