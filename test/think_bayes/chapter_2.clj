(ns think-bayes.chapter-2
  (:require  [midje.sweet :refer :all]
             [think-bayes.utils :refer [map-vals]]
             [think-bayes.core :as core]))

(facts "on the cookie exercise:

- there are two cookie bowls
- both have vanilla and chocolate cookies in them, in different proportions
- some one picked up some cookies from them
Problem: can you tell which bowl did they choose?"
       (let [prior-hypothesis {:bowl-1 0.5M :bowl-2 0.5M}
             cookies-distribution {:bowl-1 {:chocolate 0.25M :vanilla 0.75M}
                                   :bowl-2 {:chocolate 0.5M :vanilla 0.5M}}
             with-event (fn [event prior] (core/bayes-rule #(map-vals % cookies-distribution) prior event))]

          (fact "just vanilla"
                (->> prior-hypothesis
                     (with-event :vanilla))
                => {:bowl-1 0.6M :bowl-2 0.4M})

          (fact "longer time"
                (->> prior-hypothesis
                     (with-event :vanilla)
                     (with-event :chocolate)
                     (with-event :chocolate)
                     (with-event :vanilla)
                     (with-precision 2))
                => {:bowl-1 0.36M :bowl-2 0.64M})))
