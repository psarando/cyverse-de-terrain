(ns terrain.routes.analyses
  (:require [compojure.api.sweet :refer [describe]]
            [common-swagger-api.schema :refer [context GET PATCH POST DELETE]]
            [common-swagger-api.schema.badges :refer [Badge NewBadge UpdateBadge]]
            [common-swagger-api.schema.apps :refer [AnalysisSubmission]]
            [ring.util.http-response :refer [ok]]
            [terrain.util :refer [optional-routes]]
            [schema.core :as s]
            [terrain.util.config :as config]
            [terrain.clients.analyses :as analyses])
  (:import [java.util UUID]))

(s/defschema DeletionResponse
  {:id (describe UUID "The UUID of the resource that was deleted")})

(defn secured-analyses-routes
  "The routes for accessing analyses information. Currently limited to badges."
  []
  (optional-routes [config/app-routes-enabled])

  (context "/badges" []
    :tags ["analyses"]

    (GET "/:id" [id]
      :summary "Get badge information by its UUID."
      :description "Gets badge information, including the UUID, the name of
      the user that owns it, and the submission JSON"
      :return  Badge
      (ok (analyses/get-badge id)))

    (PATCH "/:id" [id]
      :body         [badge UpdateBadge]
      :return       Badge
      :summary      "Modifies an existing badge"
      :description  "Modifies an existing badge, allowing the caller to change
      owners and the contents of the submission JSON"
      (ok (analyses/update-badge id badge)))

    (POST "/" []
      :body         [badge NewBadge]
      :return       Badge
      :summary      "Adds a badge to the database"
      :description  "Adds a badge and corresponding submission information to the
      database. The username passed in should already exist. A new UUID will be
      assigned and returned."
      (ok (analyses/add-badge badge)))

    (DELETE "/:id" [id]
      :return        DeletionResponse
      :summary      "Deletes a badge"
      :description  "Deletes a badge from the database. Will returns a success
      even if called on a badge that has either already been deleted or never
      existed in the first place"
      (ok (analyses/delete-badge id)))))
