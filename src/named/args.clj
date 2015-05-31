(ns named.args
  (require [clojure.string :as str]
           [clojure.set :refer [difference]]))

(defn check-arguments [declared supplied]
  (let [suppl (set (keys supplied))
        missing (difference declared suppl)
        superfluous (difference suppl declared)
        error (fn [msg args] (throw
                               (ex-info
                                 (format msg
                                         (str/join ", " (map name args)))
                                 {:declared declared, :supplied supplied})))]
    (cond
      (not-empty missing) (error "Missing arguments: %s" missing)
      (not-empty superfluous) (error "Superfluous arguments: %s" superfluous)
      :else true)))

(defmacro defnam [name args body]
  `(defn ~name [& {:keys ~args
                   :as   ~'arguments}]
     {:pre [(check-arguments ~(set (map keyword args)) ~'arguments)]}
     ~body))

(comment
  (defnam foo [bar1 bar2] arguments)
  (foo :bar1 1 :bar2 2 :bar3 3)
  (foo :bar1 1 :bar2 2)
  (foo :bar1 1)
  (macroexpand-1 '(defnam foo [bar1] arguments))
  )
