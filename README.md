# Cucumber Tutorial for Clojure

This project shows you how to set up and use Cucumber for Clojure.


## Usage

This is not an introduction to Cucumber. For that, please consult the
following web sites:

* The main Cucumber site is [Cukes.info](http://cukes.info/).
* You can get the Cucumber Java libraries here [cuke4duke](http://wiki.github.com/aslakhellesoy/cuke4duke/)

The project inculdes an example specification in the `features`
folder, and the step definition that binds the feature file to
executable test code in the `features/step_definitions` folder.


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
    
    1 scenario (1 passed)
    5 steps (5 passed)


### Setting Up a Project for Cucumber BDD

First, edit the leiningen project.clj file to include the Cucumber
library and the lein-cuke extension to leiningen. You should have
something like this:


    (defproject cuketut "1.0.0-SNAPSHOT"
      :description "Setting up and using Cucumber with Clojure."
      :dependencies [[org.clojure/clojure "1.2.0"]
                     [org.clojure/clojure-contrib "1.2.0"]]
      :dev-dependencies [[org.clojars.mjul/lein-cuke "0.3.2"]
                         ;; force the correct version of gherkin as it is not correctly specified in cuke4duke 0.3.2
                         [gherkin "2.2.4"]
                         [cuke4duke "0.3.2"]])


Note that the cuke4duke dependencies are incorrect so we have to
override with a newer gherkin library to match the one that is
actually needed to make it work.

A side effect of this is that Maven may throw a NullPointerException
as it detects the dependencies to conflicting gherkin versions. Just
relax and run `lein deps` again if you see that.

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


## Background on the Dependency Chaos

Here is what happens if we do not override the Gherkin version:

    cucumber-tutorial mjul$ lein cuke
    wrong # of arguments(2 for 1) (ArgumentError)
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/feature_file.rb:35:in `feature'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/feature_file.rb:35:in `parse'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime/features_loader.rb:28:in `load'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime/features_loader.rb:26:in `each'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime/features_loader.rb:26:in `load'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime/features_loader.rb:14:in `features'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime.rb:179:in `features'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/runtime.rb:32:in `run!'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/cli/main.rb:54:in `execute!'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/../lib/cucumber/cli/main.rb:29:in `execute'
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/cucumber:8
    /Users/mjul/src/github/mjul/cucumber-tutorial/lib/gems/gems/cucumber-0.9.0/bin/cucumber:19:in `load'
    lib/gems/bin/cucumber:19
    #<Status org.jruby.Main$Status@78fd5428>

The reason is that the libraries we use are built against a number of
different versions of the same libraries (the cucumber-0.9.0 gem is
not compatible with gherkin-2.1.4 which cuke4duke 0.3.2 is linked
to). If you look in the `lib/dev` folder after running `lein deps`
without the gherking override you will see this clearly:

    cucumber-tutorial mjul$ ls lib/dev/
    ant-1.6.5.jar						maven-ant-tasks-2.0.10.jar
    ant-1.8.1.jar						maven-artifact-2.0.10.jar
    ant-launcher-1.8.1.jar					maven-artifact-manager-2.0.10.jar
    clansi-1.0.0.jar						maven-error-diagnostics-2.0.10.jar
    classworlds-1.1-alpha-2.jar					maven-model-2.0.10.jar
    clj-stacktrace-0.1.2.jar					maven-plugin-registry-2.0.10.jar
    clojure-1.2.0-beta1.jar					maven-profile-2.0.10.jar
    clojure-contrib-1.2.0-RC3.jar				maven-project-2.0.10.jar
    cuke4duke-0.3.2.jar						maven-repository-metadata-2.0.10.jar
    difform-1.1.0.jar						maven-settings-2.0.10.jar
    gherkin-2.1.4.jar						plexus-container-default-1.0-alpha-9-stable-1.jar
    google-diff-match-patch-0.1.jar				plexus-interpolation-1.1.jar
    hooke-1.0.2.jar						plexus-utils-1.5.5.jar
    jline-0.9.94.jar						swank-clojure-1.3.0-20100821.141701-11.jar
    jruby-complete-1.5.1.jar					wagon-file-1.0-beta-2.jar
    jtidy-4aug2000r7-dev.jar					wagon-http-lightweight-1.0-beta-2.jar
    junit-3.8.1.jar						wagon-http-shared-1.0-beta-2.jar
    lein-cuke-0.3.2.jar						wagon-provider-api-1.0-beta-2.jar
    lein-difftest-1.3.1.jar					xml-apis-1.0.b2.jar
    leiningen-1.3.1.jar

Forcing gherkin to 2.2.4 is just enough to to reconcile cuke4duke
0.3.2, gherkin and the cucumber-0.9.0 Java gem and make it all run,
even if it leaves us with several versions of ant and
clojure. Cleaning that, however, is for another day.


## License

Copyright (C) 2010 Martin Jul (www.mjul.com)

Distributed under the MIT License. See the LICENSE file for details.

