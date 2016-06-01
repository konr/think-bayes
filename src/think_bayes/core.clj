(ns think-bayes.core
  (:require [schema.core :as s]
            [think-bayes.utils :refer [map-vals]]))

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
  [likelyhood-function prior event]
  (->> (likelyhood-function event)
       (mult prior)
       normalize))
