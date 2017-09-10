(ns duckling.dims.time.api
  (:require [duckling.dims.time.pred :as pred]
            [clj-time.coerce :as c])
  (:refer-clojure :exclude [resolve]))

(defn resolve
  "Given a token presumably produced by this module, returns a list of tokens
  with a resolved value. For time in particular, predicates are turned into
  actual time instants or intervals.
  It returns a list of tokens instead of just one, because several values may
  be possible.
  If the token cannot be resolved, :value must be removed."
  [token context]
  (take 1 (pred/resolve token context)))

