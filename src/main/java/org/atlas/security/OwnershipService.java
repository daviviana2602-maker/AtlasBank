package org.atlas.security;

import org.atlas.common.exception.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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
            throw new ForbiddenException("You are not allowed to access this resource");
        }

    }

}