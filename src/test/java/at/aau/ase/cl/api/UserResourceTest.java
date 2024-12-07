package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UserResourceTest {

    @Test
    void createUserWithoutAddress() {
        User user = new User( "email1@mail.com", "john1", null);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/user")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email1@mail.com"))
                .body("username", equalTo("john1"))
                .body("address", equalTo(null));
    }

    @Test
    void createUserWithAddress() {
        Address address = new Address("some street", "Klagenfurt", "9020", "AT");
        User user = new User( "email2@mail.com", "john2", address);

        given()
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
                .body("address.countryCode", equalTo("AT"));
    }

    @Test
    void getUserWithAddress() {
        Address address = new Address("some street", "Klagenfurt", "9020", "AT");
        User user = new User( "email3@mail.com", "john3", address);

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
                .body("address.countryCode", equalTo("AT"));
    }

    @Test
    void getUserWithoutAddress() {
        User user = new User( "email4@mail.com", "john4", null);

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
                .body(user)
                .get("/user/{id}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("email", equalTo("email4@mail.com"))
                .body("username", equalTo("john4"))
                .body("address", equalTo(null));
    }

    @Test
    void addAddressToUser() {
        User user = new User( "email5@mail.com", "john5", null);
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
                .body("address.countryCode", equalTo("AT"));
    }

    @AfterEach
    void tearDown () {

    }
}