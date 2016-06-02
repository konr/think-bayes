(ns think-bayes.prior
  (:require [think-bayes.utils :refer [map-vals]]))

(defn discrete
  ([to] (discrete 0 to))
  ([from to]
   (->> (range from to)
        (map #(hash-map % 1M))
        (reduce merge))))

(defn discrete-power-law
  ([to] (discrete-power-law 0 to))
  ([from to] (discrete-power-law from to 1))
  ([from to alpha]
   (->> (range from to)
        (map #(hash-map % (Math/pow % (- alpha))))
        (reduce merge))))

(defn triangle
  [to]
  (->>
   (range to)
   (map #(hash-map % (if (< (* 2 %) to) % (- to %))))
   (reduce merge)
   (map-vals bigdec)))
