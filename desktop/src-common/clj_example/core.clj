(ns clj-example.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(declare clj-example title-screen main-screen text-screen)

(defn- create-enemy []
  (println "Create Enemy"))

(defn- create-friend []
  (println "Create Friend"))

(defn- move [entity direction]
  (case direction
    :down (update entity :y dec)
    :up (update entity :y inc)
    :left (update entity :x dec)
    :right (update entity :x inc)
    nil))

(defscreen text-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (assoc (label "0" (color :white))
            :id :fps
            :x 5))

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (label! :set-text (str (game :fps))))
             entity))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (height! screen 800))
  )

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (add-timer! screen :spawn-enemy 3)
    (add-timer! screen :spawn-friend 1)
    (update! screen :renderer (stage) :camera (orthographic))
    (let [icon (assoc (texture "Clojure_logo.gif") :x 50 :y 50 :width 100 :height 100 :player? true :angle 45 :origin-x 50 :origin-y 50)]
      [icon]))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :spawn-enemy (conj entities (create-enemy))
      :spawn-friend (conj entities (create-friend))
      nil))

  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :f5)) (on-gl (set-screen! clj-example-game main-screen))
      (= (:key screen) (key-code :w)) (move (first entities) :up)
      (= (:key screen) (key-code :s)) (move (first entities) :down)
      (= (:key screen) (key-code :d)) (move (first entities) :right)
      (= (:key screen) (key-code :a)) (move (first entities) :left)))

  :on-touch-down
  (fn [screen entities]
    (cond
      (> (game :y) (* (game :height) (/ 2 3))) (move (first entities) :up)
      (< (game :y) (/ (game :height) 3)) (move (first entities) :down)
      (> (game :x) (* (game :width) (/ 2 3))) (move (first entities) :right)
      (< (game :x) (/ (game :width) 3)) (move (first entities) :left)))

  :on-resize
  (fn [screen entities]
    (height! screen 600))
  )

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!))

  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :f5)) (on-gl (set-screen! clj-example-game main-screen))))
  )

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! clj-example-game blank-screen)))))

(defgame clj-example-game
  :on-create
  (fn [this]
    (set-screen! this main-screen text-screen))
  )


(-> main-screen :entities deref)
