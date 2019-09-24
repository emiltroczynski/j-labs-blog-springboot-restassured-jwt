package jlabsblog.jwt.task;

import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import jlabsblog.jwt.security.JwtSecurityConstants;
import jlabsblog.jwt.user.JwtUser;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@TestPropertySource("file:src/main/resources/application.properties")
public class TaskControllerTests extends AbstractTestNGSpringContextTests {
  private Long id;
  private Task task;
  private RequestSpecification specification;

  @Value("${server.port}")
  private int portNumber;

  @BeforeClass
  public void authorization() {
    JwtUser user = new JwtUser();
    user.setUsername("username");
    user.setPassword("password");

    given()
        .basePath("/users/sign-up")
        .port(portNumber)
        .contentType("application/json")
        .body(user)
        .when()
        .post()
        .then()
        .statusCode(200);

    String token =
        given()
            .basePath("/login")
            .port(portNumber)
            .contentType("application/json")
            .body(user)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .header(JwtSecurityConstants.HEADER_STRING);

    specification =
        new RequestSpecBuilder()
            .addHeader(JwtSecurityConstants.HEADER_STRING, token)
            .setBasePath("/tasks")
            .setPort(portNumber)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
  }

  @BeforeMethod
  public void createTask() {
    task = new Task("initialValue");
    given().spec(specification).contentType("application/json").body(task).when().post();
  }

  @AfterMethod
  public void cleanUp() {
    deleteTask(id);
  }

  @Test
  public void addTask() {
    Task retrievedTask = retrieveTask();

    assertTask(retrievedTask, task);
  }

  @Test
  public void editTask() {
    String updatedDescription = "editTaskUpdated";
    Task updatedTask = new Task(updatedDescription);

    Task retrievedTask = retrieveTask();

    given()
        .spec(specification)
        .contentType("application/json")
        .when()
        .body(updatedTask)
        .put(String.format("%s", retrievedTask.getId()))
        .then()
        .statusCode(200);

    retrievedTask = retrieveTask();

    assertTask(retrievedTask, updatedTask);
  }

  @Test
  public void deleteTask() {
    Task retrievedTask = retrieveTask();

    given()
        .spec(specification)
        .when()
        .delete(String.format("%s", retrievedTask.getId()))
        .then()
        .statusCode(200);

    retrievedTask = retrieveTask();

    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(retrievedTask).isNull();
    assertions.assertAll();
  }

  private Task retrieveTask() {
    Task retrievedTask =
        Arrays.stream(
                given()
                    .spec(specification)
                    .when()
                    .get()
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Task[].class))
            .reduce((first, second) -> second)
            .orElse(null);
    if (retrievedTask != null) {
      id = retrievedTask.getId();
    } else {
      id = null;
    }
    return retrievedTask;
  }

  private void deleteTask(Long id) {
    if (id != null) {
      given().spec(specification).when().delete(String.format("%s", id)).then().statusCode(200);
    }
  }

  private void assertTask(Task actual, Task expected) {
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
    assertions.assertThat(actual.getId()).isGreaterThan(0);
    assertions.assertAll();
  }
}
