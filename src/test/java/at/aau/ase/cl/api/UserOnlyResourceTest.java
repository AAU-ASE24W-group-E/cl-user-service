package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.LoginRequest;
import at.aau.ase.cl.api.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class UserOnlyResourceTest {

    @Test
    void shouldNotAccessUserResourceWhenAnonymous() {
        get("/user-only")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void shouldAccessUserAndGetIdentityWhenUserAuthenticated() {
        User user = new User("email0@mail.com", "john0", null, "SomePassword", "USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "john0";
        loginRequest.password = "SomePassword";

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        System.out.println("Generated Token: " + token);


        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/user-only")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("This is a user-only resource"));
    }
}
