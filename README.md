# Clisk


Clisk is a DSL library for procedural image generation that can be used from Clojure and Java.

## Example code and resulting image

    (show (render-lit 
            (seamless vplasma) 
              (v+ (v* 0.2 (seamless 0.2 (rotate 0.1 plasma))) 
                  (v* 0.6 vblocks))))

![Voronoi rocks](https://raw.github.com/wiki/mikera/clisk/images/VoronoiRocks.png)

For [more examples see the Wiki](https://github.com/mikera/clisk/wiki)

## Installation

The best way to get started with clisk is to [install it from Clojars](https://clojars.org/net.mikera/clisk) using either leiningen or Maven.

Once you have clisk as a dependency, you should be able to get going with the key functionality as follows:

    (ns my-ns
      (:use [clisk core functions patterns colours]))
     
    (show (checker red white))

## Features

* A concise DSL for specifying image generators through function composition
* Multi-dimensional texture generation (e.g. 4D textures including time dimension for animations) 
* Fast image synthesis thanks to compiled image generation functions (typically sub-second generation 256*256 4x antialiased textures)
* Anti-aliasing (arbitrary precision)
* A wide variety of patterns and transforms
* Easily extensible with your own image generation functions
* (In development) ability to render 3D lit surfaces

![Plasma Globe](https://raw.github.com/wiki/mikera/clisk/images/PlasmaGlobe.png)
