(ns named.args-test
  (:require [clojure.test :refer :all]
            [named.args :refer [defnam defnam-]])
  (:import (clojure.lang ExceptionInfo)))

(defn check-doc-string [foo]
  (is (= "foodoc" (:doc (meta foo)))))

(defn check-attr-map [foo]
  (is (= "foometa" (:foometa (meta foo)))))

(defn check-pre-in-prepost-map [foo]
  (is (thrown-with-msg? AssertionError #"10" (foo :bar1 10 :bar2 0))))

(defn check-post-in-prepost-map [foo]
  (is (thrown-with-msg? AssertionError #"20" (foo :bar1 0 :bar2 20))))

(defn check-not-prepost-map [foo]
  (is (= {:pre [false] :post [false]} (foo :bar1 1 :bar2 2))))

(defn check-private [foo]
  (is (:private (meta foo))))

(defn foo-fixture [f]
  (defnam foo [bar1 bar2]
          (+ bar1 bar2))
  (f)

  (defnam foo [bar1 bar2]
          (let [])
          (+ bar1 bar2))
  (f)

  (defnam foo
          "foodoc"
          [bar1 bar2]
          (let [])
          (+ bar1 bar2))
  (f)
  (check-doc-string #'foo)

  (defnam foo
          {:foometa "foometa"}
          [bar1 bar2]
          (let [])
          (+ bar1 bar2))
  (f)
  (check-attr-map #'foo)

  (defnam foo
          "foodoc"
          {:foometa "foometa"}
          [bar1 bar2]
          (let [])
          (+ bar1 bar2))
  (f)
  (check-doc-string #'foo)
  (check-attr-map #'foo)

  (defnam foo [bar1 bar2]
          {:pre [(< bar1 10)]}
          (let [])
          (+ bar1 bar2))
  (f)
  (check-pre-in-prepost-map foo)

  (defnam foo
          "foodoc"
          {:foometa "foometa"}
          [bar1 bar2]
          {:post [(< % 20)]}
          (let [])
          (+ bar1 bar2))
  (f)
  (check-doc-string #'foo)
  (check-attr-map #'foo)
  (check-post-in-prepost-map foo)

  (defnam foo [bar1 bar2]
          {:pre [false] :post [false]})
  (check-not-prepost-map foo)

  (defnam- foo
           "foodoc"
           [bar1 bar2]
           {:pre [(< bar1 10)] :post [(< % 20)]}
           (+ bar1 bar2))
  (f)
  (check-doc-string #'foo)
  (check-pre-in-prepost-map foo)
  (check-post-in-prepost-map foo)
  (check-private #'foo)

  (ns-unmap 'named.args-test 'foo)
  )

(use-fixtures :once foo-fixture)

(deftest defnam-test
  (is (thrown-with-msg? ExceptionInfo #"(?i)missing arguments: bar2" (foo :bar1 1)))
  (is (= 3 (foo :bar1 1 :bar2 2)))
  (is (thrown-with-msg? ExceptionInfo #"(?i)superfluous arguments: bar3" (foo :bar1 1 :bar2 2 :bar3 3)))
  )
