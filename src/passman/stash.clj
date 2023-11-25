(ns passman.stash
  (:require [babashka.pods :as pods]))

(def ^{:doc "path to stash file"} stash-file-path "passman.stash")

(pods/load-pod 'rorokimdim/stash  	"0.3.3")
(require '[pod.rorokimdim.stash :as stash])

(defn stash-init
  [password] (stash/init {:encryption-key password
                          :stash-path stash-file-path
                          :create-stash-if-missing true}))

(defn- resolve-key [url username]
  (str url ":" username))

(defn stash-add
  [parent-id k v]
  (stash/add parent-id k v))

(defn add-password [url username password]
  (stash-add 0 (resolve-key url  username) password))

(defn stash-nodes
  ([] (stash-nodes 0))
  ([parent-id] (stash/nodes parent-id)))

(defn get-password [url username]
  (let [key (resolve-key url username)
        nodes (stash-nodes)
        found-node (first (filter (fn [n]
                                    (= key (:key n))) nodes))] (:value found-node)))

(comment
  (stash-nodes)
  (add-password "facebook.com" "test@test.com" "secret")
  (get-password "facebook.com" "test@test.com")
  (stash-init "password"))