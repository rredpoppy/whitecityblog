(ns whitecity.settings 
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]))

(defonce settings (atom {}))

(defn read-settings! [filename]
  (timbre/info "Settings:" (json/read-str (slurp filename)))
  (reset! settings (json/read-str (slurp filename))))