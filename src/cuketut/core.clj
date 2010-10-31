(ns cuketut.core)

(def trades (ref []))
(def positions (ref {}))
(def market (ref {}))
(def open-orders (ref []))

;; ----------------------------------------------------------------
;; Market
;; ----------------------------------------------------------------

(defn get-market [cross]
  {:pre (contains? @market cross)}
  (get @market cross))

(defn set-market! [cross bid ask]
  (alter market assoc cross {:bid bid, :ask ask}))

;; ----------------------------------------------------------------
;; Positions
;; ----------------------------------------------------------------

(defn clear-positions! []
  (ref-set positions {}))

(defn get-positions []
  @positions)

(defn get-position [instrument]
  (get (get-positions ) instrument))

(defn make-position [instrument qty price]
  {:instrument instrument, :last-qty qty, :last-px price})

(defn set-position! [instrument qty price]
  (alter positions assoc instrument (make-position instrument qty price)))

;; ----------------------------------------------------------------
;; Open orders
;; ----------------------------------------------------------------

(defn clear-open-orders! []
  (ref-set open-orders []))

(defn remove-open-orders! [pred]
  (alter open-orders (remove pred open-orders)))

(defn filter-open-orders [pred]
  (filter pred @open-orders))

(defn create-order [side qty instrument type price]
  {:id (java.util.UUID/randomUUID) :side side, :qty qty, :type type, :instrument instrument, :px price})

(defn make-oco [& orders]
  (let [ids (map :id orders)]
    map #(assoc % :oco-with ids) orders))

(defn submit-orders! [orders]
  (alter open-orders #(apply conj % orders)))
		      
(defn submit-oco-orders! [a b]
  (submit-orders! (make-oco a b)))

;; ----------------------------------------------------------------
;; Trades
;; ----------------------------------------------------------------

(defn clear-trades! []
  (ref-set trades []))

(defn get-trades []
  @trades)

(defn make-trade [instrument qty price]
  {:instrument instrument, :last-qty qty, :last-px price})

(defn register-trade! [instrument qty price]
  (alter trades conj (make-trade instrument qty price)))

(defn- trade! [cross qty price]
  (dosync
   (register-trade! cross qty price)
   (set-position! cross qty price)))

(defn buy!
  ([cross qty]
     (let [price (:ask (get-market cross))]
       (trade! cross qty price)))
  
  ([cross qty target stop]
     (let [take-profit (create-order :sell qty cross :limit target)
	   stop-loss (create-order :sell qty cross :stop stop)]  
       (dosync
	(buy! cross qty)
	(submit-oco-orders! take-profit stop-loss)))))
  
(defn sell! [cross qty]
  (let [price (:bid (get-market cross))]
    (trade! cross (- qty) price)))
