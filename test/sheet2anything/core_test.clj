(ns sheet2anything.core-test
  (:require [clojure.test :refer :all]
            [sheet2anything.core :refer :all]))

(deftest docjure-interface-test
  (testing "init-sheets"
    (is (= 0 1))))

(deftest template-parsing-test
  (testing "split-token"
    (is (= ["<test>" " 123 456" ""]
           (split-token "<test> 123 456")))
    (is (= ["" "" "<SHEET test { A: fst} >"]
           (split-token "<SHEET test { A: fst} >")))
    (is (= ["" "123 456" ""]
           (split-token "123 456"))))
  (testing "read-template"
    (is (= [["<test>" " 123 456 " ""] ["<buzz>" " asd w werewr " ""] ["<1>" "" ""] ["<22>" "" ""] ["<333>" " rest" ""]]
           (read-template "§<test> 123 456 §<buzz> asd w werewr §<1>§<22> §<333> rest")))
    (is (= [["" "" "<SHEET Sheet { :A fst :B snd } >"] ["<fst>" " or " ""] ["<snd>" "" ""]]
           (read-template "§<SHEET Sheet { :A fst :B snd } > §<fst> or §<snd>")))
    ))

(deftest keyword-parsing-test
  (testing "set-keywords"
    (is (= '(["SHEET" "another one" "{ :A itzen :B HUA :C plitzen :D BUA }"])
           (set-keywords '("<SHEET \"another one\" { :A itzen :B HUA :C plitzen :D BUA }>"))))
    (is (= '(["SHEET" "one" "{ :A fst :B snd :C plitzen :D BUA }"] ["SHEET" "next one" "{ :A fst2 :B snd2 :C buzz }"])
           (set-keywords '("<SHEET \"one\" { :A fst :B snd :C plitzen :D BUA }>" "<SHEET \"next one\" { :A fst2 :B snd2 :C buzz }>"))))
    (is (= '(("" "one" "{ :A fst :B snd :C plitzen :D BUA }") ("ANYKEY" "next one" "{ :A fst2 :B snd2 :C buzz }") ("HEET" "buzz"))
           (set-keywords '("<\"one\" { :A fst :B snd :C plitzen :D BUA }>" "<ANYKEY \"next one\" { :A fst2 :B snd2 :C buzz }>" "SHEET \"buzz\""))))
    ))

(deftest print-tokens-test
  (testing "replace-token"
    (let [tokcoll '({ aaa 1 bbb 2 })
          bigcoll '([{ VAR1 1 VAR2 2 } { VAR1 11 VAR2 22 } { VAR1 111 VAR2 222 }])]
      (is (= 111 (replace-token (last (first bigcoll)) "VAR1")))
      (is (= 1 (replace-token (first tokcoll) "aaa")))
      (is (= 2 (replace-token (first tokcoll) "bbb")))
      (is (= nil (replace-token (first tokcoll) "ccc")))))
   (testing "print-records"
     (is (= nil
            (print-records ["fix1 " " fix2 " " fix3"]["VAR1" "VAR2" ""] '([{ VAR1 1 VAR2 2 } { VAR1 11 VAR2 22 } { VAR1 111 VAR2 222 }]))))))
