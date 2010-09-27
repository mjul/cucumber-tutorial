(ns cuketut.test.core
  (:use [cuketut.core] :reload-all)
  (:use [clojure.test]))

(deftest market-test
  (testing "market"
    (dosync
     (set-market! "EURUSD" 1.34662 1.34714))
    (is (= {:bid 1.34662, :ask 1.34714} (get-market "EURUSD")))))

(deftest trading-test
  (testing "trade list manipulation"
    (dosync
     (clear-trades!))
    (is (empty? (get-trades)))
    (dosync
     (register-trade! "EURUSD" 1000000 1.34714))
    (let [trades (get-trades)]
      (is (= 1 (count trades)))
      (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} (first trades)))))
  (testing "buy!"
    (dosync
     (clear-trades!)
     (set-market! "EURUSD" 1.34662 1.34714)
     (buy! "EURUSD" 1000000))
    (let [trades (get-trades)
	  positions (get-positions)
	  eurusd-pos (get-position "EURUSD")]
      (is (= 1 (count trades)))
      (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} (first trades)))
      (is (= 1 (count positions)))
      (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} eurusd-pos)))))
  
      
    


