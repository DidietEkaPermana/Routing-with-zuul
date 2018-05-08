package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class BookApplication {

  @RequestMapping(value = "/available")
  public String available() {
    return "{\"data\": \"Spring in Action from server 01\"}";
  }
  
  @RequestMapping(value = "/check")
  public ResponseEntity<String> check(@RequestHeader("Authorization") String token) {
	  if(token.isEmpty())
		  return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
	  else
		return new ResponseEntity<String>(HttpStatus.OK);
  }

  @RequestMapping(value = "/checked-out")
  public String checkedOut() {
    return "Spring Boot in Action";
  }

  public static void main(String[] args) {
    SpringApplication.run(BookApplication.class, args);
  }
}