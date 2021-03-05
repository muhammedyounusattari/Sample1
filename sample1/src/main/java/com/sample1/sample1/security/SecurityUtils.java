package com.sample1.sample1.security;

import com.sample1.sample1.domain.core.authorization.rolepermission.IRolepermissionRepository;
import com.sample1.sample1.domain.core.authorization.rolepermission.RolepermissionEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspermission.IUserspermissionRepository;
import com.sample1.sample1.domain.core.authorization.userspermission.UserspermissionEntity;
import com.sample1.sample1.domain.core.authorization.usersrole.IUsersroleRepository;
import com.sample1.sample1.domain.core.authorization.usersrole.UsersroleEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    @Qualifier("rolepermissionRepository")
    @NonNull
    private final IRolepermissionRepository rolepermissionRepository;

    @Qualifier("usersroleRepository")
    @NonNull
    private final IUsersroleRepository usersroleRepository;

    @Qualifier("userspermissionRepository")
    @NonNull
    private final IUserspermissionRepository userspermissionRepository;

    public List<String> getAllPermissionsFromUserAndRole(UsersEntity users) {
        List<String> permissions = new ArrayList<>();
        List<UsersroleEntity> ure = usersroleRepository.findByUsersId(users.getId());

        for (UsersroleEntity ur : ure) {
            List<RolepermissionEntity> srp = rolepermissionRepository.findByRoleId(ur.getRoleId());
            for (RolepermissionEntity item : srp) {
                permissions.add(item.getPermission().getName());
            }
        }

        List<UserspermissionEntity> spe = userspermissionRepository.findByUsersId(users.getId());

        for (UserspermissionEntity up : spe) {
            if (permissions.contains(up.getPermission().getName()) && (up.getRevoked() != null && up.getRevoked())) {
                permissions.remove(up.getPermission().getName());
            }
            if (!permissions.contains(up.getPermission().getName()) && (up.getRevoked() == null || !up.getRevoked())) {
                permissions.add(up.getPermission().getName());
            }
        }

        return permissions.stream().distinct().collect(Collectors.toList());
    }

    public String getTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(SecurityConstants.HEADER_STRING_AUTHENTICATION)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .map(
                authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                }
            );
    }
}
