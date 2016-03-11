(ns whitecity.middleware
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring-ttl-session.core :refer [ttl-memory-store]]
            [ring.util.http-response :refer [internal-server-error]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (timbre/error t)
        (internal-server-error "Something very bad has happened!")))))

(defn wrap-dev [handler]
  (if (env :dev)
    (-> handler
        reload/wrap-reload
        wrap-exceptions)
    handler))

(defn wrap-csrf [handler]
  (wrap-anti-forgery handler))

(defn wrap-formats [handler]
  (wrap-restful-format handler {:formats [:json-kw :transit-json :transit-msgpack]}))

(defn wrap-base [handler]
  (-> handler
      wrap-dev
      wrap-formats
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))))
      wrap-internal-error))
