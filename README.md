# Clisk

Clisk is a Clojure based DSL/library for procedural image generation.

Here is the typical use case:

 - The input is a description of a procedure / formula for generating an image
 - The output is a image rendered at a resolution of your choice

Clisk is intended to be used from Clojure in a REPL environment, but can also with a little effort be used from Java.

[![Build Status](https://secure.travis-ci.org/mikera/clisk.png)](http://travis-ci.org/mikera/clisk)

## Example code and resulting image

    ;; Create a Voronoi map based on a mathematical function
    (def vblocks 
      (v* 5.0 
          (voronoi-function 
            `(Math/sqrt (- (* ~'y ~'y) (* ~'x ~'x))))))

    ;; Render an texture using the Voronoi map as a height-field
    (show (render-lit 
            (seamless vplasma) 
            (v+ (v* 0.2 (seamless 0.2 (rotate 0.1 plasma))) 
                (v* 0.6 vblocks))))

![Voronoi rocks](https://raw.github.com/wiki/mikera/clisk/images/VoronoiRocks.png)

For [more examples see the Wiki](https://github.com/mikera/clisk/wiki)

## Installation

The best way to get started with clisk is to [install it from Clojars](https://clojars.org/net.mikera/clisk) using either leiningen or Maven.

Once you have Clisk specified as a dependency, you should be able to get going with the key functionality as follows:

    (ns my-namespace
      (:use [clisk core functions patterns colours]))
     
    (show (checker red white))

## Features

* A concise DSL for specifying image generators through function composition
* Multi-dimensional texture generation (e.g. 4D textures including time dimension for animations) 
* Fast image synthesis thanks to compiled image generation functions (typically sub-second generation 256*256 4x antialiased textures)
* Anti-aliasing (arbitrary precision)
* A wide variety of patterns and transforms, e.g. Voronoi maps, Perlin Noise
* Easily extensible with your own image generation functions
* Ability to render surfaces with shading based on 3D heightmaps


![Plasma Globe](https://raw.github.com/wiki/mikera/clisk/images/PlasmaGlobe.png)
