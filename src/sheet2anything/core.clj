(ns sheet2anything.core
 (:require  [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as spec]
            [dk.ative.docjure.spreadsheet :as xl])
  (:gen-class))


(def spreadsheet (atom { :keyw-coll [] :token-coll [] }))


(defn init-sheets [xls-file]
  (let [workbook        (xl/load-workbook xls-file)
        sheet-coll      (xl/sheet-seq workbook)
        sheet-name-coll (map xl/sheet-name sheet-coll)]
    (swap! spreadsheet assoc :workbook workbook :default-sheet-name (first sheet-name-coll))
    sheet-coll))


(defn read-columns []
  (map #(xl/select-columns (edn/read-string (nth % 2)) (xl/select-sheet (nth % 1) (:workbook @spreadsheet))) (:keyw-coll @spreadsheet)))


(defn finish-sheets [sheets-coll]
  (let [keyw-coll (read-columns)]
    (swap! spreadsheet assoc :token-coll keyw-coll)))


(defn replace-token [tokrec var]
  (get tokrec (symbol var)))


(defn print-records [f-coll v-coll]
  (let [fix-coll (if (< (count f-coll)(count v-coll)) (conj (vec f-coll) "") f-coll)
        var-coll (if (< (count v-coll)(count f-coll)) (conj (vec v-coll) "") v-coll)]
    (doseq [t (first (:token-coll @spreadsheet))]  ; for the time being: take list for first sheet only
      (println (str/join (interleave fix-coll (map #(replace-token t %) var-coll)))))))


(defn read-template [s]
  (letfn [(step [s keyw res]
            (if (empty? s)
              [keyw res]
              (let [n (str/index-of s "§<")
                    m (if (nil? n) nil (str/index-of s ">" n))]
                (if (nil? m)
                  [keyw (conj res s "")]
                  (let [srest (drop (inc m) s)
                        s1    (apply str (take n s))
                        s2    (apply str (take (- m n 2) (drop (+ n 2) s)))]
                    (if (str/starts-with? (str/trim s2) "SHEET")
                      (step (apply str srest) (conj keyw s2) res)
                      (step (apply str srest) keyw (conj res s1 s2))))))))]
    (step s [] [])))


; create two vectors from the template file: one for the fix strings and one for those to be replaced
(defn evaluate-template [tpl-file]
  (let [[keywlist strlist]  (read-template (slurp tpl-file))
        coll     (partition-all 2 strlist)
        fix-coll (map first coll)
        tpl-coll (filter #(not (empty? %)) (map second coll))]
    (vector fix-coll tpl-coll keywlist)))


; split a string of type §<sheet\column> or §<column>
; create the respective entries in the spreadsheet atom and return the index of the current column's value vector
(defn transform-XXX [s]
  (let [[s1 s2] (str/split s #"\\")
        sheet-name  (if (nil? s2) (:default-sheet-name @spreadsheet) s1)
        column-name (if (nil? s2) s1 s2)
        token       (vector sheet-name column-name)]
    (when (empty? (filter #(= (first %) token) (:token-coll @spreadsheet)))
      (swap! spreadsheet assoc :token-coll (conj (:token-coll @spreadsheet) (vector token []))))
    (dec (count (:token-coll @spreadsheet)))))


; split a string of type §<sheet\column> or §<column>
; create the respective entries in the spreadsheet atom and return the index of the current column's value vector
(defn transform [s]
  (when (empty? (filter #(= % s) (:token-coll @spreadsheet)))
    (swap! spreadsheet assoc :token-coll (conj (:token-coll @spreadsheet) (vector s []))))
  s)


(defn set-keywords [keyw-coll]
  (letfn [(split-keywords [keyw]
            (map str/trim (str/split keyw #"\"")))]
    (swap! spreadsheet assoc :keyw-coll (map split-keywords keyw-coll))))


; read the template and the spreadsheet files,
; initialize the data structures and create the result file
(defn read-files [xls-file tpl-file res-file]
  (let [sht-coll (init-sheets xls-file)
        [fix-coll tpl-coll keyw-coll]  (evaluate-template tpl-file)
        var-coll (doall (map transform tpl-coll))]
    (set-keywords keyw-coll)
    (finish-sheets sht-coll)
    (print-records fix-coll var-coll)))


; sheet2anything x <spreadsheet name> t <document template name>
(defn -main [& vargs]
  (let [args   (apply hash-map vargs)
        keyl   (map #(keyword (apply str %)) (keys args))
        argset (zipmap keyl (vals args))
        spreadsheet (:x argset)
        template    (:t argset)
        result      (:r argset)]
    (read-files spreadsheet template result)))
