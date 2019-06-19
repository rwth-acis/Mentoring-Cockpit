package moodleRestClient;

import java.io.IOException;
import java.net.ProtocolException;

public class Main {
	private static String token = "a31d48432709e02ace346c6de2e0f803";
	private static String domainName = "http://localhost:8888/moodle37";
	public static void main(String[] args) throws ProtocolException, IOException {
		
		MoodleWebServiceConnector.init(token, domainName);
		
		//String courses= MoodleWebServiceConnector.core_course_get_courses();
		//System.out.println(courses);
		
		
		String assignmentData= MoodleWebServiceConnector.mod_assign_get_grades(1,0);
		System.out.println(assignmentData);
		
	}
}
