package moodleRestClient;

import java.io.IOException;
import java.net.ProtocolException;

public class Main {
	private static final String TOKEN = "a31d48432709e02ace346c6de2e0f803";
	private static final String DOMAIN_NAME = "http://localhost:8888/moodle37";
	
	public static void main(String[] args) throws ProtocolException, IOException {
		
		MoodleWebServiceConnector.init(TOKEN, DOMAIN_NAME);
		
		String userCourses= MoodleWebServiceConnector.core_enrol_get_users_courses(4);
		System.out.println(userCourses);
		
		
		//String enrolledUsers= MoodleWebServiceConnector.core_enrol_get_enrolled_users(4);
		//System.out.println(enrolledUsers);
		
		//String courses= MoodleWebServiceConnector.core_course_get_courses();
		//System.out.println(courses);
		
		
		//String grades= MoodleWebServiceConnector.gradereport_user_get_grade_items(4);
		//System.out.println(grades);
		
		
	}
}
