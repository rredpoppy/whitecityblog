(ns whitecity.blogfiles
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as st]))

(s/defschema BlogPost {:id String
                      :title String
                      :body String
                      :created Long})

(s/defschema Blog {:articles [(s/maybe BlogPost)]
                      :currentPage Long
                      :pageCount Long})

(defn to-blogpost [filename contents created]
  (let [slug (st/replace filename #"\.md$" "")
        title (st/capitalize (st/join " " (st/split slug #"\-")))] 
    {:id slug :title title :body contents :created created}))

(defn get-md-files [folder]
  (filter #(and (.isFile %) (.endsWith (.getName %)  ".md")) (-> folder io/file file-seq)))

(defn get-file-pages [fileList]
  (partition-all 9 (sort #(> (.lastModified %1) (.lastModified %2)) fileList)))

(defn get-article-summary [text]
  (first (st/split text #"\n\n")))

(defn get-articles [page pages]
  (map #(to-blogpost (.getName %) (get-article-summary (slurp %)) (.lastModified %)) (nth pages page [])))

(defn get-blog [page pages]
    {:articles (get-articles page pages)
        :currentPage page
        :pageCount (count pages)})

(defn get-post-file [id fileList]
  (first (filter #(== 0 (compare (str id ".md") (.getName %))) fileList)))

(defn get-article [postFile]
  (to-blogpost (.getName postFile) (slurp postFile) (.lastModified postFile)))