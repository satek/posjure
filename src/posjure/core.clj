(ns posjure.core
  (:require [ring.middleware.resource :as ring-resource]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.json :as middleware]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [immutant.web :refer :all]
            [posjure.packing :as packing]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))


(defroutes app-routes
  (GET "/" [] "<h1>Posjure</h1>")
  (context "/pack/:packager" [packager]
           (defroutes packing-routes
             (POST "/" {body :body}
                   {:body {:packed (packing/bytes-to-string
                                     (packing/pack-from-params (packing/get-new-message packager) body))}})))
  (context "/unpack/:packager" [packager]
           (defroutes unpacking-routes
             (POST "/" {body :body}
                   {:body {:unpacked (packing/extract-fields packager
                                                             (packing/bytes-from-string (body "packed")))}})))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 8080
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-H" "--hostname HOST" "Remote host"
    :default "localhost"]
   ["-P" "--path PATH" "Application web path"
    :default "/"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: program-name [options]"
        ""
        "Options:"
        options-summary
        "\n"]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (run app :host (:hostname options) :port (:port options) :path (:path options))))

(defn -dev [& {:as args}]
  (run-dmc app :host "localhost" :port 8080 :path "/"))
