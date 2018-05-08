package hello.filters.pre;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import org.springframework.core.env.Environment;


public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    @Autowired
  private Environment env;

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
	
	String searchString = env.getProperty("gateway.searchstring");
	
    String tokenFrHeader = request.getHeader(searchString);
    
    String tokenFrParam = request.getParameter(searchString);

    String tokenFrCookies = getCookieValue(request.getCookies(), searchString);
    
    /*
     * sample to call other service
     * */
    
    HttpHeaders headers = new HttpHeaders();
    
    headers.add(searchString, tokenFrParam);
    
    RestTemplate restTemplate = new RestTemplate();
    
    String url = env.getProperty("gateway.authserver.url");
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
	
	int timeout = Integer.parseInt(env.getProperty("gateway.authserver.timeout"));
	
	((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(timeout);
    
    ResponseEntity<Object> respEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    
    if(respEntity.getStatusCode() == HttpStatus.OK){
    	log.info("get response");
    }
	else
		setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
    
    log.info(String.format("%s request to %s with token %s", request.getMethod(), request.getRequestURL().toString(), tokenFrHeader));
	
    /*
     * if not appropriate send response
     * */
	if(tokenFrHeader.equals("nono"))
		setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());

	
	HttpServletResponse response = ctx.getResponse();
	
	Cookie cookie = new Cookie(searchString, tokenFrParam);
	
	response.addCookie(cookie);
	
    return null;
  }
  
	private void setFailedRequest(String body, int code) {
		log.debug("Reporting error ({}): {}", code, body);
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setResponseStatusCode(code);
		if (ctx.getResponseBody() == null) {
			ctx.setResponseBody(body);
			ctx.setSendZuulResponse(false);
		}
	}
	
	private String getCookieValue(Cookie[] kukies, String Name){
		for (Cookie cookie : kukies){
			if(Name.equals(cookie.getName())){
				return cookie.getValue();
			}
		}
		
		return null;
	}

}