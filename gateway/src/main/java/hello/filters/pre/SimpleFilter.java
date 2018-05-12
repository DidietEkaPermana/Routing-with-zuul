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
	  
	  //log.info("run filter");
    //RequestContext ctx = RequestContext.getCurrentContext();
    
	//HttpServletRequest request = ctx.getRequest();
	
	//String searchString = env.getProperty("gateway.searchstring");
	
	//String tokenFrHeader = request.getHeader("Authorization");
    
    //if(tokenFrHeader == null){
    //	log.info("empty header");
    //}
    
    //String tokenFrParam = request.getParameter("Authorization");

    //if(tokenFrParam == null || tokenFrParam.isEmpty()){
    //	log.info("error param");
    //}
	
    //String tokenFrCookies = getCookieValue(request.getCookies(), "Authorization");
    
    //if(tokenFrCookies == null){
    //	log.info("empty cookies");
    //}
    
    /*
     * sample to call other service
     * */
    
    //HttpHeaders headers = new HttpHeaders();
    
    //headers.add(searchString, tokenFrParam);
    
    //RestTemplate restTemplate = new RestTemplate();
    
    //String url = env.getProperty("gateway.authserver.url");
    
    //HttpEntity<String> entity = new HttpEntity<>(headers);
	
	//int timeout = Integer.parseInt(env.getProperty("gateway.authserver.timeout"));
	
	//((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(timeout);
    
    //ResponseEntity<Object> respEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    
    //if(respEntity.getStatusCode() == HttpStatus.OK){
    //	log.info("get response");
    //}
	//else
	//	setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
    
    //log.info(String.format("%s request to %s with token %s", request.getMethod(), request.getRequestURL().toString(), tokenFrHeader));
	
    /*
     * if not appropriate send response
     * */
	//if(tokenFrHeader.equals("nono"))
	//	setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());

	
	//HttpServletResponse response = ctx.getResponse();
	
	//Cookie cookie = new Cookie(searchString, tokenFrParam);
	
	//response.addCookie(cookie);
	
	log.info("run filter");
    RequestContext ctx = RequestContext.getCurrentContext();
    
	HttpServletRequest request = ctx.getRequest();
	
	String searchString = env.getProperty("gateway.searchstring");
	
	String token = request.getParameter(searchString);
    
	if(token == null || token.isEmpty()){
		
		log.info("token not found on parameter");
		
    	token = getCookieValue(request.getCookies(), searchString);

		log.info("result check on cookies");
		if(token == null || token.isEmpty()){
			log.info("token not found on cookies");
			
			setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
			
			return null;
		}
    }
	else{
		HttpServletResponse response = ctx.getResponse();
		Cookie cookie = new Cookie(searchString, token);
		response.addCookie(cookie);
	}
	
	log.info(String.format("%s request to %s with token %s", request.getMethod(), request.getRequestURL().toString(), token));
	
	if(!getAuth(token)){
		setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
	}
	
    return null;
  }
  
  private boolean getAuth(String token){
	  HttpHeaders headers = new HttpHeaders();
	    
	    headers.add("Authorization", token);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    String url = env.getProperty("gateway.authserver.url");
	    
	    HttpEntity<String> entity = new HttpEntity<>(headers);
		
		int iTimeout = Integer.parseInt(env.getProperty("gateway.authserver.timeout"));
	    
	    ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(iTimeout);
	    
	    ResponseEntity<Object> respEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
	    
	    if(respEntity.getStatusCode() == HttpStatus.OK){
	    	return true;
	    }
	    
	    return false;
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
		if(kukies != null){		
			for (Cookie cookie : kukies){
				if(Name.equals(cookie.getName())){
					return cookie.getValue();
				}
			}
		}
		
		return null;
	}

}