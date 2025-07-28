package tests;

import static constants.ApiEndPointsKey.*;
import static io.restassured.RestAssured.given;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.response.Response;

public class VerifyPositiveScenarioForBookTest extends BasicAPITest {
	public Response responseData;
	public static String BookId;
	public static String CreatedBookName;
	public static String bookTitleName;
	public static String UpdatedBookTitleName;
	public static String UpdatedBookAuthorName;
	public static String Created_BookId;
	public static String Book_AuthorName;
	public static String Book_PublishedYear;
	public static String Book_SummaryName;
	public static String message;

	public static String GeneratedRandomBookid = ("") + Math.abs(UUID.randomUUID().toString().hashCode());

	// TestCase1: Verify Create Book with valid User Access Token 

	@Test(priority = 1, dependsOnMethods = { "tests.VerifyPositiveScenarioForUserAccountTest.NewUserCreation",
			"tests.VerifyPositiveScenarioForUserAccountTest.GenerateNewTokentoAccessUser" })
	public void createBookInCollection() {
		test = reportobj.createTest("Create Book");

		String requestBody = String.format("""
				{
				    "id": "%s",
				    "name": "%s",
				    "author": "%s",
				    "published_year":" %s",
				    "book_summary": "%s"
				}
				""", GeneratedRandomBookid, ConfigFileReader.get("BOOK_NAME"), ConfigFileReader.get("BOOK_AUTHOR_NAME"),
				ConfigFileReader.get("BOOK_PUBLISHED_YEAR"), ConfigFileReader.get("BOOK_SUMMARY"));

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(requestBody).when().post(REQUEST_URL_ADD_BOOKS).then()
				.statusCode(200).extract().response();

		System.out.println("USER Access Token Used :-"  + VerifyPositiveScenarioForUserAccountTest.token);
		System.out.println("Response Data for Create book is:- " + responseData.getBody().asString());

		test.log(Status.INFO, "Response Data for Create book is :- " + responseData.getBody().asString());
	
		CreatedBookName = responseData.jsonPath().getString("name");
		System.out.println("Created Book Name Listed under Book Collection: " + CreatedBookName);
		Assert.assertEquals(ConfigFileReader.get("BOOK_NAME"), CreatedBookName);
		
		Created_BookId = responseData.jsonPath().getString("id");
		System.out.println("Created Bookid Listed under Book Collection: " + Created_BookId);
		Assert.assertEquals(GeneratedRandomBookid, Created_BookId);
		
		Book_AuthorName = responseData.jsonPath().getString("author");
		System.out.println("Author Name Listed under Book Collection: " + Book_AuthorName);
		Assert.assertEquals(ConfigFileReader.get("BOOK_AUTHOR_NAME"), Book_AuthorName);
		
		Book_PublishedYear = responseData.jsonPath().getString("published_year");
		System.out.println("Book PublishedYear Listed under Book Collection: " + Book_PublishedYear);
		Assert.assertEquals(ConfigFileReader.get("BOOK_PUBLISHED_YEAR"), Book_PublishedYear);
		
		Book_SummaryName = responseData.jsonPath().getString("book_summary");
		System.out.println("Book SummaryName Listed under Book Collection: " + Book_SummaryName);
		Assert.assertEquals(ConfigFileReader.get("BOOK_SUMMARY"), Book_SummaryName);
		
		BookId = responseData.jsonPath().getString("id");
		Assert.assertNotNull(BookId);
		test.log(Status.PASS, "Book created with Book Id : " + BookId);
	}

	// TestCase2: Verify created Book is listed under Book Collection.

	@Test(priority = 2, dependsOnMethods = "createBookInCollection")
	public void testGetAllBooks() {
		test = reportobj.createTest("Get Created Books from BookList");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.when().get(REQUEST_URL_GET_BOOKS + GeneratedRandomBookid).then().statusCode(200).extract().response();

		System.out.println("Response Data for checking if book created: " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for getting the newly created book is " + responseData.getBody().asString());

		bookTitleName = responseData.jsonPath().getString("name");
		Assert.assertNotNull(bookTitleName);
		
		Created_BookId = responseData.jsonPath().getString("id");
		Assert.assertNotNull(Created_BookId);
		
		Book_AuthorName = responseData.jsonPath().getString("author");
		Assert.assertNotNull(Book_AuthorName);
		
		test.log(Status.PASS, "User present with BookTitle Name : " + bookTitleName);
	}
	
	// TestCase3: Verify update Book with Valid Book Details 

	@Test(priority = 3, dependsOnMethods = { "createBookInCollection" })
	public void updateBookInCollection() {
		test = reportobj.createTest("Update Few details of Book");
		String requestBody = String.format("""
				  {
				    "id": "%s",
				    "name": "%s",
				    "author": "%s"
				   }
				""", BookId, ConfigFileReader.get("UPDATE_BOOK_NAME"), ConfigFileReader.get("UPDATE_AUTHOR_NAME"));

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(requestBody).when().put(REQUEST_URL_UPDATE_BOOKS + BookId).then()
				.statusCode(200).extract().response();

		UpdatedBookTitleName = responseData.jsonPath().getString("name");
		Assert.assertEquals(ConfigFileReader.get("UPDATE_BOOK_NAME"), UpdatedBookTitleName);
		
		UpdatedBookAuthorName = responseData.jsonPath().getString("author");
		Assert.assertEquals(ConfigFileReader.get("UPDATE_AUTHOR_NAME"), UpdatedBookAuthorName);

		System.out.println("Response Data for update Books Is : " + responseData.getBody().asString());
		test.log(Status.INFO, "Response Data for updating Book is " + responseData.getBody().asString());
	}

// TestCase4: Verify Delete Book with Valid BookId and User Access Token

	@Test(priority = 4, dependsOnMethods = { "createBookInCollection" })
	public void deleteBookFromCollection() {
		test = reportobj.createTest("Delete Book From Book Collection");

		System.out.println("Token used for Book Deletion Is :- " + VerifyPositiveScenarioForUserAccountTest.token);
		Assert.assertNotNull(VerifyPositiveScenarioForUserAccountTest.token, "Token is null!");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").when().delete(REQUEST_URL_DELETE_BOOK + BookId).then().statusCode(200)
				.extract().response();

		System.out.println("Response Data for delete book Is :- " + responseData.getBody().asString());
		test.log(Status.INFO, "Response Data for deleted book is:-" + responseData.getBody().asString());

		message = responseData.jsonPath().getString("message");
		Assert.assertEquals(message, "Book deleted successfully");

		System.out.println("DELETE Response is empty as expected");
		test.log(Status.INFO, "DELETE Response is empty as expected");
	}

// TestCase5: Verify Deleted Book is no longer listed under Book Collection.

	@Test(priority = 5, dependsOnMethods = "deleteBookFromCollection")
	public void checkIfBookDeleted() {

		test = reportobj.createTest("Verify whether Book is Deleted from BookList");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").when().delete(REQUEST_URL_DELETE_BOOK + BookId).then().statusCode(404)
				.extract().response();

		String responseBody = responseData.getBody().asString();
		System.out.println("Book not found: " + responseBody);

		message = responseData.jsonPath().getString("detail");
		Assert.assertEquals(message, "Book not found");

		test.log(Status.INFO, "Book collection after delete: " + responseBody);

		// Verify the BookId is no longer in the Book's collection
		Assert.assertFalse(responseBody.contains(VerifyPositiveScenarioForBookTest.BookId),
				"Deleted book still appears in the user's collection.");

		test.log(Status.PASS, "Deleted Book is no longer in the Book's collection.");
	}
}
