package com.hrms.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component @RequiredArgsConstructor @Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwt;
    private final UserDetailsService uds;
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            String header = req.getHeader("Authorization");
            if(StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String email = jwt.extractUsername(token);
                if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
                    UserDetails ud = uds.loadUserByUsername(email);
                    if(jwt.isValid(token,ud)) {
                        var auth = new UsernamePasswordAuthenticationToken(ud,null,ud.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch(Exception e){ log.error("JWT error: {}", e.getMessage()); }
        chain.doFilter(req,res);
    }
}
