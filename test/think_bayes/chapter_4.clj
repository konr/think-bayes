(ns think-bayes.chapter-4
  (:require  [midje.sweet :refer :all]
             [think-bayes.utils :refer [map-vals map-vals* mean p]]
             [think-bayes.prior :as prior]
             [think-bayes.beta :as beta]
             [think-bayes.core :as core]))

(facts "on the euro problem:

- You flip a coin N times
- You get H heads

Problem: Is the coin fair?

Strategy:
 - Calculate the probabilities of getting H heads
 - Compare metrics with unbiased coin's result, which would be 50%

"
       (with-precision 10
         (let [with-event (fn [prior event] (core/bayes-rule
                                             (fn [event] (map-vals* (fn [k _] (if (= event :heads) (/ k 100) (- 1 (/ k 100)))) prior))
                                             prior event))
               events (concat (repeat 140 :heads) (repeat 110 :tails))
               pmf (->>
                    events
                    (reduce with-event (prior/discrete 101)))]
           (fact "maximum likelyhood, mean and median"
                 (core/maximum-likelyhood pmf) => 56
                 (core/mean pmf)   => (roughly 55.95M 0.01)
                 (core/median pmf) => 56)

           (fact "credible interval. 50 is outside the credible interval"
                 [(core/percentile 0.05M pmf)
                  (core/percentile 0.95M pmf)] 
                 => [51 61])

           (fact "using triangle prior"
                 (let [triangle-pmf (->> events
                                         (reduce with-event (prior/triangle 101))
                                         core/normalize)]

                   (core/maximum-likelyhood triangle-pmf) => 56
                   (core/mean triangle-pmf) => (roughly 55.75M 0.01)
                   (core/median triangle-pmf) => 56))

           (fact "using beta distribution"
                   (-> beta/uniform-prior-beta
                       (beta/with-events 140M 110M)
                       beta/mean)
                   => (roughly 0.56M 0.01)))))

