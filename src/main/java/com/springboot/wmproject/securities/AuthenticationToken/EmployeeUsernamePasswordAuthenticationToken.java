package com.springboot.wmproject.securities.AuthenticationToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class EmployeeUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public EmployeeUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public EmployeeUsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
