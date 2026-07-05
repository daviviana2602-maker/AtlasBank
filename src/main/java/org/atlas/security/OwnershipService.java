package org.atlas.security;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class OwnershipService {

    public void checkOwnership(Long resourceOwnerId) {

        String userRole = SecurityContextHolder.
                getContext().
                getAuthentication().
                getAuthorities().iterator().next().getAuthority();

        if (userRole.equals("ROLE_ADMIN")) {
            return;
        }


        Long authenticatedUserId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!authenticatedUserId.equals(resourceOwnerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN
            );
        }

    }

}