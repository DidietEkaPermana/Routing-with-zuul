package hello.filters.pre;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

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
    
    String token = request.getHeader("Authorization");

    /*
     * cookies check
     * */
//    Cookie[] kukis = request.getCookies();
//   
//    for (Cookie cookie : kukis) {
//    	
//    	log.info(String.format("Cookie: %s", cookie.getName()));
//        if ("my-cookie-name".equals(cookie.getName())) {
//             String value = cookie.getValue();
//            //do something with the cookie's value.
//        }
//   }
    
    
    /*
     * sample to call other service
     * */
    
//    RestTemplate restTemplate = new RestTemplate();
//    restTemplate.getForObject(url, responseType)
    
    log.info(String.format("%s request to %s with token %s", request.getMethod(), request.getRequestURL().toString(), token));
	
    /*
     * if not appropriate send response
     * */
	if(token.equals("nono"))
		setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());

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

}