(ns named.args
  (require [clojure.string :as str]
           [clojure.set :refer [difference]]
           [clojure.core.match :refer [match]]))

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

(defn- params [args arguments]
  ['& {:keys args :as arguments}])

(defn- prepost-map [body]
  (let [candidate (first body)]
    (when (and (or (:pre candidate) (:post candidate))
               (next body))
      candidate)))

(defn- pre [args arguments body]
  (let [pre1 {:pre [(list 'named.args/check-arguments (set (map keyword args)) arguments)]}
        prepost-map (prepost-map body)]
    (if prepost-map
      (merge-with (comp vec concat) pre1 prepost-map)
      pre1)))

(defn- dobody [body]
  (if (prepost-map body)
    (rest body)
    body))

(defmacro defnam
  "Similar to defn but all args of the function defined are named and non-optional.
   Custom argument destructuring, variadic args & overloads are not supported."
  {:arglists '([name doc-string? attr-map? [params*] prepost-map? body])}
  [name & spec]
  (let [arguments (gensym "arguments")]
    (match (vec spec)
           [[& args] & body] `(defn ~name ~(params args arguments) ~(pre args arguments body) ~@(dobody body))
           [doc-string-or-attr-map [& args] & body] `(defn ~name ~doc-string-or-attr-map ~(params args arguments)
                                                       ~(pre args arguments body) ~@(dobody body))
           [doc-string attr-map [& args] & body] `(defn ~name ~doc-string ~attr-map ~(params args arguments)
                                                    ~(pre args arguments body) ~@(dobody body))
           )))
