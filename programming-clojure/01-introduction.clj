
;; define uma função para verificar se uma string é vazia
;; a função every? retorna true se para cada
;; item de uma coleção a pre condição de checagem for verdadeira
(defn blank? [str]
    ;; Verifica se todo caracter de str é um espaço
    (every? #(Character/isWhitespace %) str)) ;; utiliza uma classe do Java para verificar a pre condição

(blank? "")

;; Definimos um Record que tem semelhanças com uma classe do Java
;; É como uma dataclass do python/kotlin onde first name e last name
;; são atributos. O Record é imutável.
(defrecord Person [first-name last-name])
(def foo (->Person "Joffily" "F"))
(:first-name foo) ;; Joffily


(defn hello-world [username]
    (println (format "Hello, %s" username)))

(hello-world "Joffily") ;; nil -> Hello, Joffily

;; Vírgulas são opicionais
;; Vírgulas são tratadas como espaços em branco
[1, 2, 3, 4, 5]
[1 2 3 4 5]

; Cond macro para testar condições
(cond 
    (> 10 0) "10 > 0" 
    (= 10 0) "10 = 0")


; Clojure simplifies concurrent programming
;; it uses the STM (software transational memory)

(def accounts (ref #{}))
(defrecord Account [id balance])

; "The ref function creates a transactionally protected "
; reference to the current state of the database"

(dosync
    (alter accounts conj (->Account "CLJ" 1000.00)))
; dosync faz com que a atualização do record "Account" aconteça
; dentro de uma transação, evitando problemas de sincronização
; mutex, lock etc



; um novo hello world

(defn hello-word-2 [name]
    (str "Hello, " name))

(hello-word-2 "Joffily") ; "Hello, Joffily"


; Podemos criar uma referência imutável para um Set
(def visitantes (atom #{}) ) ; um atom que aponta para um set

; Criamos uma função impura que altera o atom de visitantes e
; registra todos os visitantes que receberam um hello
(defn hello-atom
    "Retorna uma saudação ao visitante e registra o visitante
    no Atom visitantes."
    [username]
    (swap! visitantes conj username)
    (str "Hello, " username))


(hello-atom "Joffily")
(hello-atom "Mari")

@visitantes ; #{"Mari" "Joffily"}


; Navegando entre bibliotecas do clojure / java
; Pensando no python é como se fizessemos um import io
; Então o clojure carrega o namespace IO do clojure.java
(require 'clojure.java.io)

; carregamos o namespace repl e renomeamos como rp
(require '[clojure.repl :as rp])
(rp/doc str) ; printa no repl a documentação de str

; A função find-doc auxilia na busca de documentação de funções
(rp/find-doc "reduce")

; é possível inspecionar o código de uma função através do macro `source`
(rp/source conj)
; (def
;  ^{:arglists '([coll x] [coll x & xs])
;    :doc "conj[oin]. Returns a new collection with the xs
;     'added'. (conj nil item) returns (item).  The 'addition' may
;     happen at different 'places' depending on the concrete type."
;    :added "1.0"
;    :static true}
;  conj (fn ^:static conj
;         ([] [])
;         ([coll] coll)
;         ([coll x] (clojure.lang.RT/conj coll x))
;         ([coll x & xs]
;          (if xs
;            (recur (clojure.lang.RT/conj coll x) (first xs) (next xs))
;            (clojure.lang.RT/conj coll x)))))
; nil
