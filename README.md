# Cucumber Tutorial for Clojure

This project shows you how to set up and use Cucumber for Clojure.

You can read more about Cucumber here:

* The main Cucumber site is [Cukes.info](http://cukes.info/).
* You can get the Cucumber Java libraries here [cuke4duke](http://wiki.github.com/aslakhellesoy/cuke4duke/)

## Usage

The project includes an example specification in the `features`
folder, and the step definition that binds the feature file to
executable test code in the `features/step_definitions` folder.

For example, to test that we can open a position in a currency trading
application, you could write a feature like this:

    Feature: Open Position
      In order to open a position
      As a trader
      I want to send a trade order
    
      Scenario: Market Order
        Given that my position in EURUSD is 0 at 1.34700
        And the market for EURUSD is at [1.34662;1.34714]
        When I submit an order to BUY 1000000 EURUSD at MKT
        Then a trade should be made at 1.34714
        And my position should show LONG 1000000 EURUSD at 1.34714

That is you specification.  Now add step definitions to the
`features/step_definitions` folder to connect the specification
mini-language thus invented to code by matching a regex to the "given"
text and returning a function of the values matched by the regex,
e.g.
  
    (Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
           (fn [cross qty price]
             (dosync 
               (set-position! cross qty price))))

### Specifications in your own Language    
You can define Cucumber features in many languages. Here is the Danish
version of the example above, from the file
`features/open_position_da.feature`:

    #language: da
    Egenskab: Åbn position
      For at åbne en position
      Som en valutahandler
      Ønsker jeg at afgive en handelsordre
    
      Scenarie: Markedsordre
        Givet at min position i EURUSD er 0 købt til kurs 1,34700
        Og markedsprisen for EURUSD er [1,34662;1,34714]
        Når jeg afgiver en ordre om at KØBE 1000000 EURUSD til MARKEDSPRIS
        Så skal en handel ske til kurs 1,34714
        Og min position skal være LANG 1000000 EURUSD købt til kurs 1,34714

### Test the same Scenario with Multiple Examples 
You can create a template, called a Scenario Outline, and have
Cucumber evaluate it with different sets of values substituted into
the template fields. The sets of values are called Examples.

For example, to evaluate selling euro-dollar at various price points
use the following Scenario Outline from the file 
`features/open_position.feature`:

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

### Use Tables of Values in Specifications
*NOTE: This feature is not working with the current version of the
Clojure Cucumber bindings.*

You can pass tabular data to your step definitions in the form of an
object implementing the cuke4duke.Table interface. This is useful for
setting up context or verifying multiple correlated assertiong.

For example, if we want to put conditional exits on a currency
position we can create two orders to take profit if the market rises
or limit the loss if the price falls respectively. These are called
LIMIT and STOP orders and they should be of the OCO-type, meaning that
one cancels the other: if either one is triggered the other one should
be cancelled.

See the file `features/conditional_order.feature` for an example:

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

The following helper function is useful for extracting the values from
the table into a sequence of maps. See the
`features/step_definitions/open_position_steps.clj` file for an
example of how to use it to write the step definitions:

    (defn hashes [table]
      "Get the data from a Cucumber Table as a list of maps.
       The result is a sequence of maps for each non-header row of the
       table. Each map contains the values of each column in the row keyed
       by the corresponding column names taken from the header row."
      (map #(into {} %) (.hashes table)))
    
## Installation

To get the dependencies to run the code in this tutorial do this:

    lein deps
    lein cuke-gems

Now, `lein cuke` will run the Cucumber tests.

    lein cuke

You should now see something like this:

    Feature: Open Position
      In order to open a position
      As a trader
      I want to send a trade order
    
      Scenario: Market Order                                       # features/open_position.feature:6
        Given that my position in EURUSD is 0 at 1.34700           # ^that my position in (\w{6}) is (\d+) at ([\d.]+)$
        And the market for EURUSD is at [1.34662;1.34714]          # ^the market for (\w{6}) is at \[([\d.]+);([\d.]+)\]$
        When I submit an order to BUY 1000000 EURUSD at MKT        # ^I submit an order to BUY (\d+) (\w{6}) at MKT$
        Then a trade should be made at 1.34714                     # ^a trade should be made at ([\d.]+)$
        And my position should show LONG 1000000 EURUSD at 1.34714 # ^my position should show LONG (\d+) (\w{6}) at ([\d.]+)$
    
    #language: da
    Egenskab: Åbn position
      For at åbne en position
      Som en valutahandler
      Ønsker jeg at afgive en handelsordre
    
      Scenarie: Markedsordre                                                   # features/open_position_da.feature:7
        Givet at min position i EURUSD er 0 købt til kurs 1,34700              # ^at min position i (\w{6}) er (\d+) købt til kurs ([\d,]+)$
        Og markedsprisen for EURUSD er [1,34662;1,34714]                       # ^markedsprisen for (\w{6}) er \[([\d,]+);([\d,]+)\]$
        Når jeg afgiver en ordre om at KØBE 1000000 EURUSD til MARKEDSPRIS     # ^jeg afgiver en ordre om at KØBE (\d+) (\w{6}) til MARKEDSP RIS$
        Så skal en handel ske til kurs 1,34714                                 # ^skal en handel ske til kurs ([\d,]+)$
        Og min position skal være LANG 1000000 EURUSD købt til kurs 1,34714 # ^min position skal være LANG (\d+) (\w{6}) købt til kurs  ([\d,]+)$
    
    2 scenarios (2 passed)
    10 steps (10 passed)

### Setting Up a Project for Cucumber BDD

First, edit the leiningen project.clj file to include the Cucumber
library and the lein-cuke extension to leiningen. You should have
something like this:


    (defproject cuketut "1.1.0-SNAPSHOT"
      :description "Setting up and using Cucumber with Clojure."
      :dependencies [[org.clojure/clojure "1.2.0"]
                     [org.clojure/clojure-contrib "1.2.0"]]
      :dev-dependencies [[org.clojars.mjul/lein-cuke "1.0.0"]])


Now install cuke: 

    lein deps
    lein cuke-gems

Create a feature folder and a folder for step definitions inside it in
the project root:

    make -p features/step_definitions/
   
As you write your code, put the feature definitions in the `features`
folder and the step definitions that link them to your code in the
`step_definitions` subfolder.

To run the Cucumber tests from lein:

    lein cuke 

To get extra information from Cucumber use the verbose flag:

    lein cuke --verbose

If you want to run only a specific feature or set of features you can
pass file names or directories to `lein cuke`:

    lein cuke features/open_position_da.feature


## License

Copyright (C) 2010 Martin Jul (www.mjul.com)

Distributed under the MIT License. See the LICENSE file for details.


## About the Author

Martin Jul is a software architect and partner in Ative, a
Copenhagen-based consultancy specialised in doing and teaching lean
software development.

His work is currently focused on building distributed,
high-performance low-latency financial trading applications.

He is also the organiser of the Copenhagen Clojure meet-ups:

* Twitter: [@mjul](http://twitter.com/mjul)
* Work: [Ative](http://www.ative.dk) 
* Blog: [Ative at Work](http://community.ative.dk/blogs/)
* Copenhagen Clojure Meet-Up [dates here](http://www.ative.dk/om-ative/arrangementer.aspx)
