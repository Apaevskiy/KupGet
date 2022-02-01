package kup.get.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest arg0,
                                        HttpServletResponse arg1,
                                        Authentication authentication) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.forEach(authority -> {
            if (authority.getAuthority().equals("ROLE_USER") || authority.getAuthority().equals("ROLE_ADMIN")) {
                try {
                    redirectStrategy.sendRedirect(arg0, arg1, "/user/waybills");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (authority.getAuthority().equals("ROLE_COVID")) {
                try {
                    redirectStrategy.sendRedirect(arg0, arg1, "/sick");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (authority.getAuthority().equals("ROLE_SUPERADMIN")) {
                try {
                    redirectStrategy.sendRedirect(arg0, arg1, "/superAdmin/upload");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalStateException();
            }
        });
    }
}