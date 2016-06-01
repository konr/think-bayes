(ns think-bayes.prior)

(defn discrete [upto]
  (->> (range upto)
       (map inc)
       (map #(hash-map % 1M))
       (reduce merge)))

(defn discrete-power-law [upto alpha]
  (->> (range upto)
       (map inc)
       (map #(hash-map % (Math/pow % (- alpha))))
       (reduce merge)))
