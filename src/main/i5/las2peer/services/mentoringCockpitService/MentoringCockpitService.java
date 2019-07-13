package i5.las2peer.services.mentoringCockpitService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import i5.las2peer.api.Context;
import i5.las2peer.api.ManualDeployment;
import i5.las2peer.api.logging.MonitoringEvent;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.mentoringCockpitService.moodleData.MoodleWebServiceConnection;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@ServicePath("mc")
// TODO Your own service class
public class MentoringCockpitService extends RESTService {
	
	//private static volatile boolean isMoodleConnected = true;
	
	private String moodleDomain;
	private String moodleToken;
	private String lrsDomain;
	private String lrsAuth;
	
	private MoodleWebServiceConnection moodle;

	private static ArrayList<String> oldstatements = new ArrayList<String>();

	private static final String NEW_DATA_MESSAGE = "New moodle data was sent to MobSOS.";
	private static final String NO_NEW_DATA_MESSAGE = "No new moodle data was found.";
	
	
	public MentoringCockpitService() {
		setFieldValues();
		moodle = new MoodleWebServiceConnection(moodleToken, moodleDomain);
	}
	
	
	@POST
	@Path("/moodle-data/{courseId}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "Moodle connection is initiaded") })
	public Response initMoodleConnection(@PathParam("courseId") int courseId) throws ProtocolException, IOException{
		String returnMessage = NO_NEW_DATA_MESSAGE;
		//isMoodleConnected = true;
		//System.out.println("Vor execute")
		//Context.get().getExecutor().execute(() -> {
			//System.out.println("Im execute");
			
			//while(isMoodleConnected) {
		String gradereport = "";
		String userinfo = "";
		try {
			gradereport = moodle.gradereport_user_get_grade_items(courseId);
			userinfo = moodle.core_enrol_get_enrolled_users(courseId);
		} catch (IOException e) {
			e.printStackTrace();
			//return Response.status(Status.NOT_FOUND).build();
		}
		
		ArrayList<String> newstatements = new ArrayList<String>();
		try {
			newstatements = moodle.statementGenerator(gradereport, userinfo);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i = 0; i < newstatements.size(); i++) {
			String statement = newstatements.get(i);
			if(!oldstatements.contains(statement))
				Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_2, statement);
				oldstatements.add(statement);
				if(!returnMessage.equals(NEW_DATA_MESSAGE))
					returnMessage = NEW_DATA_MESSAGE;
			
		}
		//oldstatements=newstatements;
				/*
				try {
					Thread.sleep(30*1000);
				} catch (Exception e) {
					
				}
				*/
			//}
		//});
		return Response.ok().entity(returnMessage).build();
	}
	
	
	@POST
	@Path("/stop-moodle")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "Moodle connection is stopped") })
	public Response stopMoodleConnection() {
		Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_2, "Hallo das ist ein Test");
		//isMoodleConnected = false;
		return Response.ok().entity("Test").build();
	}
	
	
	public void sendXAPIstatement(ArrayList<String> statements) {
		for(String statement : statements) {
			try {
				URL url = new URL(lrsDomain);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("X-Experience-API-Version","1.0.3");
				conn.setRequestProperty("Authorization", lrsAuth);
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setUseCaches(false);
				
				OutputStream os = conn.getOutputStream();
				os.write(statement.getBytes("UTF-8"));
				os.flush();
				
				Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				
				//maybe for frontend needed maybe not
				for (int c; (c = reader.read()) >= 0;)
					System.out.print((char)c);
					
				
				conn.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
