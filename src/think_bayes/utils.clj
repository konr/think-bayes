(ns think-bayes.utils)

(defn map-vals [f m]
  (zipmap (keys m) (map f (vals m))))

(defn map-vals* [f m]
  (zipmap (keys m) (map (partial apply f) m)))

(defn mean [list]
  (/ (apply + list) (count list)))

(defn find-first [f s]
  (first (filter f s)))

(def p partial)
