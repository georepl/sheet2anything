(ns sheet2anything.core
  (:require  [clojure.string :as str]
             [clojure.edn :as edn]
             [clojure.spec.alpha :as spec]
             [dk.ative.docjure.spreadsheet :as xl])
    (:gen-class))


(defn init-spreadsheet [xls-file]
  (xl/load-workbook xls-file))


(defn init-sheets [workbook]
  (xl/sheet-seq workbook))


(defn read-columns [workbook keyw-coll]
  (let [sheet-list (map #(xl/select-sheet (nth % 1) workbook) keyw-coll)
        colmn-list (map #(edn/read-string (nth % 2)) keyw-coll)]
    (map #(xl/select-columns %1 %2) colmn-list sheet-list)))


(defn replace-token [tokrec var]
  (get tokrec (symbol var)))


(defn print-records [fix-coll var-coll token-coll]
  (doseq [t (first token-coll)]  ; for the time being: take list for first sheet only
    (print (str/join (interleave (map #(replace-token t %) var-coll) fix-coll)))))


(defn split-token [s]
  (if (str/starts-with? s "<")
    (let [l     (str/index-of s \>)
          len   (if (nil? l) 1 (if (zero? l) l (inc l)))
          [a b] (split-at len s)
          s1    (if (str/blank? (apply str a)) "" (apply str a))
          s2    (if (str/blank? (apply str b)) "" (apply str b))]
      (if (str/starts-with? (apply str s1) "<SHEET")
        (vector "" (apply str s2) (apply str s1))
        (vector (apply str s1) (apply str s2) "")))
    (vector "" s "")))


; create three vectors from the template file: one for the fix strings and one for those to be replaced plus a keyword string list
(defn read-template [s]
  (let [str-coll (str/split s #"§")]
   (map split-token str-coll)))


(defn set-keywords [keyw-coll]
  (letfn [(split-keywords [keyw]
            (let [kw    (apply str (rest (butlast keyw)))]
              (map str/trim (str/split kw #"\""))))]
    (map split-keywords keyw-coll)))


; read the template and the spreadsheet files,
; initialize the data structures and create the result file
(defn process [xls-file tpl-file]
  (let [workbook  (init-spreadsheet xls-file)
        sht-coll  (init-sheets workbook)
        coll      (read-template (slurp tpl-file))
        fix-coll  (map second coll)
        var-coll  (map #(apply str (rest (butlast %))) (map first coll))
        key-coll  (set-keywords (filter (complement empty?) (map last coll)))
        val-coll  (read-columns workbook key-coll)]
    (print-records fix-coll var-coll val-coll)))


; sheet2anything x <spreadsheet name> t <document template name>
(defn -main [& vargs]
  (let [args   (apply hash-map vargs)
        keyl   (map #(keyword (apply str %)) (keys args))
        argset (zipmap keyl (vals args))
        spreadsheet (:x argset)
        template    (:t argset)]
    (process spreadsheet template)))
