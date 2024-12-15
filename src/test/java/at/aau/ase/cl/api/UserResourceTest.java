package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.LoginRequest;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.util.JWT_Util;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {
    @Inject
    JWT_Util jwtUtil;

    @Test
    void createUserWithoutAddress() {
        User user = new User("email1@mail.com", "john1", null, "SomePassword", "USER");

        String hashedPassword = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email1@mail.com"))
                .body("username", equalTo("john1"))
                .body("address", equalTo(null))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }

    @Test
    void createUserWithAddress() {
        Address address = new Address("some street", "Klagenfurt", "9020", "AT");
        User user = new User("email2@mail.com", "john2", address, "SomePassword", "USER");

        String hashedPassword = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email2@mail.com"))
                .body("username", equalTo("john2"))
                .body("address.street", equalTo("some street"))
                .body("address.city", equalTo("Klagenfurt"))
                .body("address.postalCode", equalTo("9020"))
                .body("address.countryCode", equalTo("AT"))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }

    @Test
    void getUserWithAddress() {
        Address address = new Address("some street", "Klagenfurt", "9020", "AT");
        User user = new User("email3@mail.com", "john3", address, "SomePassword", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        String hashedPassword = given()
                .pathParam("id", userId)
                .contentType(ContentType.JSON)
                .body(user)
                .get("/user/{id}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email3@mail.com"))
                .body("username", equalTo("john3"))
                .body("address.street", equalTo("some street"))
                .body("address.city", equalTo("Klagenfurt"))
                .body("address.postalCode", equalTo("9020"))
                .body("address.countryCode", equalTo("AT"))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }

    @Test
    void getUserWithoutAddress() {
        User user = new User("email4@mail.com", "john4", null, "SomePassword", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        String hashedPassword = given()
                .pathParam("id", userId)
                .contentType(ContentType.JSON)
                .body(user)
                .get("/user/{id}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email4@mail.com"))
                .body("username", equalTo("john4"))
                .body("address", equalTo(null))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }

    @Test
    void addAddressToUser() {
        User user = new User("email5@mail.com", "john5", null, "SomePassword", "USER");
        Address address = new Address("some street", "Klagenfurt", "9020", "AT");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        given().pathParam("id", userId)
                .contentType(ContentType.JSON)
                .body(address)
                .post("/user/{id}/address")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("address.street", equalTo("some street"))
                .body("address.city", equalTo("Klagenfurt"))
                .body("address.postalCode", equalTo("9020"))
                .body("address.countryCode", equalTo("AT"))
                .body("role", equalTo("USER"));

        String hashedPassword = given()
                .pathParam("id", userId)
                .contentType(ContentType.JSON)
                .get("/user/{id}")
                .then()
                .statusCode(200)
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }



    @Test
    public void testUserCreationLoginAndTokenValidation() {
        User user = new User();
        user.username = "testuser";
        user.email = "test@example.com";
        user.password = "password123";
        user.role = "admin";

        Response createUserResponse = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertNotNull(createUserResponse);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "testuser";
        loginRequest.password = "password123";

        String token = given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

        assertNotNull(token);

        boolean isRoleValid = jwtUtil.validateRole(token, "admin");
        assertTrue(isRoleValid, "Token should be valid for the role 'admin'");
    }
}
