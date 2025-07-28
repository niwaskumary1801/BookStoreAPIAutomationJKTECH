package tests;

import static constants.ApiEndPointsKey.*;
import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

// TestCase 1: Verify the new User Creation with valid data 

public class VerifyPositiveScenarioForUserAccountTest extends BasicAPITest {

	public static String GeneratedRandomNewUserName = "user" + (int) (Math.random() * 1000) + ("@BookStore.com");
	public static String GeneratedRandomid = ("") + Math.abs(UUID.randomUUID().toString().hashCode());
	public static String token;
	public static String TokenType;
	public static Response responseData;
	public static String GeneratedUserid;

	@Test(priority = 1)
	public void NewUserCreation() {

		test = reportobj.createTest("Create New User Testing Scenario");

		String requestBody = String.format("""
				    {
				      "id":"%s",
				      "email": "%s",
				      "password": "%s"
				    }
				""", GeneratedRandomid, GeneratedRandomNewUserName, ConfigFileReader.get("PASSWORD"));

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER)
				.then().statusCode(200).extract().response();

		System.out.println("API Response Data for Newly Created user: " + responseData.getBody().asString()); // helpful debug

		System.out.println("Created User successfully with Email Id: " + GeneratedRandomNewUserName);
		System.out.println("Created User successfully with Id: " + GeneratedRandomid);
		
		// Log added in the report file.
		test.log(Status.INFO, "API Response Data for Newly Created User is :" + responseData.getBody().asString());
		test.log(Status.PASS, "User created successfully: " + GeneratedRandomNewUserName);
	}

// TestCase 2: Verify the Generate Access Token for Newly Created User 

	@Test(priority = 2, dependsOnMethods = "NewUserCreation")
	public void GenerateNewTokentoAccessUser() {

		test = reportobj.createTest("Generate New Token for Newly Created User");
		String requestBody = String.format("""
				    {
				      "id":"%s",
				      "email": "%s",
				      "password": "%s"
				    }
				""", GeneratedRandomid,GeneratedRandomNewUserName, ConfigFileReader.get("PASSWORD"));

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_GENERATE_TOKEN)
				.then().statusCode(200).extract().response();
		
		System.out.println("API Response data for Generated Access Token is :- " + responseData.getBody().asString());

		test.log(Status.INFO, "API Response data for Generated Access Token is :- " + responseData.getBody().asString());

		token = responseData.jsonPath().getString("access_token");
		
		TokenType = responseData.jsonPath().getString("token_type");

		System.out.println("API Response data for Generated Access Token Type: " + TokenType);
	
		Assert.assertNotNull(token);
		
		test.log(Status.PASS, "New User Access Token Generated:- " + token);
	}

	// TestCase 3: Verify Uvicorn Server Health Status .

	@Test(priority = 3)
	public void GetServerHealthStatus() 
	{

		test = reportobj.createTest("Check Uvicorn Server Health Status");
		responseData = given().contentType(ContentType.JSON).when().get(REQUEST_URL_GET_HEALTH).then().statusCode(200)
				.extract().response();

		System.out.println("API Response Data to Get Uvicorn Server Health: " + responseData.getBody().asString());

		test.log(Status.INFO, "API Response Data to Get Uvicorn Server Health " + responseData.getBody().asString());

	}

}
