(ns named.args-test
  (:require [clojure.test :refer :all]
            [named.args :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(defn foo-fixture [f]
  (defnam foo [bar1 bar2]
          arguments)
  (f)
  (ns-unmap 'named.args-test 'foo))

(use-fixtures :once foo-fixture)

(deftest defnam-test
  (is (thrown-with-msg? ExceptionInfo #"(?i)missing arguments: bar2" (foo :bar1 1)))
  (is (= {:bar1 1 :bar2 2} (foo :bar1 1 :bar2 2)))
  (is (thrown-with-msg? ExceptionInfo #"(?i)superfluous arguments: bar3" (foo :bar1 1 :bar2 2 :bar3 3)))
  )
