(defproject think-bayes "0.1.0-SNAPSHOT"
  :description "Think Bayes book code implemented in Clojure"
  :license {:name "GPL 3"}

  :plugins [[lein-midje "3.1.3"]
            [lein-cloverage "1.0.6"]
            [lein-vanity "0.2.0"]
            [s3-wagon-private "1.2.0"]
            [lein-ancient "0.6.7"]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.4"]
                 [prismatic/plumbing "0.5.0"]]

  :profiles {:dev {:source-paths ["config"]
                   :dependencies [[midje "1.7.0" :exclusions [org.clojure/clojure]]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [ns-tracker "0.3.0"]]}}

  )
