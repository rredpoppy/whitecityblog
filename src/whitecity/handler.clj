(ns whitecity.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [whitecity.routes.services :refer [service-routes cors-mw]]
            [whitecity.middleware :as middleware]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     (if (env :dev) :trace :info)
     :appenders {:rotor (rotor/rotor-appender
                          {:path "whitecity.log"
                           :max-size (* 512 1024)
                           :backlog 10})}})

  (timbre/info (str
                 "\n-=[whitecity started successfully"
                 (when (env :dev) " using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "whitecity is shutting down...")
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
    (cors-mw (var service-routes))
    (route/not-found "Not found")))

(def app (middleware/wrap-base #'app-routes))
