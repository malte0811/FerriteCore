This file tries to explain the changes done in FerriteCore, and how much memory they save. The saved memory for the
first 4 points refers to a ForgeCraft 1 instance around 19th December 2020, after that to version 1.2.0 of the (smaller)
1.16.4 Direwolf20 pack. This is mostly because FC 1 uses [ServerPackLocator](https://github.com/cpw/serverpacklocator/),
which is great for rapid mod updates, but also makes reproducible tests near-impossible (and means that I can't test
when the server is down :smile:)

### 1. Optionals in `KeyValueCondition`

This change is made obsolete by the 4th point, it is only included in this list for completeness.

The vanilla implementation contains code along these lines:

```java
Optional<T> opt=newlyCreatedOptional();
if(!opt.isPresent()){
    // Something
}else{
    return()->doThing(opt.get());
}
```

The created lambda is kept around for a long time, and there are a few million of them. In
total the shallow size (i.e. the equivalent to `sizeof` in C/C++) of the captured
`Optional`s is about 100 MB. By replacing the `else`-branch with
```java
T unwrapped = opt.get();
return () -> doThing(unwrapped);
```
the `Optional`s can be GCd right away.

Saved memory: 100 MB  
CPU impact: zero or negative (one less pointer to follow)  
Side: client  

### 2. BlockState neighbors

To implement `StateHolder#with` (mostly seen as `BlockState#with`) a state needs to be able to quickly find its "
neighbor states". In vanilla this is implemented using a
`Table<Property<?>, Comparable<?>, S>` for each state. In total these tables use about 600 MB of memory.
Asymptotically (which is relevant for complex blocks in practice) the memory usage for a block
is `O((number of states) * sum(number of values per property))`. By replacing these with one `FastMap` per block this
can be reduced to `O(number of states)`, with a similar coefficient. A `FastMap` in this case is simply an `ArrayList`
used as a multi-dimensional array. Finding a neighbor state can be done by a few integer modulo, multiplication and
addition operations. Alternatively it can be implemented using bitmasks, for slightly improved performance at slightly
higher memory usage (this is the default starting in version 2.0).

Saved memory: Around 600 MB (the `FastMap`s are around 7 MB total)  
CPU impact: hard to prove, but most likely near zero  
Side: both  
Mixin subpackage: `fastmap`

### 3. BlockState property storage
Each blockstate stores its properties as an `ImmutableMap<Property<?>, Comparable<?>>`, which takes around 170 MB in
total. Replacing this `ImmutableMap` by a custom implementation based on the `FastMap` from the previous point (loaded
with some classloader trickery) removes most of that memory usage.

Saved memory: Around 170 MB  
CPU impact: probably around zero  
Side: both  
Mixin subpackage: None (implemented as part of the `fastmap` code)  
Notes: If this is ever included in Forge the custom `ImmutableMap` should probably be replaced by a regular map, and a
new `getValues` method returning a `map` rather than an `ImmutableMap` should be added

### 4. Multipart model predicate caching

Each multipart model stores a number of predicates to determine which parts to show under what conditions. These
predicates take up 300-400 MB. However in many cases these predicates are checking the same thing, they are just newly
created every time. For
`KeyValueCondition` the predicates can be cached by using the property and its value as a key, for `And/OrCondition` (
and multi-value `KeyValueCondition`s) the key is the list of input predicates sorted by hash value.  
One detail that makes this even more effective is that a block can never have two properties that are equal according
to `equals`, while the common property implementations include `equals`. Additionally `StateHolder#get` also
considers `equals` (as opposed to reference equality), so using the same lambda for equivalent (but non
reference-equivalent) properties and values is actually possible. This is particularly useful as one of the most common
usages of multipart models is pipes, where the states are nearly always boolean properties named `north` etc. As a
result the number of predicates is reduced from between 10s of thousands and millions to a few ten or hundred instances.

Saved memory: 300-400 MB (relative to the state after the first change, so 100 MB more compared to a "clean" instance)  
CPU impact: Some impact in model loading (but less allocations), zero while playing  
Side: client  
Mixin subpackage: `predicates`

### 5. String instance reduction in `ModelResourceLocation`

The `ModelResourceLocation` constructor accepting a `ResourceLocation` and a `BlockState`
(the main one used in practice) is implemented by first converting the RL to a string and then splitting it again. This
is not only a waste of CPU time, it also means that new
`String` instances are created for the path and namespace of the MRL.  
Another optimization is to deduplicate the `variant` string of the MRL, i.e. to use the same `String` instance for all
MRLs with a given `variant`.

Saved memory: about 300 MB (DW20 pack version 1.2.0)  
CPU impact: Zero or negative for the first part, slight (<1s) during loading for the second part  
Side: client  
Mixin subpackage: `mrl`  
Note: The CPU impact of the current Mixin implementation is positive for both parts, because a negative impact for the
first part would require changing what constructor the constructor in question redirects to.

### 6. Multipart model instances
By default every blockstate using a multipart model gets its own instance of that multipart model. Since multipart
models are generally used for blocks with a lot of states this means a lot of instances, and a lot of wasted memory. The
only input data for a multipart model is a `List<Pair<Predicate<BlockState>, IBakedModel>>`. The predicate is already
deduplicated by point 4, so it is very easy to use the same instance for equivalent lists. This reduces the number of
instances from about 200k to 1.5k (DW20 1.2.0).

Saved memory: Close to 200 MB  
CPU impact: Slight during loading, zero at runtime  
Side: client  
Mixin subpackage: `dedupmultipart`

### 7. Blockstate cache deduplication

Blockstates that are not marked as "variable opacity" cache their collision and render shapes. This uses around 200 MB,
mostly just because there are a lot of blockstates. Many blocks have the same shapes, so in a lot of cases the existing
instances can be reused. There is an additional problem with this: Many mods cache their own render/collision shapes
internally, even if they're cached by the vanilla cache. This can be worked around by replacing the "internals" of the
voxel shape returned by the block with the internals of the "canonical" instance of that shape. Vanilla assumes that
shapes are immutable once created, so this should be safe. To slightly reduce the impact while joining the code first
checks if the state already had an initialized cache and reuses shapes from that cache if they match. This saves a map
lookup for virtually all states.

Saved memory: Around 200 MB  
CPU impact: Some during loading and joining (<1 second with the DW20 pack), none afterwards  
Side: both  
Mixin subpackage: `blockstatecache`

### 8. Quad deduplication

Baked quads (especially their `int[]`s storing vertex data) account for around 340 MB in total, a lot of which is simply
necessary to store the models. But it is also common for multiple quads to have the same data. Using the same `int[]`
instance for quads with the same data reduces the amount of memory used by quads to around 195 MB. This is not
technically 100% safe to do, since the `int[]` can theoretically be modified at any time. Only applying the optimization
to quads used in `SimpleBakedModel`s is probably a good compromise for this.

Saved memory: Close to 150 MB  
CPU impact: Some during model loading, none afterwards  
Side: client  
Mixin subpackage: `bakedquad`
