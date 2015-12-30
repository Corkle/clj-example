(ns clj-example.core.desktop-launcher
  (:require [clj-example.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. clj-example-game "clj-example" 1500 800)
  (Keyboard/enableRepeatEvents true))
