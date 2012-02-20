(ns uberclj.core
  (:require [clj-http.client :as client]))

(defn artifacts []
  (let [r (client/get 
            "http://search.maven.org/solrsearch/select?q=org.clojure&rows=50&wt=json"
            {:as :json})
        docs (-> r :body :response :docs)]
    (map #(select-keys %1 [:g :a :latestVersion]) docs)))

(defn dependecy-str [artifact]
  (str "[" (:g artifact) "/" (:a artifact) " \"" (:latestVersion artifact) "\"]"))


(defn lein-proj-file []
  (let [arts (artifacts)
        deps (map dependecy-str arts)]
    (str "(defproject uberclj \"0.1.0-SNAPSHOT\"
    :dependencies [" (doall (reduce #(str %1 %2 "\n") deps)) "])")))

(defn build-project []
  (let [proj-str (lein-proj-file)]
    (with-open [w (clojure.java.io/writer  "uber_project.clj" :append false)]
      (.write w proj-str))))

