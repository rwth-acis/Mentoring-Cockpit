package i5.las2peer.services.moodleService.moodleConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class MoodleWebServiceConnection {
	private static String token = null;
	private static String domainName = null;
	private static String restFormat = "";
	
	
	public MoodleWebServiceConnection() {
		
	}
	
	
	/**
	 * Initiates the moodle web service connections
	 * @param token The token of your moodle webservice.
	 * @param domainName The url of your moodle instance.
	 */
	public void init(String token, String domainName) {
		MoodleWebServiceConnection.token = token;
		MoodleWebServiceConnection.domainName = "http://" + domainName;
	}
	
	/**
	 * @param isJson If you want the output in json format than set true.
	 */
	public void init(String token, String domainName, boolean isJson) {
		MoodleWebServiceConnection.token = token;
		MoodleWebServiceConnection.domainName = "http://" + domainName;
		if (isJson) restFormat = "&moodlewsrestformat=json";
	}
	
	
	/**
	 * This function requests a Rest function to the initiated moodle web server.
	 * @param functionName This the function name for the moodle rest request.
	 * @param assignmentNumber These are the parameters in one String for the moodle rest request.
	 * @return Returns the output of the moodle rest request.
	 */
	private String restRequest(String functionName, String urlParameters) throws ProtocolException, IOException{
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
	public String core_course_get_courses() throws ProtocolException, IOException {
		String result = restRequest("core_course_get_courses", null);
		return result;
	}
	
	/**
	 * @param courseId This is Id of the course you want to have enrolled users of
	 * @return Returns enrolled users for specified course 
	 */
	public String core_enrol_get_enrolled_users(int courseId) throws ProtocolException, IOException {
		String urlParameters = "courseid=" + URLEncoder.encode(Integer.toString(courseId), "UTF-8");
		String result = restRequest("core_enrol_get_enrolled_users", urlParameters);
		return result;
	}
	
	/**
	 * @param userId This is Id of the user you want to have the courses of
	 * @return Returns courses where the specified user is enrolled in
	 */
	public String core_enrol_get_users_courses(int userId) throws ProtocolException, IOException {
		String urlParameters = "userid=" + URLEncoder.encode(Integer.toString(userId), "UTF-8");
		String result = restRequest("core_enrol_get_users_courses", urlParameters);
		return result;
	}
	

	/**
	 * @param courseId This is Id of the course you want to have grades of
	 * @return Returns grades for all users, who are enrolled in the specified course 
	 */
	public String gradereport_user_get_grade_items(int courseId) throws ProtocolException, IOException {
		
		String urlParameters = "courseid=" + URLEncoder.encode(Integer.toString(courseId), "UTF-8");
		String result = restRequest("gradereport_user_get_grade_items", urlParameters);
		return result;
	}
	
	/**
	 * @param courseId This is Id of the course you want to have grades of
	 * @param userId This is Id of the user you want to have grades of 
	 * @return Returns grades for the specified course and user
	 */
	public String gradereport_user_get_grade_items(int courseId, int userId) throws ProtocolException, IOException {
		
		String urlParameters = "courseid=" + URLEncoder.encode(Integer.toString(courseId), "UTF-8") + 
				"&userid=" + URLEncoder.encode(Integer.toString(userId), "UTF-8");
		String result = restRequest("gradereport_user_get_grade_items", urlParameters);
		return result;
	}
	
}
