(ns midje.sweet.t-background-nesting
  (:use clojure.test)
  (:use [midje.sweet] :reload-all)
  (:use [midje.test-util])
  (:use clojure.contrib.pprint))

;; This is a separate file because we're making namespace-wide changes

(unfinished outermost middlemost innermost)

(deftest background-command-slams-new-background-in-place
  (in-separate-namespace
   (background (outermost ...o...) => 1)
   (fact (+ 1 (outermost ...o...)) => 2)
   (background (outermost ...o...) => -1)
   (fact (+ 1 (outermost ...o...)) => 0)))

(deftest background-command-is-shadowed-by-against-background
  (in-separate-namespace
   (background (outermost) => 2
	       (middlemost) => 'a)
   (against-background [ (middlemost) => 33]
		       (fact (+ (middlemost) (outermost)) => 35))))
  

(deftest three-levels-of-nesting
  (in-separate-namespace
   (background (outermost) => 2
	       (middlemost) => 'a)
   (against-background [ (middlemost) => 33
			 (innermost) => 'c]
		       (fact
			 (against-background (innermost) => 8)
			 (+ (middlemost) (outermost) (innermost)) => 43))))

(against-background [ (middlemost) => 33 ]
  (deftest spanning-deftest-does-not-work
      (fact
      (against-background (innermost) => 8)
      (+ (middlemost) (innermost)) => (throws java.lang.Error))))
     
		      
