package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.LoginRequest;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.util.JWT_Util;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
                .body("initialLoginPending", equalTo(true))
                .body("username", equalTo("john1"))
                .body("address", equalTo(null))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }

    @Test
    void createUserWithAddress() {
        Address address = new Address(49.21303, 20.49321);
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
                .body("initialLoginPending", equalTo(true))
                .body("address.latitude", equalTo(49.21303F))
                .body("address.longitude", equalTo(20.49321F))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("SomePassword", hashedPassword), "Password does not match the hash");
    }


    @Test
    void getUserWithAddress() {
        Address address = new Address(49.21303, 20.49321);
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
                .body("initialLoginPending", equalTo(true))
                .body("address.latitude", equalTo(49.21303F))
                .body("address.longitude", equalTo(20.49321F))
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
                .body("initialLoginPending", equalTo(true))
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
        Address address = new Address(49.21303, 20.49321);

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
                .body("address.latitude", equalTo(49.21303F))
                .body("address.longitude", equalTo(20.49321F))
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
    void updateAddressOfUser() {
        Address oldAddress = new Address(49.21303, 20.49321);
        Address newAddress = new Address(50.21303, 20.49321);
        User user = new User("email6@mail.com", "john6", oldAddress, "SomePassword", "USER");

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
                .body(oldAddress)
                .post("/user/{id}/address")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("address.latitude", equalTo(49.21303F))
                .body("address.longitude", equalTo(20.49321F))
                .body("role", equalTo("USER"));

        given().pathParam("id", userId)
                .contentType(ContentType.JSON)
                .body(newAddress)
                .put("/user/{id}/address")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("address.latitude", equalTo(50.21303F))
                .body("address.longitude", equalTo(20.49321F))
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
    void loginAndValidateUserRoleToken() {
        User user = new User("email7@mail.com", "john7", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email7@mail.com"))
                .body("username", equalTo("john7"))
                .body("role", equalTo("USER"))
                .extract()
                .path("id");

        assertNotNull(userId, "User ID should not be null after creation");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "john7";
        loginRequest.password = "password123";

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/user/login")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("token", notNullValue())
                .extract()
                .path("token");

        assertNotNull(token, "JWT token should not be null after login");

        boolean isRoleValid = jwtUtil.validateRole(token, "USER");
        assertTrue(isRoleValid, "Token should be valid for the role 'USER'");
    }

    @Test
    void loginAndUpdateLoginState() {
        User user = new User("email8@mail.com", "john8", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("initialLoginPending", equalTo(true))
                .body("email", equalTo("email8@mail.com"))
                .body("username", equalTo("john8"))
                .body("role", equalTo("USER"))
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .put("/user/" + userId + "/set-initial-login")
                .then()
                .statusCode(200)
                .log().body(true)
                .extract()
                .path("id");

        String hashedPassword = given()
                .contentType(ContentType.JSON)
                .get("/user/" + userId)
                .then()
                .statusCode(200)
                .log().body(true)
                .body("initialLoginPending", equalTo(false))
                .body("email", equalTo("email8@mail.com"))
                .body("username", equalTo("john8"))
                .body("role", equalTo("USER"))
                .extract()
                .path("password");

        assertTrue(BcryptUtil.matches("password123", hashedPassword), "Password does not match the hash");
    }
}
