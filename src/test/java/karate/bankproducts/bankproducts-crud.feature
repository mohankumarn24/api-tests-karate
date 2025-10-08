Feature: CRUD tests for bankproducts API

  Background:
    * url baseUrl
    * header Accept = 'application/json'
    * header Content-Type = 'application/json'

  # ----------------------------
  # CREATE
  # ----------------------------
  Scenario: Create a new bank product
    * def sampleProduct = { "title": "My product" }
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
