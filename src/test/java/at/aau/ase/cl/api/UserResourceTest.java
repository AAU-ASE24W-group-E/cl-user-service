package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.*;
import at.aau.ase.cl.service.ResetPasswordService;
import at.aau.ase.cl.util.JWT_Util;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserResourceTest {
    @Inject
    JWT_Util jwtUtil;

    @Inject
    ResetPasswordService resetPasswordService;

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

    @Test
    void loginWithWrongPassword() {
        User user = new User("email9@mail.com", "john9", null, "password123", "USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("initialLoginPending", equalTo(true))
                .body("email", equalTo("email9@mail.com"))
                .body("username", equalTo("john9"))
                .body("role", equalTo("USER"))
                .extract()
                .path("id");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "john9";
        loginRequest.password = "incorrectPassword";

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/user/login")
                .then()
                .statusCode(401)
                .body("type", equalTo("InvalidPasswordException"))
                .body("message", containsString("Invalid password for user: john9"));
    }

    @Test
    void updateUserInfoSuccessfully() {
        User user = new User("email10@mail.com", "john10", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        UserPayload updatedUserInfo = new UserPayload();
        updatedUserInfo.email = "new-email@mail.com";
        updatedUserInfo.username = "new-username";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", userId)
                .body(updatedUserInfo)
                .put("/user/{id}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("new-email@mail.com"))
                .body("username", equalTo("new-username"));
    }

    @Test
    void updateUserInfoConflict() {
        User user1 = new User("email11@mail.com", "john11", null, "password123", "USER");
        User user2 = new User("email12@mail.com", "john12", null, "password123", "USER");

        String user1Id = given()
                .contentType(ContentType.JSON)
                .body(user1)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .body(user2)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        UserPayload conflictUserInfo = new UserPayload();
        conflictUserInfo.email = "email12@mail.com";
        conflictUserInfo.username = "new-username";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", user1Id)
                .body(conflictUserInfo)
                .put("/user/{id}")
                .then();
    }


    @Test
    void updatePasswordSuccessfully() {
        User user = new User("email13@mail.com", "john13", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PasswordPayload passwordPayload = new PasswordPayload();
        passwordPayload.oldPassword = "password123";
        passwordPayload.newPassword = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", userId)
                .body(passwordPayload)
                .put("/user/{id}/password")
                .then()
                .statusCode(200);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "john13";
        loginRequest.password = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void updatePasswordInvalidOldPassword() {
        User user = new User("email14@mail.com", "john14", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PasswordPayload passwordPayload = new PasswordPayload();
        passwordPayload.oldPassword = "wrongOldPassword";
        passwordPayload.newPassword = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", userId)
                .body(passwordPayload)
                .put("/user/{id}/password")
                .then()
                .statusCode(401)
                .body("type", equalTo("InvalidPasswordException"))
                .body("message", containsString("Invalid old password for user"));
    }

    @Test
    void forgotPasswordSuccessfully() {
        User user = new User("testuser@mail.com", "testuser", null, "password123", "USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200);

        UserEmailPayload payload = new UserEmailPayload();
        payload.email = "testuser@mail.com";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/user/forgot-password")
                .then()
                .statusCode(200)
                .body(equalTo("Password reset email sent"));
    }

    @Test
    void forgotPasswordInvalidEmail() {
        UserEmailPayload payload = new UserEmailPayload();
        payload.email = "";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/user/forgot-password")
                .then()
                .statusCode(400)
                .body(equalTo("Email cannot be null or empty"));
    }

    @Test
    void forgotPasswordUserNotFound() {
        UserEmailPayload payload = new UserEmailPayload();
        payload.email = "nonexistentuser@mail.com";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/user/forgot-password")
                .then()
                .statusCode(404)
                .body("message", equalTo("User with identifier nonexistentuser@mail.com not found"));
    }

    @Test
    void resetPasswordSuccessfully() {
        User user = new User("testreset@mail.com", "resetuser", null, "password123", "USER");

        String userId = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        UserEmailPayload payload = new UserEmailPayload();
        payload.email = "testreset@mail.com";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/user/forgot-password")
                .then()
                .statusCode(200);


        UUID token = UUID.randomUUID();

        resetPasswordService.savePasswordResetToken(UUID.fromString(userId), token);

        ResetPasswordPayload resetPayload = new ResetPasswordPayload();
        resetPayload.token = token.toString();
        resetPayload.newPassword = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .body(resetPayload)
                .post("/user/reset-password")
                .then()
                .statusCode(200)
                .body(equalTo("Password reset successfully"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = "resetuser";
        loginRequest.password = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void resetPasswordInvalidToken() {
        ResetPasswordPayload resetPayload = new ResetPasswordPayload();
        resetPayload.token = UUID.randomUUID().toString();
        resetPayload.newPassword = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .body(resetPayload)
                .post("/user/reset-password")
                .then()
                .statusCode(404)
                .body("message", equalTo("Reset Token "+ resetPayload.token + " not found or used"));

    }

    @Test
    void resetPasswordEmptyToken() {
        ResetPasswordPayload resetPayload = new ResetPasswordPayload();
        resetPayload.token = "";
        resetPayload.newPassword = "newPassword123";

        given()
                .contentType(ContentType.JSON)
                .body(resetPayload)
                .post("/user/reset-password")
                .then()
                .statusCode(400)
                .body(equalTo("Token cannot be null or empty"));
    }


}
