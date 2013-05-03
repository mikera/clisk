# Clisk

Clisk is a Clojure based DSL/library for procedural image generation.

You can use it for:

 - Creating 2D material textures for games
 - Building fractal images / artwork
 - Generating 3D or 4D textures for raytracing (e.g. in Enlight: https://github.com/mikera/enlight)
 - Creating patterns (e.g. randomly generated maps)

The core operation of Clisk is taking an image description using the Clisk DSL as input and
creating a bitmap images as output. You can create images of arbitrary size as long as they fit in memory.

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

    (use 'clisk.live)
     
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


### License

Clisk is open source, licensed under the GNU Lesser General Public License (LGPL)

 - http://www.gnu.org/licenses/lgpl.html
