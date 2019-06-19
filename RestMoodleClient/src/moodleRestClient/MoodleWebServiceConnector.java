package moodleRestClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class MoodleWebServiceConnector {
	private static String token = null;
	private static String domainName = null;
	private static String restFormat = "";
	
	/**
	 * Initiates the moodle web service connections
	 * @param token The token of your moodle webservice.
	 * @param domainName The url of your moodle instance.
	 */
	public static void init(String token, String domainName) {
		MoodleWebServiceConnector.token = token;
		MoodleWebServiceConnector.domainName = domainName;
	}
	
	/**
	 * @param isJson If you want the output in json format than set true.
	 */
	public static void init(String token, String domainName, boolean isJson) {
		MoodleWebServiceConnector.token = token;
		MoodleWebServiceConnector.domainName = domainName;
		if (isJson) restFormat = "&moodlewsrestformat=json";
	}
	
	
	/**
	 * This function requests a Rest function to the initiated moodle web server.
	 * @param functionName This the function name for the moodle rest request.
	 * @param assignmentNumber These are the parameters in one String for the moodle rest request.
	 * @return Returns the output of the moodle rest request.
	 */
	private static String restRequest(String functionName, String urlParameters) throws ProtocolException, IOException{
		// Send request
		String serverurl = domainName + "/webservice/rest/server.php" + "?wstoken=" + token + "&wsfunction=" + functionName + restFormat;
		HttpURLConnection con = (HttpURLConnection) new URL(serverurl).openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Language", "en-US");
		con.setDoOutput(true);
		con.setUseCaches (false);
		con.setDoInput(true);
		DataOutputStream wr = new DataOutputStream (con.getOutputStream ());
		if (urlParameters != null) wr.writeBytes (urlParameters);
		wr.flush ();
		wr.close ();

		//Get Response
		InputStream is =con.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder response = new StringBuilder();
		while((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		return response.toString();
	}
	
	/**
	 * @return Returns the information to all courses in moodle
	 */
	public static String core_course_get_courses() throws ProtocolException, IOException {
		String result = restRequest("core_course_get_courses", null);
		return result;
	}
	
	/**
	 * @param courseId This is the Id of the respective course for the assignment.
	 * @param assignmentNumber This is the number of the assignment in this course.
	 * @return Returns the assignment information (grades, date, userid, timecreated, etc.) 
	 */
	public static String mod_assign_get_grades(int courseId, int assignmentNumber) throws ProtocolException, IOException {
		
		String urlParameters = "assignmentids[" + assignmentNumber + "]=" + URLEncoder.encode(Integer.toString(courseId), "UTF-8");
		String result = restRequest("mod_assign_get_grades", urlParameters);
		return result;
	}
	

}
