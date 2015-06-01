# named-args

A Clojure library designed to help with non-optional named function arguments.
Internally it's using Clojure keyword arguments and a precondition.

## Usage

    (require '[named.args :refer [defnam defnam-]])

    (defnam start-washing-machine
      "Starts washing process."
      {:example true}
      [clothes temperature]
      {:post [(.contains % "washing")]}
      (str "washing " clothes " at " temperature))
    => #'user/start-washing-machine

    (start-washing-machine :clothes "t-shirts" :temperature 45)
    => washing t-shirts at 45

    (start-washing-machine :clothes "t-shirts")
    ExceptionInfo Missing arguments: temperature  clojure.core/ex-info (core.clj:4403)

    (try
      (start-washing-machine :clothes "t-shirts" :temperature 45 :laundry-powder :ajax)
      (catch Exception e [(.getMessage e) (ex-data e)]))
    => ["Superfluous arguments: laundry-powder" {:declared #{:clothes :temperature},
        :supplied {:laundry-powder :ajax, :clothes "t-shirts", :temperature 45}}]

## License

Copyright © 2015 Paweł Stroiński

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
