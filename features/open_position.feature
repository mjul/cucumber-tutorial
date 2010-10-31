Feature: Open Position
  In order to open a position
  As a trader
  I want to send a trade order

  Scenario: Market Order BUY
    Given that my position in EURUSD is 0 at 1.34700
    And the market for EURUSD is at [1.34662;1.34714]
    When I submit an order to BUY 1000000 EURUSD at MKT
    Then a trade should be made at 1.34714
    And my position should show LONG 1000000 EURUSD at 1.34714

  Scenario Outline: Market Order SELL
    Given that my position in EURUSD is 0 at 1.34700
    And the market for EURUSD is at [<bid>;<ask>]
    When I submit an order to SELL <quantity> EURUSD at MKT
    Then a trade should be made at <bid>
    And my position should show SHORT <quantity> EURUSD at <bid>

    Examples:
      |  bid     | ask     | quantity |
      |  1.34662 | 1.34714 | 1000000  |
      |  1.40000 | 1.40050 | 1000000  |
