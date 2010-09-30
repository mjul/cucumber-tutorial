(defproject cuketut "1.0.0-SNAPSHOT"
  :description "Setting up and using Cucumber with Clojure."
  :repositories {"cukes" "http://cukes.info/maven",
		 "codehaus" "http://repository.codehaus.org"}
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 ;; force the correct version of gherkin as it is not correctly specified in cuke4duke 0.3.2
                 [gherkin "2.2.4"]
                 [cuke4duke "0.3.2"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]
                     [lein-difftest "1.3.1"]
                     ;; Use this lein-cuke to get dependencies right:
                     [org.clojars.mjul/lein-cuke "0.3.2"]
                     ;; Note: The following does not work since it depends on Clojure 1.1.0-alpha:
                     ;; [lein-cuke "0.0.1-SNAPSHOT"]
                     ]
  :hooks [leiningen.hooks.difftest])

