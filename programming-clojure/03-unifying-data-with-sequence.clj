; Este capítulo mostra como tudo em clojure se resume a uma
; sequência de dado. PS: nem toda estrutura é uma sequência (ex: maps)
; Serão mostradas as coleções do Clojure e Java;
; Serão mostradas as Arrays e Strings do Java;
; Expresões regulares;
; Estrutura de diretórios
; I/O streams
; Árvores XML
(ns unifying-data-with-sequence)

;; Tudo é uma sequência
;; todas as estruturas de dados possuem ao menos três capacidades
;; básicas: (first, rest e cons)

(def numeros (take 10 (range 10)))

; first
(first numeros)
; 0

; rest
(rest numeros)
; (1 2 3 4 5 6 7 8 9)

; cons
(cons 100 numeros)
; (100 0 1 2 3 4 5 6 7 8 9) o `cons` cria uma nova sequência inserindo um novo elemento no índice 0

;; Os retornos dessas três funções é sempre uma nova sequência
(seq? (rest numeros))
; true

;; Podemos construir uma sequência a partir do macro `seq`
;; se a estrutura estiver vazia então ele volta  nil

(seq {:a 1})
; ([:a 1])

;; Podemos verificar se algo é uma sequência com o prédicado `seq?`
(seq? {})
; nil

(def example-map (sorted-map :x 0 :a 9 :b 1 :c 8))
(first example-map)
; [:a 9]

; Maps e Sets possuem uma ordem de travessia estável mas
; que é dependente de implementação internos da linguagem
; para garantir o mesmo resultado ao utiliar funções como
; `first` e `rest` é aconselhável ordenar os elementos.
; No caso do map nós podemos utilizar o sorted-map que
; ordena pela CHAVE que é indexada e única

(def example-map {:x 0 :a 9 :b 1 :c 8})
(into (sorted-map) example-map)
; A função `into` aplica os item de uma coleção em outra
; utilizando a função `conj`, a syntax da função `into` é
; (into dest-coll from-coll), exemplo
; (into '() '(1 2)) ; -> '(2 1)
; No caso acima a função `sorted-map` retorna um `Map` vazio
; com capacidade de ordenação porque na verdade é uma TreeMap em
; vez de ArrayMap de um Map `{}`

(class (sorted-map))
; -> clojure.lang.PersistentTreeMap

(class {})
; -> clojure.lang.PersistentArrayMap

;; conj
; A função `conj` é semelhante a função `cons` mas diferentemente
; da função `cons` a inserção do elemento ou elementos vai
; depender do tipo de estrutura de dados que é utilizada
; exemplo:
(conj [0] 1)
; -> [0 1]
(conj '(0) 1)
; -> (1 0)

; Já a função `cons` sempre insere no início da nova coleção.

; A função `conj` aceita mais de um elemento a serem inseridos,
; exemplo:
(conj '() 1 2 3 4)
; -> (4 3 2 1)

;; Um lembrete importante: Em clojure as sequências são imutáveis!


;; Using the sequence library
; Segundo o livro, as funções que operam em cima de sequências
; podem ser agrupadas em 4 categorias;
; 1) Funções que criam sequências
; 2) Funções que filtram sequências
; 3) Sequências de predicados
; 4) Funções que transforam sequências
; Acontece que isso depende de como você interpreta as operações,
; uma vez que as sequências são imutáveis, então é lógico presumir
; que essas funções na verdade criam novas sequências.

; (range start? end? step?)
(range 10) ;; start omitido
; (0 1 ... 9)

(range 10 15)
; (10 11 12 13 14)
(range 10 15 2) ;; o `step` é 2, ou seja a sequência criada é de 2 em 2
; (10 12 14)

(range 0 -10 -1)
; (0 -1 -2 -3 -4 -5 -6 -7 -8 -9)

(range 0 -1 -0.25)
; (0 -0.25 -0.5 -0.75)

(range 1/2 4 1) ;; divisões
; (1/2 3/2 5/2 7/2)


;; (repeat times element)
;; (repeat n x)
(repeat 10 1)
; (1 1 1 1 1 1 1 1 1 1)

;; Se a quantidade `n` for omitida, nós teremos uma sequência
;; infinita.

;; (iterate f x)
;; Aplica a função `f` ao elemento `x` de forma infinita
; (iterate inc 1) ; não faça isso haha

;; (take n coll)
;; A função `take` uma quantidade `n` de uma coleção `coll`
(take 10 (iterate inc 1))
; -> (1 2 3 4 5 6 7 8 9 10)
; A função take retorna uma sequência `lazy`. É um meio de
; tornar uma sequência infinita em finita.

;; cycle coll
;; A função cycle retorna uma sequência lazy (infinita)
;; repetindo a coleção
; ex: (cycle '(1 2)) -> (1 2 1 2 1 2 1 2 ...)
;; Podemos então tornar essa seq. infinita em finita com o take
(take 10 (cycle '(1 2)))
; (1 2 1 2 1 2 1 2 1 2)

;; (interleave coll1 coll2 &collsetc)
(def whole-numbers (iterate inc 1)) ; -> lazy seq
(interleave whole-numbers ["A" "B" "C" "D" "E"])
; (1 "A" 2 "B" 3 "C" 4 "D" 5 "E")

;; Por mais que o símbolo `whole-numbers` seja uma sequência
;; infinita de números, a função `interleave` retorna um resultado finito
;; porque a segunda sequência que foi passada é finita, então
;; essa intercalagem entre as duas coleções tem fim quando
;; o elemento `E` da coleção 2 é alcançado.
;; Essa função me parece muito com a função `zip` do python.

(interleave whole-numbers ["A" "B" "C" "D" "E"] '(1))
; (1 "A" 1)

;; Detalhe que a intercalagem acaba quando o último item
;; da menor sequência é atingido :)

;; (interpose sep coll)
;; Funciona como o `join` no python, porém pode ser usado com outros
;; tipos além de strings
(interpose 1 [0 0 0])
; (0 1 0 1 0)

(interpose "," ["apples" "bananas" "grapes"])
; ("apples" "," "bananas" "," "grapes")

; Se a intenção for utilizar um `join` com strings
; é aconselhável utilizar algo já pronto como
; clojure.string.join

(require '[clojure.string :as str])
(str/join "," ["apples" "bananas" "grapes"])
; "apples,bananas,grapes"

; Também é possível replicar o resultado com:
(apply str (interpose "," ["apples" "bananas" "grapes"]))
; "apples,bananas,grapes"

; A função `apply` aplica uma função em uma coleção :)
; "Applies fn f to the argument list 
; formed by prepending intervening arguments to args."

;; Funções que criam sequências

;; (list & elements)
(list 1 2 3 4)
; (1 2 3 4)

;; (vector & elements)
(vector 1 2 3 4)
; [1 2 3 4]

;; (hash-set & elements)
(into (sorted-set) (hash-set 1 2 3 4))
; #{1 4 3 2}

;; (into (sorted-set) (hash-set 1 2 3 4))
; #{1 2 3 4}

;; (hash-map key-1 val-1 ...)
(hash-map :1 1 "a" 2)
; {:1 1, "a" 2}

;; (set coll)
;; Retorna um Set mas diferentemente de `sorted-set`,
;; a função `set` espera uma coleção
(set [1 2 3 1])
; #{1 2 3}

;; (vec coll) - Similar ao `set`
(vec '(1 2 3))
;; [1 2 3]


;; Filtrando coleções

;; (filter pred coll)
(filter even? '(1 2 3 4 5))
; (2 4)

;; (take-while pred coll)
;; Semelhante ao `filter` mas só retorna elementos até
;; encontrar o primeiro `false` lógico para o predicado
;; aplicado
(take-while odd? '(1 2 3 4 5))
; (1)
(def vowel? #{\a \e \i \o \u})
(def consonant? (complement vowel?))
(take-while consonant? "the-quick-brown-fox")
; (\t \h)

(take-while vowel? "aeiwaeiou")
; (\a \e \i)
;; Vale notar que o set #{\a \e \i \o \u} age como função
;; em vowel?

;; (drop-while pred coll)
;; Funciona de modo similar ao take-while mas
;; em vez de filtrar até achar o primeiro false
;; o `drop-while` não retorna elementos até achar o
;; primeiro false
(drop-while consonant? "the-quick-brown-fox")
; (\e \- \q \u \i \c \k \- \b \r \o \w \n \- \f \o \x)

(drop-while vowel? "aeiwaeiou")
; (\w \a \e \i \o \u)

;; (split-at index coll)
;; (split-with pred coll)
;; São funções para dividir sequências

report erratum • discuss
split-at takes an index, and split-with takes a predicate: 
(split-at 5 (range 10))
; [(0 1 2 3 4) (5 6 7 8 9)]

(split-with #(<= % 10) (range 0 20 2))
; [(0 2 4 6 8 10) (12 14 16 18)]

(split-with vowel? "aeiwaeiou")
[(\a \e \i) (\w \a \e \i \o \u)]

;; todas essas funções retornam sequências lazy.