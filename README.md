# Momei MongoDB

The primary goal of the Momei-MongoDB project is to make it easier and flexible and efficient to **prepare test data before** running test and **clean up test data after** running test in **a application using MongoDB as data store**.

This test framework uses mongo scripts written in **javascript** language to prepare and clean up, and let you utilize all the MongoDB enhancements to **javascript**. It's quick and easy to get started.

## Get started

1. Write a test extending the **MongoAwareBaseTest** class:

  ```java
  @TestData
  public class BooksRepoTest extends MongoAwareBaseTest {

      @Test
      @TestData
      public void findBooks() {
          logger.info("Test find logic!");
      }

  }
  ```

2. Annotate you test with the annotation **@TestData**. If **@TestData** presents on a test class, the framework will read a javascript file named **{test class name}.js** which locates in the same directory with the test class. In this example, the BooksRepoTest.js will be read.

3. Annotate test method with the annotation **@TestData**. If **@TestData** presents on a test method, the framework will execute the preparing javascript function named **prepare4_{test method name}** before test method is invoked and the cleaning javascript function named **cleanup4_{test method name}** after test method is invoked. The two javascript functions sit in the **{test class name}.js** file. In this example, BooksRepoTest.js is as follows:

  ```javascript
  // Prepare test data for test method 'findBooks'
  prepare4_findBooks = function() {
      for (var i = 1; i <= 100; i++) {
          db.books.insert({ _id: ObjectId(), name: ("Thinking in Java "+i) });
      }
  }

  // Clean up test data for test method 'findBooks'
  cleanup4_findBooks = function() {
      db.books.remove({ name: { $regex: /Thinking in Java/ } });
  }
  ```

  The function definition must be as follows:

  ```javascript
  <prepare4_|cleanup4_>{test method name} = function() {
      // preparing or cleaning up logic
  }
  ```

4. Lastly configure the MongoDB. Go to the class path root direction, and new a file named **mongo.properties**:

  ```
  # mongo.host and mongo.port are optional.
  # If not given, host and port default to localhost and 27017 respectively
  #mongo.host=localhost
  #mongo.port=27017

  # mongo.db required
  mongo.db=test
  ```

That's all!
