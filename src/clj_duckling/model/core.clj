(ns clj-duckling.model.core)

(defprotocol Model
  (load-model! [this] "Load the model")
  (train-model! [this] "Train the model")
  (save-model! [this] "Save the model")
  (get-model [this] "Get the model")
  (get-id [this] "Get the model id")
  (set-logger! [this newlogger] "Set a new logger"))
