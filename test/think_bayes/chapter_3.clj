(ns think-bayes.chapter-3
  (:require  [midje.sweet :refer :all]
             [think-bayes.utils :refer [map-vals map-vals* mean]]
             [think-bayes.prior :as prior]
             [think-bayes.core :as core]))


(facts "on the dice problem:

- You have several n-sided dice
- You pick one of them, randomly
- You roll the dice and get X

Problem: What is the chance that you got each of the dice?"
       (let [prior-hypothesis {:d4 1M
                               :d6 1M
                               :d8 1M
                               :d12 1M
                               :d20 1M}
             max-for-each-dice {:d4 4M
                                :d6 6M
                                :d8 8M
                                :d12 12M
                                :d20 20M}
             with-event (fn [prior event] (core/bayes-rule
                                           (fn [event] (map-vals #(if (> event %) 0 (/ 1 %)) max-for-each-dice))
                                           prior event))]

         (fact "book example #1"
               (with-precision 10
                 (with-event
                   prior-hypothesis
                   6M))
                => {:d4 0.0M
                    :d6 0.3921568628M
                    :d8 0.2941176471M
                    :d12 0.1960784314M
                    :d20 0.1176470588M})

         (fact "book example #2"
               (->>
                [6M 8M 7M 7M 5M 4M]
                (reduce with-event prior-hypothesis)
                (with-precision 3))
               =>
               {:d4 0.00M
                :d6 0.00M
                :d8 0.916M
                :d12 0.0798M
                :d20 0.00377M})))

(facts "on the locomotive problem:

- A railroad numbers its locomotives in order 1..N.
- One day you see a locomotive with the number 60.

Problem: Estimate how many locomotives the railroad has."
       (let [prior-hypothesis (prior/discrete 1000M)
             with-event (fn [prior event]
                          (core/bayes-rule
                           (fn [event] (map-vals* (fn [x y] (if (> event x) 0 (/ 1N x))) prior))
                           prior event))]

         (fact "book example"
               (->>
                (with-event prior-hypothesis 60M)
                (with-precision 10)
                (apply max-key val))
                => [60 0.005905417880M])

         (fact "using distribution mean"
               (->>
                (with-event prior-hypothesis 60M)
                (with-precision 10)
                (map (fn [[hyp prob]] (* hyp prob)))
                (apply +))
               => (roughly 333M 1))

         (tabular 
          (fact "different prior upper bound"
                (->>
                  (with-event
                    (prior/discrete ?prior-upper-bound)
                    60M)
                 (with-precision 10)
                 (map (fn [[hyp prob]] (* hyp prob)))
                 (apply +))
                => (roughly ?posterior-mean 1))
          ?prior-upper-bound ?posterior-mean
          500M               207M
          1000M              333M
          2000M              552M)

         (tabular 
          (fact "more observations, different prior upper bound"
                (->>
                 (-> (prior/discrete ?prior-upper-bound)
                     (with-event 60M)
                     (with-event 30M)
                     (with-event 90M))
                 (with-precision 10)
                 (map (fn [[hyp prob]] (* hyp prob)))
                 (apply +))
                => (roughly ?posterior-mean 1))
          ?prior-upper-bound ?posterior-mean
          500M               152M
          1000M              164M
          2000M              171M)

         (tabular 
          (fact "using power law prior"
                (->>
                 (-> (prior/discrete-power-law ?prior-upper-bound 1)
                     (with-event 60M)
                     (with-event 30M)
                     (with-event 90M))
                 (with-precision 10)
                 (map (fn [[hyp prob]] (* hyp prob)))
                 (apply +))
                => (roughly ?posterior-mean 1))
          ?prior-upper-bound ?posterior-mean
          500M               130M
          1000M              133M
          2000M              134M
          4000M              134M
          8000M              134M)))
