(ns passman.clipboard
  (:require [babashka.process :refer [sh]]))

(defn copy [text]
  (-> (sh ["echo" text]) (sh ["pbcopy"]) :out))
