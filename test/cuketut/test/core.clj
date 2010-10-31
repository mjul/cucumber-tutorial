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
    (testing "at market"
      (dosync
       (clear-trades!)
       (set-market! "EURUSD" 1.34662 1.34714)
       (buy! "EURUSD" 1000000)
       (let [trades (get-trades)
	     positions (get-positions)
	     eurusd-pos (get-position "EURUSD")]
	 (is (= 1 (count trades)))
	 (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} (first trades)))
	 (is (= 1 (count positions)))
	 (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} eurusd-pos)))))
    (testing "with limit and stop"
      (dosync
       (clear-trades!)
       (clear-open-orders!)
       (set-market! "EURUSD" 1.34662 1.34714)
       (buy! "EURUSD" 1000000 1.4000 1.3000)
       (let [trades (get-trades)
	     positions (get-positions)
	     eurusd-pos (get-position "EURUSD")
	     eurusd-orders (filter-open-orders #(= (:instrument %) "EURUSD"))] 
	 (is (= 1 (count trades)))
	 (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} (first trades)))
	 (is (= 1 (count positions)))
	 (is (= {:instrument "EURUSD", :last-qty 1000000, :last-px 1.34714} eurusd-pos))
	 (is (= 2 (count eurusd-orders)))
	 (let [limit (first (filter #(= :limit (:type %)) eurusd-orders))
	       stop (first (filter #(= :stop (:type %)) eurusd-orders))]
	   (is limit)
	   (is (= {:side :sell, :qty 1000000, :instrument "EURUSD", :type :limit :px 1.4000}
		  (select-keys limit [:side :qty :instrument :px :type])))
	   (is stop)
	   (is (= {:side :sell, :qty 1000000, :instrument "EURUSD", :type :stop :px 1.3000}
		  (select-keys stop [:side :qty :instrument :px :type]))))))))
  (testing "sell!"
    (testing "at market"
      (dosync
       (clear-trades!)
       (set-market! "EURUSD" 1.34662 1.34714)
       (sell! "EURUSD" 1000000)
       (let [trades (get-trades)
	     positions (get-positions)
	     eurusd-pos (get-position "EURUSD")]
	 (is (= 1 (count trades)))
	 (is (= {:instrument "EURUSD", :last-qty -1000000, :last-px 1.34662} (first trades)))
	 (is (= 1 (count positions)))
	 (is (= {:instrument "EURUSD", :last-qty -1000000, :last-px 1.34662} eurusd-pos)))))))


  
  
      
    


