package com.sample1.sample1.security;

import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Qualifier("usersRepository")
    @NonNull
    private final IUsersRepository usersRepository;

    @NonNull
    private final SecurityUtils securityUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity applicationUser = usersRepository.findByUsername(username);

        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }

        List<String> permissions = securityUtils.getAllPermissionsFromUserAndRole(applicationUser);
        String[] groupsArray = new String[permissions.size()];
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(groupsArray));

        return new User(applicationUser.getUsername(), applicationUser.getPassword(), authorities); // User class implements UserDetails Interface
    }
}
