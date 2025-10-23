Feature: CRUD tests for bankproducts API

  # Spring Boot application must be running before executing API tests (http://localhost:8080)
  # API tests updates values in DB used by the application
  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Accept = 'application/json'

  # ----------------------------
  # CREATE
  # ----------------------------
  Scenario: Create a new bank product
    * def sampleProduct = { "title": "My product" }
    * param notify = true
    Given path 'bankproducts'
    And request sampleProduct
    When method POST
    Then status 201
    And match response.title == 'My product'

  # ----------------------------
  # READ (GET by ID)
  # ----------------------------
  Scenario: Get a bank product by ID
    * def sampleProduct = { "title": "My product" }
    * params { title: 'My product', type: 'Savings' }
    # Create product for this scenario
    Given path 'bankproducts'
    And request sampleProduct
    When method POST
    Then status 201
    * def productId = response.id

    # Now read it
    # This is equivalent to bankproducts/{productId}. Karate automatically joins the path segments with /
    Given path 'bankproducts', productId
    When method GET
    Then status 200
    And match response.id == productId
    And match response.title == 'My product'

  # ----------------------------
  # UPDATE
  # ----------------------------
  Scenario: Update a bank product
    * def sampleProduct = { "title": "My product" }
    # Create product for this scenario
    Given path 'bankproducts'
    And request sampleProduct
    When method POST
    Then status 201
    * def productId = response.id

    # Update the product
    Given path 'bankproducts', productId
    And request { "title": "Updated product" }
    When method PUT
    Then status 200
    And match response.id == productId
    And match response.title == 'Updated product'

  # ----------------------------
  # DELETE
  # ----------------------------
  Scenario: Delete a bank product
    * def sampleProduct = { "title": "My product" }
    # Create product for this scenario
    Given path 'bankproducts'
    And request sampleProduct
    When method POST
    Then status 201
    * def productId = response.id

    # Delete the product
    Given path 'bankproducts', productId
    When method DELETE
    Then status 204

    # Verify it no longer exists
    Given path 'bankproducts', productId
    When method GET
    Then status 404


# ----------------------------
# How to pass request params? Use below code
#  * param notify = true --> one request param. Not used in this application. /bankproducts?title=My+product
#  * params { title: 'My product', type: 'Savings' } --> one request param. Not used in this application. /bankproducts?title=My+product&type=Savings
# ----------------------------