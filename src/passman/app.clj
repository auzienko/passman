(ns passman.app
  (:require [babashka.cli :as cli]
            [passman.db :as db]
            [passman.password :as pwd]
            [passman.stash :as stash]
            [passman.clipboard :refer [copy]]
            [table.core :as t]))

;; https://github.com/cldwalker/table
;; https://github.com/clojure/tools.cli

(def password-copied-msg "Password copied to clipboard")

(def cli-options  {:spec {:generate {:alias :g
                                     :desc "Generate new password"}
                          :length {:alias :l
                                   :default 42
                                   :desc "Generate new password length"
                                   :coerce :long}
                          :list {:alias :ls
                                 :desc "Show all sites"}
                          :help {:coerce :boolean}}})

(defn password-input []
  (println "Enter your master key:")
  (String. (.readPassword (System/console))))


(defn- generate-password [url username length]
  (do
    (stash/stash-init (password-input))
    (let [password  (pwd/generate-password length)]
      (stash/add-password url username password)
      (db/insert-password url username) 
      (copy password)
      (println password-copied-msg))))

(defn- get-password-from-stash [url username]
  (do
    (stash/stash-init (password-input))
    (let [password (stash/get-password url username)]
      (copy password)
      (println password-copied-msg))))

(defn- show-password-list []
  (t/table (db/list-passwords)))

(defn -main [& args]
  (let [parsed-args (cli/parse-args args cli-options)
        url (first (:args parsed-args))
        username (second (:args parsed-args))
        options (:opts parsed-args)]
    (cond
      (:list options) (show-password-list)
      (:generate options) (generate-password url username (:length options))
      (and url username) (get-password-from-stash url username)
      :else (println (cli/format-opts cli-options)))))
