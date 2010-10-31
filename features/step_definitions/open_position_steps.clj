(use 'cuketut.core)
(use 'clojure.test)

(After)

(Before
 (dosync
  (clear-trades! )
  (clear-positions! )
  (clear-open-orders! )))

;; Given should return a function of the parameters extracted by the regex.

;; You can inline the function as an anonymous function,
;; which is probably the most readable way to do it:
(comment
  
  (Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
	 (fn [cross qty price]
	   (dosync 
	    (set-position! cross qty price))))

)

;; Some helpers for coercing string arguments to numbers
(defn to-int [s]
  (Integer. s))

(defn to-float [s]
  (let [decimal-point (apply str (interpose "." (.split s ",")))]
    (Float. decimal-point)))

;; To share these functions between step definitions
;; you can define them as normal functions:

(defn my-initial-position-is [cross qty price]
  (dosync 
   (set-position! cross (to-int qty) (to-float price))))

;; Step definition for English:
(Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
       my-initial-position-is)

;; Same for Danish:
(Given #"^at min position i (\w{6}) er (\d+) købt til kurs ([\d,]+)$"
       my-initial-position-is)


;; To share steps between languages we use the defn way below.

(defn the-market-is-at [cross bid ask]
  (dosync
   (set-market! cross (to-float bid) (to-float ask))))

(Given #"^the market for (\w{6}) is at \[([\d.]+);([\d.]+)\]$"
       the-market-is-at)

(Given #"^markedsprisen for (\w{6}) er \[([\d,]+);([\d,]+)\]$"
       the-market-is-at)


(defn i-submit-an-order-to-buy-at-market [qty cross]
  (dosync 
   (buy! cross (to-int qty))))

(When #"^I submit an order to BUY (\d+) (\w{6}) at MKT$"
      i-submit-an-order-to-buy-at-market)

(When #"^jeg afgiver en ordre om at KØBE (\d+) (\w{6}) til MARKEDSPRIS$"
      i-submit-an-order-to-buy-at-market)


(defn i-submit-an-order-to-sell-at-market [qty cross]
  (dosync 
   (sell! cross (to-int qty))))

(When #"^I submit an order to SELL (\d+) (\w{6}) at MKT$"
      i-submit-an-order-to-sell-at-market)


(defn a-trade-should-be-made-at [expected]
  (is (= (to-float expected) (:last-px (last (get-trades))))))

(Then #"^a trade should be made at ([\d.]+)$"
      a-trade-should-be-made-at)

(Then #"^skal en handel ske til kurs ([\d,]+)$"
      a-trade-should-be-made-at)


(defn my-position-should-show-long [qty cross price]
  (let [position (get-position cross)]
    (assert (= (to-int qty) (:last-qty position)))
    (assert (= (to-float price) (:last-px position)))))

(Then #"^my position should show LONG (\d+) (\w{6}) at ([\d.]+)$"
      my-position-should-show-long)

(Then #"^min position skal være LANG (\d+) (\w{6}) købt til kurs ([\d,]+)$"
      my-position-should-show-long)


(defn my-position-should-show-short [qty cross price]
  (let [position (get-position cross)]
    (assert (= (- (to-int qty)) (:last-qty position)))
    (assert (= (to-float price) (:last-px position)))))

(Then #"^my position should show SHORT (\d+) (\w{6}) at ([\d.]+)$"
      my-position-should-show-short)

(Then #"^min position skal være KORT (\d+) (\w{6}) solgt til kurs ([\d,]+)$"
      my-position-should-show-short)

(Given #"^I have no open orders in (\w{6})$"
       (fn [cross]
	 (dosync
	  remove-open-orders! #(= :currency cross))))

(When #"^I submit an order to BUY (\d+) (\w{6}) at MKT with TARGET ([\d.]+) and STOP ([\d.]+)$"
      (fn [qty cross target stop]
	(dosync
	 (buy! cross (to-int qty) (to-float target) (to-float stop)))))

;;
;; If you put tables in the feature specification, they will be passed
;; as a Table object. Use the hashes function below to extract a sequence
;; of the data rows.
;;

(defn hashes [table]
  "Get the data from a Cucumber Table as a list of maps.
   The result is a sequence of maps for each non-header row of the
   table. Each map contains the values of each column in the row keyed
   by the corresponding column names taken from the header row."
  (map #(into {} %) (.hashes table)))


(Then #"^my open orders should contain these OCO-orders$"
      (fn [^cuke4duke.Table table]
	;; TODO: this feature is currently not working in Cucumber for Clojure:
	(doseq [row (hashes table)]
	  (let [{side "Side", quantity "Quantity", type "Type", price "Price"} row]
	    (println "Row: " row)))
	(is (= 1 2))))
