package HW4.test;

import HW4.Endpoints;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.util.Base64;


import static io.restassured.RestAssured.given;


public class BigPictTest extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/logo5.jpg";
    static String encodedFile;
    String uploadedImageId;
    String imageHash;
    MultiPartSpecification Base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;
    static RequestSpecification requestSpecificationWithAuthAndMultipartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void beforeTest() {


        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        Base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File("src/test/resources/logo5.jpg"))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "pict")
                .addFormParam("type","gif")
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(Base64MultiPartSpec)
                .build();

    }

    @Test
    void uploadFileJPEGTest() {
        // beforeTest("jpeg");
        uploadedImageId = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(Endpoints.UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }/*

    void beforeTest(String TYPE_OF_FILE) {

        byte[] byteArray = getFileContent("src/test/resources/logo.jpeg");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File("src/test/resources/logo.jpeg"))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Picture")
                .addFormParam("type", TYPE_OF_FILE)
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

    }*/


    private byte[] getFileContent(String PATH_TO_IMAGE) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}