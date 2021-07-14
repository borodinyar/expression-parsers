; ------------------ functional ------------------
(def div (fn
           ([x] (div 1.0 x))
           ([x & args] (/ x (double (apply * args))))))

(defn create-abstract-operation [op]
  (fn [& operands]
    (fn [var] (apply op (mapv #(% var) operands)))))

(defn variable [name] #(% name))
(def constant constantly)

(def add (create-abstract-operation +))
(def subtract (create-abstract-operation -))
(def multiply (create-abstract-operation *))
(def divide (create-abstract-operation div))

(def sum add)
(def avg (create-abstract-operation #(/ (apply + %&) (count %&))))

(def negate subtract)

(def fun-operations
  {'+      add
   '-      subtract
   '*      multiply
   '/      divide
   'negate negate
   'sum    sum
   'avg    avg})


(defn parse-expr [operations const variable]
  (letfn [(parse [expr]
            (cond
              (number? expr) (const expr)
              (symbol? expr) (variable (name expr))
              (list? expr) (apply (operations (first expr))
                                  (mapv parse (rest expr)))))]
    (fn [expression]
      (parse (read-string expression)))))


(def parseFunction (parse-expr fun-operations constant variable))

; ------------------ object ------------------
(load-file "proto.clj")

(def evaluate (method :evaluate))
(def toString (method :toString))
(def toStringInfix (method :toStringInfix))
(def diff (method :diff))

(declare ZERO)

(def Constant-prototype
  (let [number (field :value)]
    {
     :toString      (fn [this] (format "%.1f" (double (number this))))
     :toStringInfix toString
     :evaluate      (fn [this _] (number this))
     :diff          (fn [_ _] ZERO)
     }))

(defn Constant [number]
  {:prototype Constant-prototype
   :value     number})

(def ZERO (Constant 0))
(def ONE (Constant 1))

(defn getFirstElement [argName] (clojure.string/lower-case (first argName)))
(def Variable-prototype
  (let [name (field :name)]
    {
     :toString      (fn [this] (name this))
     :toStringInfix toString
     :evaluate      (fn [this id] (id (getFirstElement (name this))))
     :diff          (fn [this id]
                      (if (= (getFirstElement (name this)) id) ONE ZERO))
     }))

(defn Variable [name]
  {:prototype Variable-prototype
   :name      name})

(defn Operation-prototype [eval toStr toStrInf diff]
  {:evaluate      eval
   :toString      toStr
   :toStringInfix toStrInf
   :diff          diff})

(defn operationConstructor [this & operands]
  (assoc this
    :operands operands))

(defn create-operation [function name diffFunction]
  (let [operands (field :operands)]
    (constructor operationConstructor
                 (Operation-prototype
                   (fn [this values] (apply function (map #(evaluate % values) (operands this))))
                   (fn [this]
                     (str "(" name " " (clojure.string/join " " (mapv toString (operands this))) ")"))
                   (fn [this]
                     (if (= 1 (count (operands this))) (str name "(" (toStringInfix (first (operands this))) ")")
                                                       (str "(" (toStringInfix (first (operands this))) " " name " "
                                                            (clojure.string/join (str " " name " ") (map toStringInfix (rest (operands this)))) ")")))
                   (fn [this d] (diffFunction (operands this) (mapv #(diff % d) (operands this))))))))

(declare Add)

(defn add-sum-diff [args dargs] (apply Add dargs))

(def Add (create-operation +
                           "+"
                           add-sum-diff
                           ))

(def Subtract (create-operation -
                                "-"
                                (fn [args dargs] (apply Subtract dargs))
                                ))

(def Negate (create-operation -
                              "negate"
                              (fn [args dargs] (apply Negate dargs))
                              ))

(declare Multiply)

(defn multiply-diff [args dargs] (last (reduce (fn [[a da] [b db]]
                                                 [(Multiply a b) (Add (Multiply a db) (Multiply b da))])
                                               (apply mapv vector [args dargs]))))

(def Multiply (create-operation *
                                "*"
                                multiply-diff
                                ))

(def Divide (create-operation div
                              "/"
                              (fn [[x & xs] [dx & dxs]]
                                (if (== (count xs) 0)
                                  (Divide (Negate dx) (Multiply x x))
                                  (let [dmul (multiply-diff xs dxs) mul (apply Multiply xs)]
                                    (Divide (Subtract
                                              (Multiply dx mul) (Multiply x dmul)) (Multiply mul mul)))))
                              ))

(def Sum (create-operation +
                           "sum"
                           add-sum-diff
                           ))

(def Avg (create-operation #(/ (apply + %&) (count %&))
                           "avg"
                           (fn [args dargs] (Divide (apply Add dargs) (Constant (count args))))))


(def IPow (create-operation #(Math/pow %1 %2)
                            "**"
                            nil))

(def ILog (create-operation (fn [x y] (/ (Math/log (Math/abs y)) (Math/log (Math/abs x))))
                            "//"
                            nil))

(def object-operations {'+            Add
                        '-            Subtract
                        '*            Multiply
                        '/            Divide
                        'negate       Negate
                        'sum          Sum
                        'avg          Avg
                        (symbol "**") IPow
                        (symbol "//") ILog
                        })

(def parseObject (parse-expr object-operations Constant Variable))

;(println (Constant 10.0))
;(println (toString (Constant 10.0)))
;(println (toString (diff (Constant 10.0) "x")))
;(println "------------")
;(println (Variable "x"))
;(println (toString (Variable "x")))
;(println (toString (diff (Variable "x") "x")))
;(println "------------")
;(println (toString (Add (Variable "x") (Constant 2.0))))
;(evaluate (parseObject "(+ x 2.0)"){"z" 0.0, "x" 0.0, "y" 0.0})
;(print (evaluate (diff (Add (Constant 1) (Constant 2) (Constant 3))){}))
;(print (evaluate (diff (Multiply (Variable "x") (Constant 2.0) (Constant 3.0)) "x"){"x" 1}))
;(print (evaluate (diff (Divide (Negate (Variable "x")) (Constant 2.0)) "x"){"z" 1.0, "x" 1.0, "y" 1.0}));
;(print (toString (diff (Add (Variable "x") (Constant 2.0)) "x")))
;(print ((evaluate (divide (variable "x"))){"z" 0.0, "x" 0.0, "y" 0.0}))
;(println (toString (Divide (Constant 1.0))))
;(println (toString (diff (Multiply (Subtract (Variable "z") (Variable "y")) (Negate (Variable "x"))) "y")))
;(+ (* (- 0,0 1,0) (/ (* (- z y) (negate x)) (- z y))) (* (negate 0,0) (/ (* (- z y) (negate x)) (negate x))))
;-------------------------
;(println (toString (diff (Add (Variable "x") (Constant 2.0)) "x")))
;(println (toString (diff (Divide (Variable "x")) "x")))


; ------------------ combinatorial ------------------
(load-file "parser.clj")

(defn +word [p] (apply +seqf str (mapv (comp +char str) p)))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *open (+char "("))
(def *close (+char ")"))

(def *digits (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digits))))
(def *double (+map read-string (+seqf str (+opt (+char "-")) *number (+char ".") *number)))

(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))

(def *constant (+map Constant (+seqn 0 *ws (+or *double *number))))
(def *long-letter (+str (+plus (+char "XYZxyz"))))
(def *variable (+map (comp Variable str) *long-letter))

(declare *expression)
(declare *unary-operation)

(def *parse-brackets (+seqn 1 *open *ws (delay *expression) *ws *close))

(defn *operations [operations-list] (+map (comp object-operations symbol)
                                          (+str (apply +or (mapv +word operations-list)))))

(def *unary (delay (+or *unary-operation
                        *variable
                        *constant
                        *parse-brackets)))

(def *unary-operation (+map (fn [[operation x]] (operation x)) (+seq (*operations (list "negate")) *ws *unary)))

(defn oper-fold [is-left]
  (fn [args] (let [apply-oper (fn [x [operation y]] (if is-left (operation x y)
                                                                (operation y x)))
                   arguments (if is-left args
                                         (reverse args))]
               (reduce apply-oper (first arguments) (partition 2 (rest arguments))))))

(defn *do-operation [act operations is-left]
  (+map (oper-fold is-left)
        (+seqf cons *ws act
               (+map (partial apply concat) (+star (+seq *ws (*operations operations) *ws act)))
               *ws)))

(def *pow-log (*do-operation *unary (list "//" "**") false))
(def *div-mul (*do-operation *pow-log (list "/" "*") true))
(def *sub-add (*do-operation *div-mul (list "-" "+") true))

(def *expression *sub-add)

(def parseObjectInfix
  (+parser (+seqn 0 *ws *expression *ws)))
;(println (toStringInfix (parseObjectInfix
;(declare *expr)
;(def *value (delay (+or *constant *variable *expr)))
;(def *args (+seqn 0 *ws *value *ws))
;(def *unary (+map (partial object-operations) (longOperation "negate")))
;(def *binary (+map (partial object-operations) (+char "+-/*")))
;(def binarySuffix (fn [[first second op]] (op first second)))
;(def unarySuffix (fn [[arg op]] (op arg)))
;(defn getPrefix [func expr] (+map func expr))
;(def *binaryExpr (getPrefix binarySuffix (+seq *args *args *binary)))
;(def *unaryExpr (getPrefix unarySuffix (+seq *args *unary)))
;(def *expr
;  (+seqn 1 *skipBracket *ws (+or *binaryExpr *unaryExpr) *ws *skipBracket *ws))
;(def parseObjectSuffix (+parser *args))
;----
;(println (toStringSuffix (parseObjectSuffix "(2.0 2.0 +)")))
;(println (toStringSuffix (parseObjectSuffix "( ( 2 x * ) 3 - )")))
;(println (toStringInfix (parseObjectInfix "(x + 2.0)")))
;(println (toStringInfix (parseObjectInfix "( 3.0   - y )")))
;(println toStringSuffix (parseObjectSuffix "(x 2.0 +)"))
;(println (toStringInfix (parseObjectInfix "Xxzy")))