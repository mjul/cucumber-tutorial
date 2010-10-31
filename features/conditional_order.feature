Feature: Conditional Order
  In order to guard my positions
  As a trader
  I want to send a trade order with conditional stop loss and take profit orders.

  Scenario: Market Order with Take Profit and Stop Loss guards
    Given that my position in EURUSD is 0 at 1.34700
    And the market for EURUSD is at [1.34662;1.34714]
    And I have no open orders in EURUSD
    When I submit an order to BUY 1000000 EURUSD at MKT with TARGET 1.3800 and STOP 1.3200
    Then a trade should be made at 1.34714
    And my position should show LONG 1000000 EURUSD at 1.34714
    And my open orders should contain these OCO-orders
      | Side | Quantity | Cross  | Type  | Price  | 
      | SELL | 1000000  | EURUSD | LIMIT | 1.3800 | 
      | SELL | 1000000  | EURUSD | STOP  | 1.3200 |
