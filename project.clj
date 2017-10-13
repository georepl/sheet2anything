(defproject sheet2anything "0.1.0-SNAPSHOT"
  :description "take spreadsheet columns and feed the cells into variables of a document template"
  :url "https://github.com/georepl/sheet2anything"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [dk.ative/docjure "1.11.0"]]
  :main ^:skip-aot sheet2anything.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
