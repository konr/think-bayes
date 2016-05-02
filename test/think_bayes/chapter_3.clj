(ns think-bayes.chapter-3
  (:require  [midje.sweet :refer :all]
             [think-bayes.utils :refer [map-vals]]
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
                (with-precision 10))
               =>
               {:d4 0.00M
                :d6 0.00M
                :d8 0.943248453672M
                :d12 0.0552061280613M
                :d20 0.0015454182665M})))
