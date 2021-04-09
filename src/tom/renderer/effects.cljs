(ns tom.renderer.effects
  (:require
   [tom.renderer.util :as util]
   [re-frame.core :refer [reg-fx]]))


(reg-fx
 ::set-item-to-local!
 (fn [{:keys [key val]}]
   (util/set-item! key val)))


(reg-fx
 ::remove-item-from-local!
 (fn [key]
   (util/remove-item! key)))


(reg-fx
  ::printer!
  (fn []
     (.print js/window)))
