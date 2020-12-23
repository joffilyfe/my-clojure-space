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

(split-at 5 (range 10))
; [(0 1 2 3 4) (5 6 7 8 9)]

(split-with #(<= % 10) (range 0 20 2))
; [(0 2 4 6 8 10) (12 14 16 18)]

(split-with vowel? "aeiwaeiou")
; [(\a \e \i) (\w \a \e \i \o \u)]

;; todas essas funções retornam sequências lazy.

;; 3) Sequências predicados

;; (every? pred coll)
;; Retorna true se para todos os itens da coleção o predicado
;; retornar o `true` lógico
(every? even? whole-numbers)
; false
;; Mesmo sendo uma sequência infinita, ao encontrar o primeiro número ímpar
;; a função retorna false

(every? even? '(2 4 6))
; true

;; (some pred coll)
;; Assim como a func `every?` a função some recebe
;; um predicado e uma coleção, porém ela não é um predicado
;; por si só, não há a `?` no seu nome. Porém ela é muito
;; utilizada com predicados.
(some even? '(1 2 3 4))
;; true

;; A doc (doc some) fala sobre o uso de Sets como predicados
;; algo como (some #{\a} ..), funcionaria para verificar possíveis
;; valores mas diferentemente do `filter`, nós teríamos
;; valor de resposta `valor buscado` ou `nil` (false lógico)
(some #{10} (range 100))
; 10
(some #{\a} "não há vowel A em formAto minúsculo")
; nil
(some #{\a} "não há vowel a em formato minúsculo")
; \a

;; Aqui o livro fala sobre o match lógico da função some
;; e da um exemplo para ilustrar o match lógico
(some identity [nil false 1 nil 2])
; 1

;; a função identity retorna o mesmo valor recebido,
;; sendo assim a função `some` aplica a comparação
;; lógica booleana para os valores;
;; nil é true lógico? Não, próximo
;; false é true lógico? Não, próximo
;; 1 é true lógico? Sim, retorna 1

;; (not-every? pred coll)
;; Funciona como a `every?` porém retorna true se ao menos um item for `true` lógico
(not-every? even? [1 2 4 6])
; true

;; (not-any? pred coll)
;; Retorna false se alguma verificação for true
;; exemplo even? 2 -> true, então not-every? retorna false:
;; 'Returns false if (pred x) is logical true for any x'
(not-any? even? [1 2 4 6])
; false

(not-any? even? [1 3 5])
; true

;; 4) Transformando sequências

;; (map f coll)
;; Assim como em outras linguagens, o map navega entre os elementos de
;; uma seq. retornando os elementos com alguma transformação aplicada
;; por meio de uma função.
(map #(format "<p>%s</p>" %) ["the" "quick" "brown" "fox"])
; ("<p>the</p>" "<p>quick</p>" "<p>brown</p>" "<p>fox</p>")
;; (map [f] [f coll] [f coll1 coll2] [f coll1 coll2 coll3] [f c1 c2 c3 &colls])
;; A aridade da função map é esta acima, é possível ver que podemos compor essa função
;; de diversas maneiras, com um número variável de coleções.

;; Exemplo com mais de uma coleção como entrada
(map #(format "<%s>%s</%s>" %1 %2 %1)
     ["h1" "h2" "h3" "h1"] ["the" "quick" "brown" "fox"])
; ("<h1>the</h1>" "<h2>quick</h2>" "<h3>brown</h3>" "<h1>fox</h1>")

;; (reduce f coll)
; f should be a function of 2 arguments. If val is not supplied,
; returns the result of applying f to the first 2 items in coll, then
; applying f to that result and the 3rd item, etc. If coll contains no
; items, f must accept no arguments as well, and reduce returns the
; result of calling f with no arguments.  If coll has only 1 item, it
; is returned and f is not called.  If val is supplied, returns the
; result of applying f to val and the first item in coll, then
; applying f to that result and the 2nd item, etc. If coll contains no
; items, returns val and f is not called.

(reduce + (range 1 11))
; 55

;; (sort ([coll] [comp coll]))
;; Ordena uma sequência. Se um comparador `comp` não for passado
;; a função usará o `comparator` que é um `java.util.Comparator`.
(sort (range 10 0 -1))
; (1 2 3 4 5 6 7 8 9 10)

(sort [42 1 7 11])
; (1 7 11 42)

;; utilizando um comparador como o `>`
(sort > [42 1 7 11])
; (42 11 7 1)


;; (sort-by [keyfn coll] [keyfn comp coll])
;; Returns a sorted sequence of the items in coll, where the sort
;; order is determined by comparing (keyfn item)
(sort-by #(.toString %) [42 1 7 11])
; (1 11 42 7)

;; Essa função é naturalmente útil para ordenar uma sequência de maps
;; utilizando alguma chave destes maps
(sort-by :grade > [{:grade 83} {:grade 90} {:grade 77}])
; ({:grade 90} {:grade 83} {:grade 77})

(def m [{:idade 17 :salario 3000 :nome "C"}
        {:idade 18 :salario 1000 :nome "A"}
        {:idade 19 :salario 800 :nome "B"}])

(sort-by :idade m)
; ({:idade 17, :salario 3000, :nome "C"} 
; {:idade 18, :salario 1000, :nome "A"} 
; {:idade 19, :salario 800, :nome "B"})

(sort-by :idade > m)
; ({:idade 19, :salario 800, :nome "B"} 
; {:idade 18, :salario 1000, :nome "A"} 
; {:idade 17, :salario 3000, :nome "C"})


(sort-by :salario m)
; ({:idade 19, :salario 800, :nome "B"} 
; {:idade 18, :salario 1000, :nome "A"} 
; {:idade 17, :salario 3000, :nome "C"})

(sort-by :nome m)
; ({:idade 18, :salario 1000, :nome "A"} 
; {:idade 19, :salario 800, :nome "B"} 
; {:idade 17, :salario 3000, :nome "C"})


;; Seq Comprehensions
;; Pode-se emular os comportamentos acima com o macro `for``
; (for [seq-exprs body-expr])
; (for [binding-form coll-expr filter-expr? ...] expr)
(for [word ["primeira" "segunda"]]
  (str/upper-case word))
; ("PRIMEIRA" "SEGUNDA")

(for [numero-par (range 10) :when (even? numero-par)]
  numero-par)
; (0 2 4 6 8)

;; é possível utiliza o :while como expressão de filtro, assim como o :when,
;; assim o for aqui será similar ao take-while
;; (take-while odd? '(1 2 3 4 5))
(for [n whole-numbers :while (odd? n)] n)
; (1)

;; Segundo o livro, o for é realmente útil quando queremos trabalhar com mais de um binding
;; como no exemplo a seguir.
(for [file "ABC"
      rank (range 1 9)]
  (format "%c->%d" file rank))

; ("A->1" "A->2" "A->3" "A->4" "A->5" "A->6" "A->7" "A->8" "B->1" "B->2" "B->3" "B->4" "B->5" "B->6" "B->7" "B->8" "C->1" "C->2" "C->3" "C->4" "C->5" "C->6" "C->7" "C->8")
;; Me parece muito uma forma de navegar em uma matriz.


; Clojure iterates over the rightmost binding expression in a sequence comprehension 
; first and then works its way left. Because rank is listed to the right of file in the 
; binding form, rank iterates faster. If you want files to iterate faster, you can reverse 
; the binding order and list rank first.
(for [rank (range 1 9) file "ABC"]
  (format "%c->%d" file rank))
; ("A->1" "B->1" "C->1" "A->2" "B->2" "C->2" "A->3" "B->3" "C->3" "A->4" "B->4" "C->4" "A->5" "B->5" "C->5" "A->6" "B->6" "C->6" "A->7" "B->7" "C->7" "A->8" "B->8" "C->8")


; Quando invertemos a ordem de binding o `for` passa a iterar primeiro na coleção
; `file` em vez de `rank` como no exemplo anterior

;; Lazy and Infinites Sequences
;; Boa parte das seq. em Clojure são lazy, e isso quer dizer que os elementos não
;; são calculados em tempo de definição, ou seja, eles são calculados apenas quando necessário.
;; Alguns benefícios desse comportamento:
;; 1) Você pode postergar cargas de trabalho que exigem alta capacidade computacional
;; e isso quer dizer que o trabalho só será feito se necessário (alguma chamada).
;; 2) Você pode trabalhar com uma quantidade enorme de dados que não cabem em memória. Assim
;; eles só serão carregados no momento de trabalho.
;; 3) Você pode postergar o I/O até o momento onde é necessário faze-lo

;; O livro traz um exemplo de função que opera de forma lazy em uma coleção infinita de números,
;; retorna todos os números primos (que são infinitos). Mas o livro pontua que mesmo assim
;; os dados cabem em memória quando usamos funções como `take` e `drop` já que apenas os resultados
;; mantidos ou excluídos por essas funções são retornados.
;; Seria o mesmo que executar algo como:
;; (take 1 (drop 100000000000 func-numeros-primos))
;; Assim a função `drop` excluíria os primeiros `100000000000` números primos e a `take`
;; retornaria o 100000000001 primo. A execução demoraria, claro.

;; O livro continua e fala sobre utilização de sequências preguiçosas, sobre o uso to `take`
;; para garantir que a seq. não erá avaliada completamente (o que seria ruim). Mas o livro também
;; fala que em alguns momento você pode querer SIM que uma seq. preguiçosa seja avaliada de forma prévia
;; e segundo o livro, tal situação ocorre principalmente quando a função possui algum efeito colateral
;; o que é o caso do exemplo: (def x (for [i (range 1 3)] (do (println i) i)))
(def x (for [i (range 1 3)] (do (println (str "n:" i)) i))) ;; #'user/x
;; Definimos um símbolo `x` que guarda uma seq. lazy produzida pelo macro `for`,
;; é perceptível que ao definir o código o `println` não exibe nada porque a seq não é avaliada
; préviamente. Para forçar a avaliação da seq nós podemos utilizar o `doall`

(doall x)
; n:1
; n:2
; (1 2)
;; Aqui o `doall` força que a seq seja avaliada e retorna a seq resultante

;; Podemos utilizar também o `dorun`
;; (dorun [coll] [n coll])
(def y (for [i (range 1 3)] (do (println (str "n:" i)) i))) ;; #'user/y
(dorun y)
; n:1
; n:2
; nil
;; A diferença para o `doall` é que o `dorun` não retorna a lista avaliada,
;; sendo assim, os valores computados não são salvos em memória.
;; Essas funções são utilizadas quando queremos forçar os efeitos colaterais que
;; estão empacotados por meio de uma seq. preguiçosa.

;; Clojure Makes Java Seq-able
;; As funções `first` e `rest` podem ser utilizadas em tudo que pode ter mais
;; de um item. E isso inclui os seguintes tópicos no Java:
;; 1) A API de coleções
;; 2) Expressões regulares
;; 3) A navegação pelo sistema de arquivos
;; 4) Processamento de XMLs
;; 5) Os resultados de bancos relacionais

;; 1) A API de coleções
;; "If you try to apply the sequence functions to Java collections, 
;; you’ll find that they behave as sequences. Collections that can act
;; as sequences are called seq-able. For example, arrays are seq-able"

;; O getBytes retorna uma Array de bytes
(map int (.getBytes "hello"))
; (104 101 108 108 111)

(first (.getBytes "hello"))
; 104

(last (.getBytes "hello"))
; 111

(cons (int \h) (.getBytes "ello"))
; (104 101 108 108 111)

;; O getProperties retorna uma HashTable
(first (System/getProperties))
; #object[java.util.concurrent.ConcurrentHashMap$MapEntry 0x25e49cb2 "java.specification.version=15"]

;; Strings são uma sequência de caracteres, então elas são seq-able;
(first "hello world")
; \h

(rest "hello world")
; (\e \l \l \o \space \w \o \r \l \d)


;; O clojure transforma automaticamente as coleções em sequências mas o sentido inverso não é
;; verdadeiro. Por exemplo:

(reverse "hello")
; (\o \l \l \e \h)

;; Seria natural esperar que o resultado fosse "olleh" ("hello" invertido) mas o que tivemos
;; foi uma seq. de caracteres que não foram uma string.
; Para obter o resultado esperado nós precisaríamos atuar em cima dessa nova coleção
(apply str (reverse "hello"))
; "olleh"

;; Então o livro alerta: As coleções em java são seq-able mas para a maioria dos cenários
;; elas não oferecem as facilidades que as coleções embutidas do Clojure oferecem. Dê preferência
;; pelas seqs. do Java em cenários de interoperabilidade.

;; 2) Expressões regulares
;; Não há muito o que dizer sobre o REGEX, apenas que o resultado também é uma seq. xD
;(re-seq [re s])
(re-seq #"\d" "hehe ok 1")
; ("1")

;; 3) A navegação pelo sistema de arquivos

(import 'java.io.File)
(.listFiles (File. "."))
; #object["[Ljava.io.File;" 0x10ef5fa0 "[Ljava.io.File;@10ef5fa0"]

(seq (.listFiles (File. ".")))
; (#object[java.io.File 0x59c33386 "./basics"] 
;     #object[java.io.File 0x719d35e8 "./.gitignore"] 
;     #object[java.io.File 0x2f651f93 "./.git"])

(map #(.getName %) (.listFiles (File. ".")))
; ("basics" ".gitignore" ".git")

;; Se quisermos realizar uma busca profunda em um diretório, nós podemos utilizar
;; o (file-seq [dir]) retorna uma sequência da árvore de arquivos e diretórios a partir
;; de um diretório. O diretório precisa implementar o `java.io.File`.
(map #(.getName %) (file-seq (File. ".")))
; ("." "basics" ... ".git" "config" "objects" "pack" "info" "HEAD" "info" ..  "tags")

(count (file-seq (File. ".")))
; 23

;; E se quisessemos recuperar apenas os arquivos que foram modificados recentemente?
;; Começamos escrevendo um predicado que converte os minutos para milisegundos
(defn minutes-to-millis [minutes] (* minutes 1000 60))
(defn recently-modified? [file]
  (> (.lastModified file)
     (- (System/currentTimeMillis) (minutes-to-millis 30))))

(filter recently-modified? (file-seq (File. ".")))
; (#object[java.io.File 0x6ef81f31 "./03-unifying-data-with-sequence.clj"])

;; Seq-ing a Stream
(require '[clojure.java.io :refer [reader]])

(with-open [rdr (reader "./03-unifying-data-with-sequence.clj")]
  (count (line-seq rdr)))
; 596

;; Podemos também, por exemplo, filtrar e contar apenas as linhas com conteúdo
(with-open [rdr (reader "./03-unifying-data-with-sequence.clj")]
  (count (filter #(re-find #"\S" %) (line-seq rdr))))
; 468

;; Munidos desse novo conhecimento, o livro propõe que criemos um programa para
;; buscar arquivos fonte clojure e que contemos a quantidade de linhas não brancas,
;; e retornemos a soma total de todos os arquivos.
(use '[clojure.java.io :only (reader)])
(use '[clojure.string :only (blank?)])

(defn non-blank-line? [line] (not (blank? line)))
(defn clojure-source? [file] (.endsWith (.toString file) ".clj"))
(defn clojure-loc [base-file]
  (reduce
   +
   (for [file (file-seq base-file)
         :when (clojure-source? file)]
     (with-open [rdr (reader file)]
       (count (filter non-blank-line? (line-seq rdr)))))))

(clojure-loc (File. "."))
; 825

;; Pra cada arquivo encontrado pela função `file-seq` nós verificamos se ele termina com
;; `.clj`, se sim nós abrimos o arquivo, lemos as linhas com `line-seq`, verificamos se
;; a linha não é branca e ai contamos todas as linhas filtradas. Logo depois o resultado é
;; computado pela função (reduce +)

;; A princípio essa função não é natural para mim, o pensamento procedural ainda me pega e
;; escrever essas funções sem ajuda de ferramentas que auxiliem no rastreio dos parênteses
;; é massante.

;; Calling Structure-Specific Functions

;; Olhando em retrospectiva nós estamos criando código para sequências e em geral o código
;; deve funcionar para qualquer estrutura utilizadas (vec map set), porém às vezes é preciso
;; escrever um código mais específico e que seja mais performático.
;; Veremos então algumas funções específicas para as estruturas.

;; Funções para listas

;; (peek [coll])
;; Retorna o primeiro elemento de uma lista, assim como o `first` mas é muito mais rápido para
;; vetores.
(peek [1 2 3])
; 1

;; (pop [coll])
;; Retorna todos os elementos de uma lista sem incluir o último elemento.
;; Lança uma exceção se a lista estiver vazia.
(pop '())
; Execution error (IllegalStateException) at user/eval158 (REPL:1).
; Can't pop empty list
(pop [1 2 3])
; [1 2]

;; Funções para vetores
;; Como já vimos, pop e peek também funcionam para vetores, agora veremos outras funções.

;; (get [map key] [map key not-found])

;; Acessa um índice de um vetor
(get [:a :b :c] 1)
; :b

; Acessa um índice inexistente e retorna nil
(get [:a :b :c] 5)
; nil

;; (assoc~iate~ [map key val] [map key val & kvs])
;; Substitui um valor em uma determinada posição de um vetor
(assoc [1 2] 0 :one)
; [:one 2]

;; (subvec [v start] [v start end])
;; Retorna uma fatia do vetor com base nos índices de start e fim passados.
;; O índice final é não inclusivo.
(subvec [0 1 2 3 4 5] 4)
; [4 5]
(subvec [0 1 2 3 4 5] 2 4)
;; [2 3]

;; Lança uma exceção se o índice final for maior do que o tamanho do vetor.
;; IndexOutOfBoundsException
;; (subvec [0 1 2 3 4 5] 4 100)

;; É possível simular o mesmo comportamento com funções
;; mais generalistas como o take e o drop. Porém o subvec trabalha em tempo O(1)
;; o que é muito rápido para essa estrutura.
(take 2 (drop 4 [0 1 2 3 4 5]))

;; Funções para maps

;(keys [map])
;; Retorna uma sequência com todas as chaves do mapa
(def me {:first-name "joffily" :last-name "F"})

(keys me)
; (:first-name :last-name)

; (vals [map])
;; Retorna os valores do mapa
(vals me)
; ("joffily" "F")

; (get [map key] [map key not-found])
;; Assim como para as listas e vetores, o get acessa o mapa mas utiliza uma chave para tal.
(get me :first-name "not-found")
; joffily

;; Também é possível passar um valor padrão para quando uma chave não existe
(get me :mid-name "not-found")
; "not-found"

;; Também é possível acessar uma chave sem utilizar a função `get`.
;; O livro detalha que os mapas são funções de suas chaves (?)
(me :first-name)
; "joffily"

;; Também fala que as `keywords` são funções como os mapas
(:first-name me)
; "Joffily"

(:mid-name me)
; nil

;; (contains? [map])
;; É uma função predicado que verifica se uma chave existe em um mapa.
(def me {:first-name "joffily" :last-name "F" :age nil})
(get me :age)
; nil
;; Utilizando o (get) nós podemos achar que uma chave não existe mas na verdade o seu valor é nil
;; se setassemos o valor padrão poderíamos saber se ela existe ou não mas a solução começa a ficar
;; complicada apenas para checar algo. Sem falar que o valor retornado pode ser um false lógico.
;; Contains ao resgate:

(contains? me :age)
; true

(contains? me :mid-name)
; false

;; Ainda temos funções para manipulação de mapas como:
;; 1) assoc
;; 2) dissoc
;; 3) select-keys
;; 4) merge

;; (assoc [map key val & kvs])
;; Associa chaves e valores a um mapa
(assoc me :mid-name "S")
; {:first-name "joffily", :last-name "F", :age nil, :mid-name "S"}

; (dissoc [map] [map key] [map key & ks])
;; Disassocia chaves de um mapa
(dissoc me :last-name :age)
; {:first-name "joffily"}

; (merge [& maps])
;; Combina os mapas. Se alguma chave for repetida a que valerá é a do mapa
;; mais a direita
(merge me {:where "SP"})
; {:first-name "joffily", :last-name "F", :age nil, :where "SP"}

(merge me {:where "SP"} {:where "PB"})
; {:first-name "joffily", :last-name "F", :age nil, :where "PB"}

; (merge-with [f & maps])
;; Que tal associar vários mapas e não perder valores de chaves repetidas?
;; Com o merge-with nós podemos passar uma função que decidirá como tratar esses casos.
;; No exemplo abaixo nós usamos a func. concat para concatenar as strings
(merge-with concat me {:where "SP"} {:where "PB"})
; {:first-name "joffily", :last-name "F", :age nil, :where (\S \P \P \B)}

(merge-with #(str %1 %2) me {:age 1} {:age 2})
; {:first-name "joffily", :last-name "F", :age "12"}

;; Funções para Sets
(require '[clojure.set :as setfns])
(def group-1 #{\a \b \c \d})
(def group-2 #{\a \d \e \f})

;; (union [] [s1] [s1 s2] [s1 s2 & sets])
(setfns/union group-1 group-2)
; #{\a \b \c \d \e \f}

;; (difference [s1] [s1 s2 & sets])
;; Retorna os elementos do primeiro set que não combinam com os elementos do segundo set
(setfns/difference group-1 group-2)
; #{\b \c}

(setfns/difference group-2 group-1)
; #{\e \f}

;; (intersection [s1] [s1 s2 & sets])
;; Retorna a inserseção dos sets
(setfns/intersection group-1 group-2)
; #{\a \b}

;; (select [pred xset])
;; Retorna os elementos do set em que o prédicado retorna true
(setfns/select #(= 1 (count %)) #{"nome" "sobrenome" "m" "1"})
; #{"1" "m"}

;; Os sets junto das funções disponíveis em clojure.set podem ser muito
;; úteis e poderesos para manusear dados. É possível até simular um sistema
;; SQL com a lógica relacional empregada por essa estrutura de dados e suas funções.
;; O livro traz alguns exemplos.

; obras
(def compositions
  #{{:name "The Art of the Fugue" :composer "J. S. Bach"}
    {:name "Musical Offering" :composer "J. S. Bach"}
    {:name "Requiem" :composer "Giuseppe Verdi"}
    {:name "Requiem" :composer "W. A. Mozart"}})

; compositores
(def composers
  #{{:composer "J. S. Bach" :country "Germany"}
    {:composer "W. A. Mozart" :country "Austria"}
    {:composer "Giuseppe Verdi" :country "Italy"}})

; países
(def nations
  #{{:nation "Germany" :language "German"}
    {:nation "Austria" :language "German"}
    {:nation "Italy" :language "Italian"}})

; clojure.set/rename ([xrel kmap])
; Returns a rel of the maps in xrel with the keys in kmap renamed to the vals in kmap
(setfns/rename compositions {:name :title})

;; Seria mais ou menos com um `AS` de um select
; #{{:composer "Giuseppe Verdi", :title "Requiem"}
;   {:composer "W. A. Mozart", :title "Requiem"} 
;   {:composer "J. S. Bach", :title "The Art of the Fugue"} 
;   {:composer "J. S. Bach", :title "Musical Offering"}}

;; clojure.set/select ([pred xset])
;; Returns a set of the elements for which pred is true
;; Funciona como um WHERE
(setfns/select #(= (:country %) "Germany") composers) ;foda!
; #{{:composer "J. S. Bach", :country "Germany"}}

;; clojure.set/project ([xrel ks])
;; Returns a rel of the elements of xrel with only the keys in ks
;; Faz a projeção de chaves de um mapa. É como o SELECT do SQL e a projeção do MongoDB
(setfns/project nations [:language])
; #{{:language "German"} {:language "Italian"}}

;; O livro fala então da última primitiva que é importante para a teória dos conjuntos
;; que é o full-cross-relational onde nós temos todas as relações possíveis entre os
;; sets passados.
;; Podemos obter isso através do for
(for [m compositions c composers] (concat m c))
; (([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "W. A. Mozart"] [:country "Austria"]))

(for [m #{{:a 1 :b 1}} c #{{:c 2 :d 2} {:e 3 :f 3}}] (concat m c))
; (([:a 1] [:b 1] [:e 3] [:f 3]) ([:a 1] [:b 1] [:c 2] [:d 2]))

;; Acho que isso é implementado no Clea entre as afiliações e as referências.

;; Mais interessante do que isso é poder fazer o JOIN do select com base em alguma chave
; clojure.set/join([xrel yrel] [xrel yrel km])

(setfns/join compositions composers)
; #{{:composer "W. A. Mozart", :country "Austria", :name "Requiem"} {:composer "J. S. Bach", :country "Germany", :name "Musical Offering"} {:composer "Giuseppe Verdi", :country "Italy", :name "Requiem"} {:composer "J. S. Bach", :country "Germany", :name "The Art of the Fugue"}}
;; Aqui o clojure olhou para as chaves que combinam nos dois sets, neste caso é a :composer
;; Se as chaves não tiverem o mesmo nome nós podemos criar uma relação mesmo assim

(setfns/join composers nations {:country :nation})
; #{{:composer "W. A. Mozart", :country "Austria", :nation "Austria", :language "German"}
;   {:composer "J. S. Bach", :country "Germany", :nation "Germany", :language "German"}
;   {:composer "Giuseppe Verdi", :country "Italy", :nation "Italy", :language "Italian"}}

;; E pra fechar, o livro demonstra como fazer um select com uma projeção, utilizando JOIN e where
;; Mostramos apenas os países dos compositores que escreveram uma composição de nome "Requiem"
(setfns/project
 (setfns/join
  (setfns/select #(= (:name %) "Requiem") compositions)
  composers)
 [:country])
;  #{{:country "Italy"} {:country "Austria"}}
