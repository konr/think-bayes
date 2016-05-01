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

(facts "on the Sergio Mallandro problem:

- there are three doors
- two of them have a bode inside
- one of them has a card
- you pick door a (for simplicity)
- Sergio Mallandro opens another, shows a bode, and asks if you want to change the door
Problem: does it matter which door to choose?"
       (let [prior-hypothesis {:car-in-a 1/3
                               :car-in-b 1/3
                               :car-in-c 1/3}
             chance-of-mallandro-picking-given-door {:door-a :not-applicable
                                                     :door-b {:car-in-a 1/2 ;; he would've picked at random
                                                              :car-in-b 0 ;; he wouldn't open the door with the car
                                                              :car-in-c 1} ;; he would've had to pick the other one
                                                     :door-c {:car-in-a 1/2
                                                              :car-in-b 1  ;; like above
                                                              :car-in-c 0}}
             with-event (fn [event prior] (core/bayes-rule chance-of-mallandro-picking-given-door prior event))]

          (fact "sergio opens b"
                (->> prior-hypothesis
                     (with-event :door-b))
                => {:car-in-a 1/3
                    :car-in-b 0
                    :car-in-c 2/3})

          (fact "sergio opens c"
                (->> prior-hypothesis
                     (with-event :door-c))
                => {:car-in-a 1/3
                    :car-in-b 2/3
                    :car-in-c 0})

          (fact "sergio opens b, then c"
                (->> prior-hypothesis
                     (with-event :door-b)
                     (with-event :door-c))
                => {:car-in-a 1
                    :car-in-b 0
                    :car-in-c 0}))) 


(facts "on the M&Ms problem:

- The proportion of colors of M&Ms in any given bag is given by its factory
- In 1994 and 1996 there were different proportions

Problem: If you start picking M&Ms from two different and unlabeled bags, can you tell which is from 1994 and which is from 1996?"
       (let [prior-hypothesis {:bag-1-is-1994 1/2
                               :bag-2-is-1994 1/2}
             bags {:from-1996 {:blue 24
                               :green 20
                               :orange 16
                               :yellow 14
                               :red 13
                               :brown 13}
                   :from-1994 {:brown 30
                               :yellow 20
                               :red 20
                               :green 10
                               :orange 10
                               :tan 10}}
             hypotheses {:bag-1-is-1994 {:bag-1 (:from-1994 bags) :bag-2 (:from-1996 bags)}
                         :bag-2-is-1994 {:bag-1 (:from-1996 bags) :bag-2 (:from-1994 bags)}}
             with-event (fn [event prior] (core/bayes-rule
                                           (fn [[bag color]] (map-vals #(get-in % event) hypotheses ))
                                           prior event))]

         (fact "book example"
               (->> prior-hypothesis
                    (with-event [:bag-1 :yellow])
                    (with-event [:bag-2 :green]))
               => {:bag-1-is-1994 20/27
                   :bag-2-is-1994 7/27})))
