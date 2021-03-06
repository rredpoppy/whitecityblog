(defproject whitecity "1.0"

  :description "WhiteCity Code contact app"
  :url "http://whitecitycode.com"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.taoensso/timbre "4.0.2"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.67"]
                 [environ "1.0.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-ttl-session "0.1.1"]
                 [ring "1.4.0"
                  :exclusions [ring/ring-jetty-adapter]]
                 [metosin/ring-middleware-format "0.6.0"]
                 [metosin/ring-http-response "0.6.3"]
                 [bouncer "0.3.3"]
                 [prone "0.8.2"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [org.clojure/data.json "0.2.6"]
                 [metosin/compojure-api "0.22.1"]
                 [org.immutant/web "2.0.2"]
                 [javax.activation/activation "1.1.1"]
                 [com.draines/postal "1.11.3"]]

  :min-lein-version "2.0.0"
  :uberjar-name "whitecity-1.0.jar"
  :jvm-opts ["-server"]

  :main whitecity.core

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]]
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :aot :all}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[ring/ring-mock "0.2.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.7.0"]]


                  :repl-options {:init-ns whitecity.core}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  ;;when :nrepl-port is set the application starts the nREPL server on load
                  :env {:dev        true
                        :port       3000
                        :nrepl-port 7000}}
   :project/test {:env {:test       true
                        :port       3001
                        :nrepl-port 7001}}
   :profiles/dev {}
   :profiles/test {}})
