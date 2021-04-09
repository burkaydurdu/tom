(ns tom.renderer.util)


(defn set-item! [key val]
  (try
    (.setItem (.-localStorage js/window)
              key
              (.stringify js/JSON (clj->js val)))
    (catch js/Error e
      (println e))))


(defn remove-item! [key]
  (try
    (.removeItem (.-localStorage js/window) key)
    (catch js/Error e
      (println e))))


(defn get-item! [key]
  (.getItem js/localStorage key))


(defn settings
  "This function gets current settings from local storage"
  []
  (try
    (into (sorted-map)
          (as-> (get-item! "settings") data
            (.parse js/JSON data)
            (js->clj data :keywordize-keys true)))
    (catch js/Error _
      {})))
