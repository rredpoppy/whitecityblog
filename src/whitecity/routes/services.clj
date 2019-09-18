(ns whitecity.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [postal.core :refer [send-message]]
            [whitecity.settings :refer [settings]]))

(defn smtp-settings []
  (let [host (get @settings "smtp-host")
        user (get @settings "smtp-user")
        pass (get @settings "smtp-pass")
        port (get @settings "smtp-port")
        ssl (get @settings "smtp-ssl")
        base-params {:host host :port port :user user :pass pass}]
    (cond
      (= ssl "ssl") (assoc base-params :ssl true)
      (= ssl "tls") (assoc base-params :tls true)
      :else base-params)))

(defn cors-mw
  "Cross-origin Resource Sharing (CORS) middleware. Allow requests from all
  origins, all http methods and Authorization and Content-Type headers."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "https://whitecitycode.com")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET, POST, OPTIONS")
          (assoc-in [:headers "Access-Control-Allow-Headers"] "Authorization, Content-Type")))))

(defapi service-routes
  ; (ring.swagger.ui/swagger-ui
  ;  "/swagger-ui")
  ;JSON docs available at the /swagger.json route
  (swagger-docs
    {:info {:title "WhiteCity code blog API"}
     :produces ['application/json']
     :consumes ['application/json']})

  (OPTIONS* "*"           {:as request}
           :tags        ["Preflight"]
           :return      {}
           :summary     "This will catch all OPTIONS preflight requests from the
                        browser. It will always return a success for the purpose
                        of the browser retrieving the response headers to validate CORS
                        requests. For some reason it does not work in the swagger UI."
           (ok {}))

  (POST* "/contact" []
         :body     [contact {:name String, :email String, :msg String}]
         (when-let [{:keys [name email msg]} contact]
           (let [{:keys [code error message]}
                 (send-message (smtp-settings)
                               {:from (get @settings "mail-from")
                                :to (get @settings "mail-to")
                                :reply-to email
                                :subject (get @settings "mail-subject")
                                :body [{:type "text/plain; charset=utf-8"
                                        :content (str "Message from " name " (" email "):\n" msg)}]})]
             (if (== code 0)
               (ok message)
               (status 500))))))
