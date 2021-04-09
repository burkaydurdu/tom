(defproject tom "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.24"]
                 [reagent "1.0.0"]
                 [re-frame "1.2.0"]
                 [day8.re-frame/tracing "0.6.2"]]

  :plugins [[cider/cider-nrepl "0.25.6"]
            [lein-shadow "0.3.1"]
            [lein-less "1.7.5"]
            [lein-shell "0.5.0"]
            [lein-pprint "1.3.2"]]

  :min-lein-version "2.9.0"

  :jvm-opts ["-Xmx1G"]

  :source-paths ["src"]

  :test-paths   ["test/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js" "target" "test/js"]

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :shadow-cljs {:nrepl  {:port 8777}

                :dev-http {8080 "resources/public/"}

                :builds {:main {:target    :node-script
                                :output-to "resources/main.js"
                                :main      tom.main.core/main}
                         :renderer {:target     :browser
                                    :output-dir "resources/public/js"
                                    :asset-path "/js"
                                    :modules    {:renderer {:init-fn tom.renderer.core/init
                                                            :preloads [devtools.preload
                                                                       day8.re-frame-10x.preload]}}
                                    :dev        {:compiler-options
                                                 {:closure-defines {re-frame.trace.trace-enabled? true
                                                                    day8.re-frame.tracing.trace-enabled? true}}}
                                    :release    {:build-options
                                                 {:ns-aliases
                                                  {day8.re-frame.tracing day8.re-frame.tracing-stubs}}}

                                    :devtools   {:http-root "resources/public"
                                                 :http-port 8280}}

                         :browser-test {:target :browser-test
                                        :ns-regexp "-test$"
                                        :runner-ns shadow.test.browser
                                        :test-dir "target/browser-test"
                                        :devtools {:http-root "target/browser-test"
                                                   :http-port 8290}}

                         :karma-test {:target :karma
                                      :ns-regexp "-test$"
                                      :output-to "target/karma-test.js"}}}

  :shell {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows         ["cmd" "/c" "start"]
                              :macosx          "open"
                              :linux           "xdg-open"}}}

  :aliases {"dev"          ["do"
                            ["shell" "echo" "\"DEPRECATED: Please use lein watch instead.\""]
                            ["watch"]]

            "watch"        ["with-profile" "dev" "do"
                            ["less" "once"]
                            ["shadow" "watch" "main" "renderer" "browser-test" "karma-test"]]

            "prod"         ["do"
                            ["shell" "echo" "\"DEPRECATED: Please use lein release instead.\""]
                            ["release"]]

            "release"      ["with-profile" "prod" "do"
                            ["shadow" "release" "main" "renderer"]]

            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]

            "karma"        ["do"
                            ["shell" "echo" "\"DEPRECATED: Please use lein ci instead.\""]
                            ["ci"]]

            "ci"           ["with-profile" "prod" "do"
                            ["shadow" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles {:dev
             {:dependencies [[binaryage/devtools "1.0.2"]
                             [day8.re-frame/re-frame-10x "1.0.2"]]
              :source-paths ["dev"]}
             :prod {}})
