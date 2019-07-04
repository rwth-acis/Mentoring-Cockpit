package i5.las2peer.services.moodleService;

import java.io.IOException;
import java.net.ProtocolException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import i5.las2peer.api.Context;
import i5.las2peer.api.ManualDeployment;
import i5.las2peer.api.logging.MonitoringEvent;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.moodleService.moodleConnection.MoodleWebServiceConnection;
import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;



// TODO Describe your own service
/**
 * las2peer-Template-Service
 * 
 * This is a template for a very basic las2peer service that uses the las2peer WebConnector for RESTful access to it.
 * 
 * Note: If you plan on using Swagger you should adapt the information below in the SwaggerDefinition annotation to suit
 * your project. If you do not intend to provide a Swagger documentation of your service API, the entire Api and
 * SwaggerDefinition annotation should be removed.
 * 
 */
// TODO Adjust the following configuration
@Api
@SwaggerDefinition(
		info = @Info(
				title = "las2peer Template Service",
				version = "1.0",
				description = "A las2peer Template Service for demonstration purposes.",
				termsOfService = "http://your-terms-of-service-url.com",
				contact = @Contact(
						name = "John Doe",
						url = "provider.com",
						email = "john.doe@provider.com"),
				license = @License(
						name = "your software license name",
						url = "http://your-software-license-url.com")))

@ManualDeployment
@ServicePath("moodle")
// TODO Your own service class
public class MoodleServiceClass extends RESTService {
	
	private boolean isMoodleConnected = true;
	
	@Path("/start-moodle/{domainName}/{token}/{courseId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String initMoodleConnection(@PathParam("domainName") String domainName, @PathParam("token") String token, @PathParam("courseId") int courseId) throws ProtocolException, IOException{
		//String token = "a31d48432709e02ace346c6de2e0f803";
		//String domainName = "http://localhost:8888/moodle37";
		
		//String token = 0403e93e9bcdc0554fafe004f36b3e6f
		//String domainName = http://tech4comp.dbis.rwth-aachen.de:30150/my
		
		MoodleWebServiceConnection moodle = new MoodleWebServiceConnection();
		moodle.init(token, domainName, true);
		
		Context.get().getExecutor().execute(() -> {
			
			String oldgrades = "";
			String olduserinfo = "";
			isMoodleConnected = true;
			
			while(isMoodleConnected) {
				String newgrades = "";
				String newuserinfo = "";
				try {
					newgrades = moodle.gradereport_user_get_grade_items(courseId);
					newuserinfo = moodle.core_enrol_get_enrolled_users(courseId);
				} catch (IOException e) {
					e.printStackTrace();
					//return Response.status(Status.NOT_FOUND).build();
				}
				
				if(!newgrades.equals(oldgrades)){
					oldgrades = newgrades;
					Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_1, oldgrades);
				}
				
				if(!newuserinfo.equals(olduserinfo)){
					olduserinfo = newuserinfo;
					Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_2, olduserinfo);
				}
				
				}

				
				try {
					Thread.sleep(30*1000);
				} catch (Exception e) {
					
				}
			});
		return "Moodle Connection initiated";
	}
	
	@Path("/stop-moodle")
	@Produces(MediaType.TEXT_PLAIN)
	public String stopMoodleConnection() {
		isMoodleConnected = false;
		return "Moodle Connection stopped";
	}


	/**
	 * Shows how to use the las2peer Context to get the user name.
	 * 
	 * @return Returns the username for the currently active agent.
	 */
	@GET
	@Path("/username")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserName() {
		
		//UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
		//String name = userAgent.getLoginName();

		return "Hello World";
	}
	
	
	@POST
	@Path("/{input}") //This value is used in the methods' declaration in the annotation @PathParam("input")
	@Produces(MediaType.TEXT_PLAIN)
	public String postMessage(@PathParam("input") String myInput) {
		String returnString = "";
		returnString += "Input " + myInput;
		return returnString;
	}


	
	
	
}
