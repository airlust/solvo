package com.curvedpin;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/greetingtoo")
public class GreetingControllerToo {

    @GET
    @Produces("application/json")
    public Greeting getMe() {
        return new Greeting(999,"Thinky");
    }
}
