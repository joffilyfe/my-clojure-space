; Não há afirmações "statements" em clojure, existem apenas expreções
; Quando uma expresão é avaliada ela retorna um valor que é 
; substituído na expressão

(+ 1 2) ; 3

; Os parênteses são utilizados para definir uma lista, no caso acima
; a lista contém um símbolo `+` e dois inteiros `1 2`.
; quando o interpretador avalia o conteúdo da lista o primeiro elemento
; é tratado como uma operação que deve ser aplicada no restante da lista
; Esse estilo é chamado de "prefix notation"


; Symbols ou Símbolos
; É uma forma de nomear as coisas dentro do clojure. Funções
; namespaces, operadores "+, -, *", classes e etc.
; Os símbolos não podem iniciar com números mas podem com caracteres como ?

(def ?nome "Joffily")
(str ?nome) ; "Joffily"


; Coleções

; vetor
[1 2 3]

; lista
'(1 2 3)

; set
#{1 3 2} ; #{1 3 2}

; map

{:first-name "Joffily" :last-name "F" "chave-aleatoria" 10}
; A chave pode ser qualquer coisa como até outras expressões

{(+ 1 2) "a"} ; {3 "a"}
{0 "zero"} ; {0 "zero"}
{{:a "a"} "a"}


; Booleans e o nil

; true é true e false é false
; nil é false quando utilizado no contexto booleano


(if nil "retorna true" "retorna false") ; "retorna false"

; tirando false e nil, todo o resto é avaliado como true quando
; verificado em um contexto booleano

(if "string" "retorna true" "retorna false") ; string é avaliada como true
(if () "retorna true" "retorna false") ; a lista vazia tb é tida como true
(if 0 "retorna true" "retorna false") ; zero também é avaliado como true

; Os predicados são um conceito importante do Clojure. Estes são
; funções que retorna true ou false. É comum normar um predicado com ?
(nil? "string") ; false
(true? (= 1 1)) ; true


; Funções
; definimos funções com o `defn`

(defn greeting
  "Retorna uma mensagem de saudação no formato Hello nome"
  [username]
  (str "Hello, " username))

(str (greeting "Joffily")) ; Hello, Joffily

; Se não passarmos o parâmetro para a função greeting
; o clojure vai lançar uma exeção
; (greeting) ; ArityException
; Isso porque o clojure força a aridade das funções

; É possível definir funções com diferentes aridades
; como por exemplo
(defn greeting2
  ([] (greeting2 "World"))
  ([username] (str "Hello, " username)))

(str (greeting2)) ; Hello, World
(str (greeting2 "Joffily")) ; Hello, Joffily

; Assim quando chamamos a função greeting2 sem parâmetros ela substitui
; o parâmetro por "World"

; É possível criar uma função com aridade variável apenas adicionando
; & na frente do parêmetro

(defn greeting3
  [& names]
  (str names))

(greeting3 "jof" "mari") ;"(\"jof\" \"mari\")"
(greeting3) ; ""

(defn count-names [& names]
  (str "Total: " (count names)))

(count-names "joff" "Mari") ; "Total: 2"

; Funções anônimas
; Utilizamos funções anônimas ou não nomeadas quando:
; 1) Uso exploratório, breve e que acrescenta complexidade de leitura no código
; 2) Uso apenas no escopo de outra função para fins internos
; 3) Uso interno em uma função para capturar valores


; Exemplo de função que retorna apenas palavras com mais de duas letras
(require '[clojure.string :as cstr])

(defn indexable-worlds [text]
  (filter (fn [word] (> (count word) 2)) (cstr/split text #"\W+")))

(indexable-worlds "O pedestre deve atravessar na faixa de transito") ; ("pedestre" "deve" "atravessar" "faixa" "transito")

; É possível utilizar um macro mais curto para definir funções anônimas
; Algo como #(str %1) - neste caso é uma função que recebe um parâmetro e aplica o str

(defn indexable-worlds2 [text]
  (filter #(> (count %) 2) (cstr/split text #"\W+")))

; ("pedestre" "deve" "atravessar" "faixa" "transito")
(indexable-worlds2 "O pedestre deve atravessar na faixa de transito")


; Variáveis

; Podemos fazer biding de valores em variáveis utilizando o macro `def`


(def nome "Joffily") ; -> #'user/nome
nome ; "Joffily"


; O let cria um escopo léxico visível apenas dentro de si ()
; Mesmo dentro da função não não conseguimos acesar
(defn square-corners [bottom left size]
  (let [top (+ bottom size)
        right (+ left size)]
    [[bottom left] [top left] [top right] [bottom right]]))

(square-corners 0 0 10)


; Destructuring / Destruição eeerh

; Consiste em acessar/capturar apenas a parte necessária de uma estrutura de dados.
; Por exemplo fazendo o binding de parte de um Map


(defn get-first-name [{fname :first-name}]
  (str "Apenas o first-name: " fname))

(get-first-name {:first-name "Joffily" :last-name "F"}) ; "Apenas o first-name: Joffily"

; No bloco do do let nós fazemos biding apenas os dois primeiros números
; 1 e 2 para as símbolos x y 
(let [[x y] [1 2 3]]
  (conj '() x y)) ; (2, 1)

; Também é possível pular items utiliando o _
(let [[_ _ x] [1 2 3]]
  [x]) ; [3]

; Utilizando o `:as` nós fazemos o binding de toda a coleção no símbolo informado "coords"
(let [[x y :as coords] [1 2 3 4 5 6]]
  (str "x: " x ", y: " y ", total dimensions " (count coords)))
; "x: 1, y: 2, total dimensions 6"

(require '[clojure.string :as cstr])

(defn ellipsize [text]
  (let [[w1 w2 w3] (cstr/split text #"\s+")]
    (cstr/join " " [w1 w2 w3 "..."])))

(ellipsize "Um pedestre deve atravessar na faixa") ; "Um pedestre deve ..."

; Namespaces


; Quando associamos um valor a um nome ou definimos uma função
; nós estamos fazendo bind do nome a um namespace
(def foo :foo)

(resolve 'foo) ; #'user/foo

; É possível trocar de namespace utilizando a função (in-ns 'nome)
; (in-ns 'teste) ; quando estamos no repl o namespace é trocado
; pacote java.lang fica disponível automaticamente

String ; -> java.lang.String

; Por padrão, todas as classes que estão fora do pacote java.lang precisam ser chamadas
; com todo o caminho do pacote
java.io.File/separator ; "/"

; Para não precisar qualificar todo o caminho é possível importar uma classe específica
; (import '(java.io File))
; (File/separator) ; "/"

; A importação é utilizada apenas para classes JAVA. Para variáveis Clojure de outros
; namespaces é preciso requere-las com (require)
(require 'clojure.string)
(clojure.string/split "O pedestre precisa atravessar na faixa" #"\s+")
; ["O" "pedestre" ... "faixa"]

; também é possível apelidar o namespace
(require '[clojure.string :as str])
(str/split "O pedestre precisa atravessar na faixa" #"\s+")
; ["O" "pedestre" ... "faixa"]

; Assim como em outras linguagens, em Clojure nós costumamos definir os imports/requires
; no topo do arquivo. E assim como no java/php nós definimos um namepsace/pacote

;; Algo como:
(ns exploring-clojure
  (:require [clojure.string :as str])
  (:import (java.io File)))

; foo agora está definido no namespace `exploring-clojure`
(def foo :bar)

; Podemos definir um ponto de entrada `main` para um arquivo
; neste ponto nós podemos dar a entrada ao script/programa
; (defn -main
;   [& args]
;   (println foo))


; Calling Java
(new java.util.Random) ; instancia um objeto do tipo Random
(java.util.Random.) ; Tem o mesmo efeito de utilizar o new ..
(def rnd (new java.util.Random)) ; faz o binding da nova instância

;; invoca um método
(. rnd nextInt 10) ; chama o método `nextInt` do objeto `rnd`

;; Acessa um atributo de uma instância
(def p (java.awt.Point. 10 20)) ; insância um Ponto
(. p x) ; Acessa a posição x -> 10

;; Acessa um método estático
(. System lineSeparator) ; "\n"

;; Acessa um atributo estático
(. Math PI) ; -> 3.14..


; Também é possível reescrever estes exemplos de forma
; mais consisa

;; Método nextInt do ojeto rnd
(.nextInt rnd 10) ; -> 1

;; Atributo x do objeto p
(.x p)

;; Acessa método estático
(System/lineSeparator)

;; Acessa atributo estático
(Math/PI)


;; Flow control

; Criando branches com o if
; é similar a qualquer linguagem mas o branch só aceita
; uma instrução no seu bloco, ou seja, o código comentado
; abaixo não funciona porque existem 3 instruções, uma a mais
; do else. O correto é ter um para em caso de true (faz-algo)
; e uma para o else (faz-algo-else)
; (if (< 1 100)
;   faz-algo
;   faz-algo-else
;   faz-algo-else)


(defn is-small? [n]
  (if (< n 100) "yes" "no"))

(is-small? 10) ; "yes"

;; Efeitos colaterais em branches com o DO

(defn is-small2? [n]
  (if (< n 100)
    "yes"
    (do
      (println "the number " n " is bigger than 100")
      "no")))

; aqui nós printamos uma string e interagimos com o
; mundo exterior da função

(is-small2? 1000)

;; Recursão e loops

(loop [result [] x 5]
  (if (zero? x)
    result
    (recur (conj result x) (dec x)))) ; [5 4 3 2 1]

; O macro loop faz um biding em result com []
; e também faz o biding de x com 5
; o if testa se x é zero, em caso de positivo retorna result
; senão o bloco de recur é chamado.
; O bloco de `recur` então refaz os bidings das variáveis result e x
; no caso, em result é feito um append de x
; e no caso de x é subtraído 1
; então a execução volta para o topo do loop e é dado continuidade

;; É possível reescrever essa função apenas com recursão, sem o lopp
(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(countdown [] 5) ; [5 4 3 2 1]

; O livro fala sobre o poder do recur e da recursão mas
; também diz que isso não será utilizado com frequência,
; uma vez que muitas funções já podem ser utilizadas para
; uma grande variedade de casos

; Tem o mesmo efeito que os dois exemplos acima
(into [] (take 5 (reverse (range 6)))) ; [5 4 3 2 1]
(into [] (take 5 (iterate dec 5)))
(vec (reverse (rest (range 6))))


; O livro então questiona sobre o uso de loops
; dentro do java e outras linguages
; utiliza o exemplo extraído da biblioteca Commons Lang
; que retorna o índice da primeira ocorrência de um caracter
; que é informado a partir de uma lista de caracteres
; algo como: find("abcdefef", ["c", "d", "z"])
; O código em java tem 14 linhas, 4 ramificações
; 3 pontos de retorno e 3 variáveis
; Então é feita a seguinte implementação:


(defn indexed [coll] (map-indexed vector coll))
(indexed "ab") ; ([0 \a] [1 \b])

(defn index-filter [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))

; Aqui nós já implementamos a função que retorna todos os índices
; em uma string a partir de um Set 
(index-filter #{\z \b \c} "abcdef") ; (1 2)

(defn index-of-any [pred coll]
  (first (index-filter pred coll)))

; Aqui nós retornamos apenas o primeiro índice encontrado
(index-of-any #{\z \b \c} "abcdef") ; 1

; Ao términio nós implementamos a mesma funcionalidade
; com apenas 6 linhas de código, 1 ramificação, 1 ponto de saída
; e 0 variáveis.

(index-of-any #{\z \b \c} "")


; A função criada em clojure é mais versátil e consegue
; buscar em qualquer coleção com qualquer tipo de predicado
(index-of-any #{:h} [:t :t :h :t :h :t :t :t :h :h]) ; 2