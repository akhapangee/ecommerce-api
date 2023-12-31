package com.ecommerce.api.security.jwt;

import com.auth0.jwt.JWT;
import com.ecommerce.api.exception.ApiRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationVerificationFilter.class);

    public JWTAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    /**
     * {@link JWTAuthenticationVerificationFilter#getAuthentication(HttpServletRequest)} reads a JWT and validates.
     * When JWT is valid, sets the user in the Security Context and allows the request to proceed
     *
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    /**
     * Verify the token
     *
     * @param req
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        log.info("[Token Verification]: Verifying JWT token...");
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            try {
                String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                        .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                        .getSubject();
                if (user != null) {
                    log.info("[Token Verification]: Token verified successfully...");
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
                return null;
            } catch (Exception e) {
                log.error("[Token Verification]: Issue found in token verification: {}", e.getMessage());
                throw new ApiRequestException(String.format("Issue found in token verification: %s", e.getMessage()));
            }
        }
        return null;
    }

}
