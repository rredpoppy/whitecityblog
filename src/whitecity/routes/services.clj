(ns whitecity.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [postal.core :refer [send-message]]
            [whitecity.blogfiles :refer :all]
            [whitecity.settings :refer [settings]]))

(defapi service-routes
  ; (ring.swagger.ui/swagger-ui
  ;  "/swagger-ui")
  ;JSON docs available at the /swagger.json route
  (swagger-docs
    {:info {:title "WhiteCity code blog API"}
     :produces ['application/json']
     :consumes ['application/json']})
  
  (POST* "/contact" []
         :body     [contact {:name String, :email String, :msg String}]
         (when-let [{:keys [name email msg]} contact]
            (let [{:keys [code error message]} 
              (send-message {:host (get @settings "smtp-host")
                             :user (get @settings "smtp-user")
                             :pass (get @settings "smtp-pass")
                             :port (get @settings "smtp-port")}
                            {:from (get @settings "mail-from")
                             :to (get @settings "mail-to")
                             :reply-to email
                             :subject (get @settings "mail-subject")
                             :body [{:type "text/plain; charset=utf-8"
                                    :content (str "Message from " name " (" email "):\n" msg)}]})]
             (if (== code 0)
               (ok message)
               (status 500)))))

  (GET* "/blogposts/:slug" [slug]
        :return      (s/maybe BlogPost)
        :path-params [slug :- String]
        :summary     "Retrieve a blog post by slug"
        (let [files (get-md-files (get @settings "md-folder"))
              postFile (get-post-file slug files)] 
          (when (not (nil? postFile)) 
            (ok (get-article postFile)))))
  
  (GET* "/blogposts" [page]
        :return       Blog
        :query-params [{page :- Long 0}]
        :summary      "Retrieve a list of 9 blog posts per page starting with page {page}"
        (let [pages  (get-file-pages (get-md-files (get @settings "md-folder")))] 
          (ok (get-blog page pages)))))
