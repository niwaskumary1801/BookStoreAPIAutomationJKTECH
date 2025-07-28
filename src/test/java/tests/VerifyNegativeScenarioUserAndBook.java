package tests;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static constants.ApiEndPointsKey.*;

public class VerifyNegativeScenarioUserAndBook extends BasicAPITest {
	public Response responseData;
	public static String GeneratedRandomNewUserName = "user" + (int) (Math.random() * 1000) + ("@BookStore.com");
	public static String GeneratedRandomBookID = ("") + Math.abs(UUID.randomUUID().toString().hashCode());
	public static String message;

// Neg Test1: Verify user creation when Payload data missing.
	@Test
	public void test_CreateUserWhenPayloadDataMissing() {
		test = reportobj.createTest("Create User when Payload data missing");

		String requestBody = """
				{
				 "id": "",
				 "email": "EmailId@test.com",
				 "password": ""
				 }
				  """;
		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER)
				.then().statusCode(500).extract().response();

		String ResMessage = responseData.getBody().asString();
		Assert.assertTrue(ResMessage.contains("Internal Server Error"));

		System.out.println(
				"Response Data for create user when Payload data missing :-" + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for create user when Payload data missing " + responseData.getBody().asString());
	}

// Neg Test2: Verify User creation when User enters invalid/empty password
	@Test
	public void test_CreateUserWithInvalidPassword() {
		test = reportobj.createTest("Create User with Invalid Password");
		String requestBody = """
				{
				    "id":"00",
				    "email": "TestEmail4321@bookStore.com",
				    "password": ""
				}""";

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER)
				.then().statusCode(500).extract().response();

		String ResMessage = responseData.getBody().asString();
		Assert.assertTrue(ResMessage.contains("Internal Server Error"));

		System.out.println("Response Data for create user with Invalid/Empty password: " + responseData.getBody().asString());
		test.log(Status.INFO,
				"Response Data for new user with Invalid/Empty password is " + responseData.getBody().asString());
	}

	/**
	 * Neg Test3: Verify Generate token with invalid credentials. Generate token
	 */
	@Test
	public void testGenerateTokenInvalidCredentials() {
		test = reportobj.createTest("Generate Token with Invalid User Credentials");

		String requestBody = """
				{
				     "id": 0,
				     "email": "testUser@gmail.com",
				     "password": "password"
				    }
				""";

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_GENERATE_TOKEN)
				.then().statusCode(400).extract().response();

		String message = responseData.jsonPath().getString("detail");
		Assert.assertEquals(message, "Incorrect email or password");

		test.log(Status.PASS, "Invalid login attempt was correctly rejected and Token not generated.");

		System.out.println(
				"Response Data for generate token with invalid credentials: " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for generate token with invalid credentials " + responseData.getBody().asString());
	}

	/**
	 * Neg Test4: Verify Create book with no Authorisation and check API responsed
	 * status code  403
	 */
	@Test
	public void createBookInCollection() {

		test = reportobj.createTest("Create Book without authorisation");

		String requestBody = String.format("""
				{
				    "id": "%s",
				    "name": "%s",
				    "author": "%s",
				    "published_year":" %s",
				    "book_summary": "%s"
				}
				""", GeneratedRandomBookID, ConfigFileReader.get("BOOK_NAME"), ConfigFileReader.get("BOOK_AUTHOR_NAME"),
				ConfigFileReader.get("BOOK_PUBLISHED_YEAR"), ConfigFileReader.get("BOOK_SUMMARY"));

		responseData = given().contentType("application/json").body(requestBody).when().post(REQUEST_URL_ADD_BOOKS)
				.then().statusCode(403).extract().response();

		String message = responseData.jsonPath().getString("detail");
		Assert.assertEquals(message, "Not authenticated");

		test.log(Status.PASS, "Attempt to create books without authorisation was correctly rejected.");
		System.out.println("Response Data for Create book without authorisation: " + responseData.getBody().asString());
		test.log(Status.INFO, "Response Data for Create book without authorisation " + responseData.getBody().asString());
	}

// Neg Test5: Verify Get Book with wrong Book id to search ,API should respond status code 404 'Book not found'.

	@Test
	public void testBookIsAdded() 
	{
		test = reportobj.createTest("Get user but search for wrong Book id");
		
		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
						.when().get(REQUEST_URL_GET_BOOKS+GeneratedRandomBookID).then().statusCode(404)
						.extract().response();
		
		System.out.println("TOKEN Used :" + VerifyPositiveScenarioForUserAccountTest.token);
		String message = responseData.jsonPath().getString("detail");
		Assert.assertEquals(message, "Book not found");
		System.out.println("Response Data for get user with wrong BooKid: " + responseData.getBody().asString());

		test.log(Status.INFO, "Response Data for get user with wrong BooKid " + responseData.getBody().asString());
	}

	/**
	 * Neg Test6: Verify attempt Update book when provide Wrong BooKid..
	 */
	@Test
	public void updateBookInCollection() {

		test = reportobj.createTest("Update Book but provide Wrong BooKid");

		String requestBody = String.format("""
				  {
				    "id": "%s",
				    "name": "%s",
				    "author": "%s"
				   }
				""", VerifyPositiveScenarioForBookTest.BookId, ConfigFileReader.get("UPDATE_BOOK_NAME"),
				ConfigFileReader.get("UPDATE_AUTHOR_NAME"));

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(requestBody).when().put(REQUEST_URL_UPDATE_BOOKS + GeneratedRandomBookID)
				.then().statusCode(404).extract().response();

		String ResMessage = responseData.jsonPath().getString("detail");

		Assert.assertTrue(ResMessage.contains("Book not found"));

		test.log(Status.PASS, "Attempt to update book with Wrong BooKid was correctly rejected.");

		System.out.println("Response Data for update books with Wrong BooKid: " + responseData.getBody().asString());

		test.log(Status.INFO, "Response Data for updating book with Wrong BooKid " + responseData.getBody().asString());
	}

//	// Neg Test7: Verify Delete book when pass wrong Bookid.

	@Test(priority = 1, dependsOnMethods = { "tests.VerifyPositiveScenarioForUserAccountTest.NewUserCreation",
			"tests.VerifyPositiveScenarioForUserAccountTest.GenerateNewTokentoAccessUser" })
	public void checkIfBookDeleted() {

		test = reportobj.createTest("Verify whether Book is Deleted from BookList");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").when()
				.delete(REQUEST_URL_DELETE_BOOK + ConfigFileReader.get("BOOK_ID")).then().statusCode(404).extract()
				.response();

		String responseBody = responseData.getBody().asString();
		
		message = responseData.jsonPath().getString("detail");
		Assert.assertEquals(message, "Book not found");
		
		test.log(Status.INFO, "Book collection after delete: " + responseBody);
		
		Assert.assertFalse(responseBody.contains(ConfigFileReader.get("BOOK_ID")),
				"Deleted book still appears in the Book's collection.");

		test.log(Status.PASS, "Deleted book is no longer in the Book's collection.");
	}

	@Test()
	public void deleteBookFromCollection() {

		test = reportobj.createTest("Delete Book but provide wrong BookId");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json")

				.when().delete(REQUEST_URL_DELETE_BOOK + GeneratedRandomBookID).then().statusCode(404)
				.extract().response();
		String ResMessage = responseData.jsonPath().getString("message");
		test.log(Status.PASS, "Attempt to delete book with passing wrong Bookid was correctly rejected.");

		System.out
				.println("Response Data for delete books with passing wrong Bookid: " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for delete books with passing wrong Bookid " + responseData.getBody().asString());
	}
}
