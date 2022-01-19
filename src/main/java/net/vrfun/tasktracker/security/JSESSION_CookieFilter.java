package net.vrfun.tasktracker.security;

import net.vrfun.tasktracker.appconfig.WebMvcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class JSESSION_CookieFilter extends GenericFilterBean {
    private final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        Collection<String> headers = resp.getHeaders(HttpHeaders.SET_COOKIE);
        for (String header : headers) {
            LOGGER.debug("Filtering" + header);
            if (header.contains("JSESSIONID") && !header.contains("SameSite=")) {
                LOGGER.debug("Replacing JSESSIONID cookie parameters!");
                List<String> valueAttributesList = Arrays.asList( "Secure", "SameSite=None");
                String valueAttributes = String.join(";", valueAttributesList);
                resp.setHeader(HttpHeaders.SET_COOKIE, header + ";" + valueAttributes);
                break;
            }
        }
        chain.doFilter(request, resp);
    }
}
