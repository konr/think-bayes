(ns think-bayes.beta)

(def uniform-prior-beta {:α 1 :β 1})

(defn with-events
  [beta successes failures]
  (-> beta
      (update :α + successes)
      (update :β + failures)))

(defn mean [{:keys [α β]}]
  (/ α (+ α β)))
