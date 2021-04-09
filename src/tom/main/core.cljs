(ns tom.main.core
  (:require
   ["electron" :refer [app BrowserWindow ipcMain]]))


(def main-window (atom nil))
(def backend-nrepl-port 3011)


(def ls (js/require "electron-localstorage"))


(defn- ^js/electron.BrowserWindow get-main-window []
  @main-window)


(defn init-browser []
  (let [index-html (str "file://" js/__dirname "/public/index.html")]
    (reset! main-window (BrowserWindow.
                         (clj->js {:width (max (or (.getItem ls "width") 800) 800)
                                   :height (max (or (.getItem ls "height") 600) 600)
                                   :webPreferences {:nodeIntegration true}})))
    ; Path is relative to the compiled js file (main.js in our case)
    (.loadURL (get-main-window) index-html)
    (.on (get-main-window) "close" #(.send (.-webContents (get-main-window)) "app-close"))
    (.on (get-main-window) "resize" (fn []
                                      (let [[w h] (js->clj (.getSize (get-main-window)))]
                                        (.setItem ls "width" w)
                                        (.setItem ls "height" h))))
    (.on ipcMain "closed" #(when-not (= js/process.platform "darwin")
                             (.quit app)))
    (.on ipcMain "url-change" #(.loadURL (get-main-window) index-html))))


(defn main []
  (.on app "ready" init-browser))
