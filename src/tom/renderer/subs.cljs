(ns tom.renderer.subs
  (:require
   [re-frame.core :refer [reg-sub]]))


(reg-sub
 ::page-one-columns
 (fn [db]
   (:page-one-columns db)))


(reg-sub
 ::page-two-columns
 (fn [db]
   (:page-two-columns db)))


(reg-sub
 ::page-one
 (fn [db]
   (:page-one db)))


(reg-sub
  ::active
  (fn [db]
    (:active db)))


(reg-sub
  ::row
  (fn [db]
    (:row db)))


(reg-sub
  ::row-update
  (fn [db]
    (:row-update db)))


(reg-sub
  ::column
  (fn [db]
    (:column db)))


(reg-sub
  ::active-two-page
  (fn [db]
    (let [id (-> db :active :id keyword)]
      (-> db :page-two id))))
