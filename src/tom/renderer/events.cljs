(ns tom.renderer.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx]]
   [tom.renderer.db :as db]
   [tom.renderer.effects :as effects]))


(reg-event-fx
 ::initialize-db
 [(inject-cofx :settings)]
 (fn [{:keys [_ settings]}]
   {:db (merge db/default-db settings)}))


(reg-event-db
 ::set-data
 (fn [db [_ path value]]
   (let [path (if (vector? path) path [path])]
     (assoc-in db path value))))


(reg-event-db
 ::update-data
 (fn [db [_ key-seq f & args]]
   (let [key-seq (if (vector? key-seq) key-seq [key-seq])]
     (apply update-in (concat [db key-seq f] args)))))


(reg-event-fx
 ::save-settings-to-local
 (fn [{:keys [db]} _]
   {::effects/set-item-to-local! {:key "settings"
                                  :val (select-keys db [:page-one-columns :page-two-columns
                                                        :page-one :page-two])}}))


(reg-event-db
 ::reset
 (fn [db [_ k]]
   (dissoc db k)))


(reg-event-fx
 ::set-new-column
 (fn [{:keys [db]} [_ path]]
   {:db (update db path conj {:id (str (random-uuid))
                              :name (:column db)})
    :dispatch-n [[::save-settings-to-local]
                 [::reset :column]]}))


(reg-event-fx
  ::set-new-row
  (fn [{:keys [db]} [_ path]]
    (let [path (if (= path :page-one)
                 [:page-one]
                 [:page-two (-> db :active :id keyword)])]
      {:db (update-in db path conj (merge (:row db) {:id (str (random-uuid))}))
       :dispatch-n [[::save-settings-to-local]
                    [::reset :row]]})))


(reg-event-fx
  ::delete-row
  (fn [{:keys [db]} [_ path id]]
    (let [data-path (if (= path :page-one)
                      [:page-one]
                      [:page-two (-> db :active :id keyword)])
          db        (if (= path :page-one)
                      (update db :page-two dissoc (keyword id))
                      db)]
      {:db (assoc-in db data-path (filter #(not= (:id %) id) (get-in db data-path)))
       :dispatch [::save-settings-to-local]})))


(reg-event-fx
  ::remove-column
  (fn [{:keys [db]} [_ path id]]
    (let [data-path (if (= path :page-one-columns)
                      [:page-one]
                      [:page-two (-> db :active :id keyword)])]
      {:db (-> db
               (assoc path (filter #(not= (:id %) id) (path db)))
               (assoc-in data-path (map #(dissoc % (keyword id)) (get-in db data-path))))
       :dispatch [::save-settings-to-local]})))


(reg-event-fx
  ::update-row
  (fn [{:keys [db]} [_ path id]]
    (let [data-path (if (= path :page-one)
                      [:page-one]
                      [:page-two (-> db :active :id keyword)])
          rows      (:row-update db)]
      {:db (assoc-in db data-path (map #(if (= id (:id %))
                                         (merge % rows)
                                         %) (get-in db data-path)))
       :dispatch-n [[::reset :row-update]
                    [::save-settings-to-local]]})))

(reg-event-fx
  ::printer
  (fn [_ _]
    {::effects/printer! {}}))
