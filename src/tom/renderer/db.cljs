(ns tom.renderer.db
  (:require
   [tom.renderer.util :as util]
   [re-frame.core :refer [reg-cofx]]))


(def default-db
  {:page-one-columns []
   :page-two-columns []
   :page-one '()
   :page-two {}
   :active {:panel :page-one}})


(reg-cofx
 :settings
 (fn [cofx _]
   (assoc cofx :settings (util/settings))))
