(ns think-bayes.utils)

(defn map-vals [f m]
  (zipmap (keys m) (map f (vals m))))
