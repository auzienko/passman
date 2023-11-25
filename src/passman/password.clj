(ns passman.password)

(defn generate-password [length]
  (let [startChar 33
        endChar 123
        available-chars (reduce (fn [acc val] (str acc (char val))) "" (range startChar endChar))]
    (loop [password ""]
      (if (= (count password) length)
        password
        (recur (str password (rand-nth available-chars)))))))