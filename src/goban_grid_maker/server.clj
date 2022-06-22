(ns goban-grid-maker.server
  (:require
    [babashka.fs :as fs]
    [clojure.test :refer [are run-tests with-test]]
    [org.httpkit.server :refer [run-server]]))



(with-test
  (defn match-route [test-expr expr]
    (let [[test-method test-uri] test-expr
          [method uri] expr]
      (and
        (= test-method method)
        (if (instance? java.util.regex.Pattern test-uri)
          (re-matches test-uri uri)
          (= test-uri uri)))))
  (are [test-expr expr] (match-route test-expr expr)
    [:get "/"] [:get "/"]
    [:get #".*"] [:get "/"]
    [:get #".*"] [:get "/js/test"])
  (are [test-expr expr] (not (match-route test-expr expr))
    [:get ".*"] [:get "/js/test"]))


(defn handler [{:keys [:request-method :uri] :as req}]
  (condp match-route [request-method uri]
    [:get "/"] {:body (slurp "public/index.html")
                :status 200}
    [:get #"/js/.*"] (let [[body status] (let [filename (str "public" uri)]
                                           (if (fs/exists? filename)
                                             [(slurp filename) 200]
                                             (do
                                               (println filename "does not exist")
                                               [nil 404])))]
                       {:body body
                        :status status
                        :headers {"Content-Type" "application/javascript"}})
    [:get "/gobanGen.css"] {:body (slurp "public/gobanGen.css")
                            :status 200}
    [:get "/goban_t.png"] {:body (slurp "public/goban_t.png")
                           :status 200}
    ;; catch-all
    [:get #".*"] (let [msg (str "couldn't find " uri)]
                   (println msg)
                   {:body msg :status 500})))

(defn -main [& args]
  (run-server #'handler {:port 8000})
  (println "started server")
  @(promise))
