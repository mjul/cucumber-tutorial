(ns cuketut.core)


(def trades (ref []))
(def positions (ref {}))
(def market (ref {}))

(defn clear-trades! []
  (ref-set trades []))

(defn get-trades []
  @trades)


(defn get-market [cross]
  {:pre (contains? @market cross)}
  (get @market cross))

(defn set-market! [cross bid ask]
  (alter market assoc cross {:bid bid, :ask ask}))


(defn clear-positions! []
  (ref-set positions {}))

(defn get-positions []
  @positions)

(defn make-position [instrument qty price]
  {:instrument instrument, :last-qty qty, :last-px price})

(defn set-position! [instrument qty price]
  (alter positions assoc instrument (make-position instrument qty price)))

(defn get-position [instrument]
  (get (get-positions ) instrument))


(defn make-trade [instrument qty price]
  {:instrument instrument, :last-qty qty, :last-px price})

(defn register-trade! [instrument qty price]
  (alter trades conj (make-trade instrument qty price)))

(defn buy! [cross qty]
  (dosync
   (let [price (:ask (get-market cross))]
     (register-trade! cross qty price)
     (set-position! cross qty price))))