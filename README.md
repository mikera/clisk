# Clisk


Clisk is a toolkit for procedural image generation (Clisk = Clojure Image Synthesis Kit)

Features:

* A concise DSL for specifying image generators through function composition
* Up to 4D texture generation (including time dimension for animations) 
* Fast image synthesis thanks to compiled image generation functions (typically sub-second generation 256*256 4x antialiased textures)
* Anti-aliasing (arbitrary precision)
* A wide variety of patterns and transforms
* Easily extensible with your own image generation functions