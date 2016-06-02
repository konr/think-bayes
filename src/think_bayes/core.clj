(ns think-bayes.core
  (:require [schema.core :as s]
            [think-bayes.utils :refer [map-vals find-first]]))

(def PmfType {s/Any BigDecimal})

(s/defn normalize
  "Probability mass function"
  [pmf :- PmfType]
  (let [total (apply + (vals pmf))]
    (map-vals #(/ % total) pmf)))

(s/defn mult
  "Updates PMF"
  [pmf-1 :- PmfType, pmf-2 :- PmfType]
  (merge-with * pmf-1 pmf-2))


(s/defn bayes-rule
  "Updates, given a likelyhood function"
  [likelyhood-function :- s/fn-schema
   prior :- PmfType
   event :- s/Any]
  (->> (likelyhood-function event)
       (mult prior)
       normalize))

(s/defn percentile
  [threshold :- BigDecimal pmf :- PmfType]
  (->>
   pmf
   (sort-by first)
   (reductions (fn [[_ prob-acc] [val prob]] [val (+ prob-acc prob)]))
   (find-first (fn [[val prob]] (< threshold prob)))
   first))

(s/defn maximum-likelyhood
  [pmf :- PmfType]
  (key (apply max-key second pmf)))

(s/defn mean
  [pmf :- PmfType]
  (->> pmf
       (map (fn [[hyp prob]] (* hyp prob)))
       (apply +)))

(s/defn median
  [pmf :- PmfType]
  (percentile 0.5M pmf))
