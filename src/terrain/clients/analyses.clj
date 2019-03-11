(ns terrain.clients.analyses
  (:require [terrain.util.config :refer [analyses-base-uri]]
            [clj-http.client :as http]
            [cemerick.url :refer [url]]
            [terrain.auth.user-attributes :refer [current-user]]))


(defn analyses-url
  ([components]
   (analyses-url components {}))
  ([components query]
   (-> (apply url (analyses-base-uri) components)
       (assoc :query (assoc query :user (:username current-user)))
       (str))))

(defn get-badge
  [id]
  (:body (http/get (analyses-url ["badges" id]) {:as :json})))

(defn delete-badge
  [id]
  (:body (http/delete (analyses-url ["badges" id]) {:as :json})))

(defn update-badge
  [id submission-info]
  (:body (http/patch (analyses-url ["badges" id])
                     {:content-type  :json
                      :as            :json
                      :form-params   submission-info})))

(defn add-badge
  [submission-info]
  (:body (http/post (analyses-url ["badges"])
                    {:content-type :json
                     :as           :json
                     :form-params  submission-info})))
