# Cucumber Tutorial for Clojure

This project shows you how to set up and use Cucumber for Clojure.


## Usage

This is not an introduction to Cucumber. For that, please consult the following web sites:

* The main Cucumber site is [Cukes.info](http://cukes.info/).
* You can get the Cucumber Java libraries here [cuke4duke](http://wiki.github.com/aslakhellesoy/cuke4duke/)


## Installation

To get the dependencies to run the code in this tutorial do this:

    lein deps
    lein cuke-gems

Now, `lein cuke` will run the cucumber tests.

    lein cuke


### Setting Up a Project for Cucumber BDD

First, edit the leiningen project.clj file to include the Cucumber library and the
lein-cuke extension to leiningen. You should have something like this:


    (defproject cuketut "1.0.0-SNAPSHOT"
      :description "Setting up and using Cucumber with Clojure."
      :dependencies [[org.clojure/clojure "1.2.0"]
                     [org.clojure/clojure-contrib "1.2.0"]
		     ;; force the correct version of gherkin as it is not correctly specified in cuke4duke 0.3.2
                     [gherkin "2.2.4"]
                     [cuke4duke "0.3.2"]]
      :dev-dependencies [[org.clojars.mjul/lein-cuke "0.3.2"]])


Note that the cuke4duke dependencies are incorrect, so we have to
override with a newer gherkin library to match the one that is
actually needed to make it work.

Now install cuke: 

    lein deps
    lein cuke-gems

Create a feature folder and a folder for step definitions inside it in the project root:

    make -p features/step_definitions/
   
As you write your code, put the feature definitions in the features folder and the 
step definitions that link them to your code in the step_definitions subfolder.

To run the Cucumber tests from lein:

   lein cuke 



## License

Copyright (C) 2010 Martin Jul (www.mjul.com)

Distributed under the MIT License. See the LICENSE file for details.

