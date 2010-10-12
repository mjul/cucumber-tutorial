(use 'cuketut.core)
(use 'clojure.test)

(After)

(Before
 (dosync
  (clear-trades! )
  (clear-positions! )))

;; Given should return a function of the parameters extracted by the regex.

;; You can inline the function as an anonymous function,
;; which is probably the most readable way to do it:
(comment
  
  (Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
	 (fn [cross qty price]
	   (dosync 
	    (set-position! cross qty price))))

)


;; To share these functions between step definitions
;; you can define them as normal functions:

(defn my-initial-position-is [cross qty price]
  (dosync 
   (set-position! cross qty price)))

;; Step definition for English:
(Given #"^that my position in (\w{6}) is (\d+) at ([\d.]+)$"
       my-initial-position-is)

;; Same for Danish:
(Given #"^at min position i (\w{6}) er (\d+) købt til kurs ([\d,]+)$"
       my-initial-position-is)


;; To share steps between languages we use the defn way below.

(defn the-market-is-at [cross bid ask]
  (dosync
   (set-market! cross bid ask)))

(Given #"^the market for (\w{6}) is at \[([\d.]+);([\d.]+)\]$"
       the-market-is-at)

(Given #"^markedsprisen for (\w{6}) er \[([\d,]+);([\d,]+)\]$"
       the-market-is-at)


(defn i-submit-an-order-to-buy-at-market [qty cross]
  (dosync 
   (buy! cross qty)))

(When #"^I submit an order to BUY (\d+) (\w{6}) at MKT$"
      i-submit-an-order-to-buy-at-market)

(When #"^jeg afgiver en ordre om at KØBE (\d+) (\w{6}) til MARKEDSPRIS$"
      i-submit-an-order-to-buy-at-market)


(defn a-trade-should-be-made-at [expected]
  (is (= expected (:last-px (last (get-trades))))))

(Then #"^a trade should be made at ([\d.]+)$"
      a-trade-should-be-made-at)

(Then #"^skal en handel ske til kurs ([\d,]+)$"
      a-trade-should-be-made-at)


(defn my-position-should-show-long [qty cross price]
  (let [position (get-position cross)]
    (assert (= qty (:last-qty position)))
    (assert (= price (:last-px position)))))

(Then #"^my position should show LONG (\d+) (\w{6}) at ([\d.]+)$"
      my-position-should-show-long)

(Then #"^og min position skal være LANG (\d+) (\w{6}) købt til kurs ([\d,]+)$"
      my-position-should-show-long)
