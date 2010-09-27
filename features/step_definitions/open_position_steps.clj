(use 'cuketut.core)
(use 'clojure.test)

(After)

(Before
 (dosync
  (clear-trades! )
  (clear-positions! )))

(Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
       (fn [cross qty price]
	 (dosync 
	  (set-position! cross qty price))))

(Given #"^the market for (\w{6}) is at \[([\d.]+);([\d.]+)\]$"
       (fn [cross bid ask]
	 (dosync
	  (set-market! cross bid ask))))

(When #"^I submit an order to BUY (\d+) (\w{6}) at MKT$"
      (fn [qty cross]
	(dosync 
	 (buy! cross qty))))

(Then #"^a trade should be made at ([\d.]+)$"
      (fn [expected]
	(is (= expected (:last-px (last (get-trades)))))))

(Then #"^my position should show LONG (\d+) (\w{6}) at ([\d.]+)$"
       (fn [qty cross price]
 	(let [position (get-position cross)]
 	  (assert (= qty (:last-qty position)))
 	  (assert (= price (:last-px position))))))





