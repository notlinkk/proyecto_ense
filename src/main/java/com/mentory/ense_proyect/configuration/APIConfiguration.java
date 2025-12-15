package com.mentory.ense_proyect.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Configuration
public class APIConfiguration {

    // Adds default API-Version header = 1 when it is missing
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public OncePerRequestFilter apiVersionDefaultHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String version = request.getHeader("API-Version");
                if (version == null || version.isBlank()) {
                    HttpServletRequest wrapper = new HttpServletRequestWrapper(request) {
                        private final Map<String, List<String>> extra = new HashMap<>() {{
                            put("API-Version", List.of("1"));
                        }};

                        @Override
                        public String getHeader(String name) {
                            if (extra.containsKey(name)) {
                                return extra.get(name).get(0);
                            }
                            return super.getHeader(name);
                        }

                        @Override
                        public Enumeration<String> getHeaders(String name) {
                            if (extra.containsKey(name)) {
                                return Collections.enumeration(extra.get(name));
                            }
                            return super.getHeaders(name);
                        }

                        @Override
                        public Enumeration<String> getHeaderNames() {
                            Set<String> names = new HashSet<>();
                            Enumeration<String> delegate = super.getHeaderNames();
                            while (delegate.hasMoreElements()) {
                                names.add(delegate.nextElement());
                            }
                            names.addAll(extra.keySet());
                            return Collections.enumeration(names);
                        }
                    };
                    filterChain.doFilter(wrapper, response);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
}
