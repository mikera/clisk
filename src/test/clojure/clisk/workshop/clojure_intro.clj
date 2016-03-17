(ns clisk.workshop.clojure-intro
  "A namespace in which a range of basic Clojure features are introduced.")

;; Literals - Regular values

42                             ; Long 
3.14159265359                  ; Double
"Hello World"                  ; String
\M                             ; Characetr
0xFF8000                       ; Hex

;; Literals - Special values  

:foo                           ; Keyword
'hello                         ; Symbol
1/3                            ; Ratio
#"[0-9]+"                      ; Regex
15511210043330985984000000N    ; BigInt
189675.1678969698698969986M    ; BigDecimal
3r1201200                      ; Base-N integer

;; Literals - Collections

{:foo 10 
 :bar 20}
#{2 3 5 7 11 13}
[1 2 3 :foo]
(foo a b c)

;; Code as Data

(+ 2 3)
'(+ 2 3)
(eval '(+ 2 3))


;; Functions

(defn triple [x]
  (* x 3))

(triple 10)