package HW4.test;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class BaseTest {
    static ResponseSpecification positiveResponseSpecification;
    static RequestSpecification requestSpecificationWithAuth;
  //  static ResponseSpecification negativeResponseSpecification;

    static Properties properties = new Properties();
    static String token;
    static String username;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        getProperties();
        token = properties.getProperty("token");
        username = properties.getProperty("username");

        positiveResponseSpecification = new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        requestSpecificationWithAuth = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();

    /*    negativeResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.HTML)
                .expectStatusCode(404)
                .build(); */
        RestAssured.responseSpecification = positiveResponseSpecification;
    }

    private static void getProperties() {
        try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}