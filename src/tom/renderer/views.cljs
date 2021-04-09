(ns tom.renderer.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [tom.renderer.subs :as subs]
   [tom.renderer.events :as events]
   [reagent.core :as r]))


(defn- input-field [path value]
  [:input.app-input
   {:value     value
    :on-change #(dispatch [::events/set-data path (-> % .-target .-value)])}])


(defn- column-list-view [class items key]
  [:div.column-list
   {:class class}
   (for [item items]
     ^{:key (str "page-column-list-" (:id item))}
     [:div.column-item
      [:div (:name item)]
      [:div.delete
       {:on-click #(dispatch [::events/remove-column key (:id item)])}
       "X"]])])


(defn- settings-view []
  [:div.settings
   [input-field [:column] @(subscribe [::subs/column])]
   [:div.button-box
    [:button.one
     {:on-click #(dispatch [::events/set-new-column :page-one-columns])}
     "Add to page 1"]
    [:button.two
     {:on-click #(dispatch [::events/set-new-column :page-two-columns])}
     "Add to page 2"]]
   [:div.column-list-box
    [column-list-view "left" @(subscribe [::subs/page-one-columns]) :page-one-columns]
    [column-list-view "right" @(subscribe [::subs/page-two-columns]) :page-two-columns]]])


(defn- page-add [columns path]
 (let [row @(subscribe [::subs/row])]
  [:tr
   (for [header columns
         :let [data-key (-> header :id keyword)]]
     ^{:key (str "page-add-" data-key)}
     [:td
      [input-field [:row data-key] (data-key row)]])
   [:td.add-action
    {:on-click #(dispatch [::events/set-new-row path])}
    "Add"]]))


(defn- list-header-view [columns]
  [:thead
   [:tr
    (for [header (conj columns {:name "Action"})]
      ^{:key (str "list-header-view-" (:id header))}
      [:th (:name header)])]])


(defn- action-row-view [data path update?]
  [:td
   {:on-click #(.stopPropagation %)}
   [:div
    [:span.delete-action
     {:on-click #(dispatch [::events/delete-row path (:id data)])}
     "Del"]
    [:span.update-action
     {:on-click #(do (when @update?
                      (dispatch [::events/update-row path (:id data)]))
                     (swap! update? not))}
     "Up"]
    [:span.select-action
     {:on-click #(dispatch [::events/set-data [:active] {:panel :page-two
                                                         :id (:id data)}])}
     "Se"]]])


(defn- list-content-view [data columns]
  [:<>
    (for [header columns
          :let [data-key (-> header :id keyword)]]
      ^{:key (str "list-content-view-" data-key)}
      [:td (data-key data)])])


(defn- update-field-view [data columns]
  (r/create-class
    {:component-did-mount #(doseq [header columns
                                   :let [data-key (-> header :id keyword)]]
                             (dispatch [::events/set-data [:row-update data-key] (data-key data)]))
     :reagent-render (fn []
                      (let [row @(subscribe [::subs/row-update])]
                        [:<>
                         (for [header columns
                               :let [data-key (-> header :id keyword)]]
                           ^{:key (str "update-field-view-" data-key)}
                           [:td
                            [input-field [:row-update data-key] (data-key row)]])]))}))


(defn- page-one-content []
  (let [update? (r/atom false)]
    (fn [data columns path]
      [:tr
       (if @update?
         [update-field-view data columns]
         [list-content-view data columns])
       [action-row-view data path update?]])))


(defn- page-one-view []
  (let [columns @(subscribe [::subs/page-one-columns])]
    [:table#app-table.app-table
     [list-header-view columns]
     [:tbody
      (for [data @(subscribe [::subs/page-one])]
        ^{:key (str "page-one-" (:id data))}
        [page-one-content data columns :page-one])
      [page-add columns :page-one]]]))


(defn- page-two-view []
  (let [columns @(subscribe [::subs/page-two-columns])]
    [:table#app-table.app-table
     [list-header-view columns]
     [:tbody
      (for [data @(subscribe [::subs/active-two-page])]
        ^{:key (str "page-two-" (:id data))}
        [page-one-content data columns :page-two])
      [page-add columns :page-two]]]))


(defn- bar-view []
  [:nav.app-nav
   [:ul
    [:li
     {:on-click #(dispatch [::events/set-data [:active :panel] :settings])}
     "Settings"]
    [:li
     {:on-click #(dispatch [::events/set-data [:active :panel] :page-one])}
     "List"]
    [:li
     {:on-click #(dispatch [::events/printer])}
     "Print"]]])


(defn- main-view []
  (let [active @(subscribe [::subs/active])]
    (case (:panel active)
      :settings [settings-view]
      :page-two [page-two-view]
      [page-one-view])))


(defn- body []
  [:div
   [bar-view]
   [main-view]])


(defn main-panel []
  (r/create-class
    {:reagent-render (fn [] [body])}))
