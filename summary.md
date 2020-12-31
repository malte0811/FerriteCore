This file tries to explain the changes done in FerriteCore, and how much memory they save. The saved memory for the
first 4 points refers to a ForgeCraft 1 instance around 19th December 2020, after that to version 1.2.0 of the (smaller)
1.16.4 Direwolf20 pack. This is mostly because FC 1 uses [ServerPackLocator](https://github.com/cpw/serverpacklocator/),
which makes reproducible tests near-impossible (and means that I can't test when the server is down :smile:)

### 1. Optionals in `PropertyValueCondition`

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
To implement `StateHolder#with` (mostly seen as `BlockState#with`) a state needs to be
able to quickly find its "neighbor states". In vanilla this is implemented using a
`Table<Property<?>, Comparable<?>, S>` for each state. In total these tables use about 600
MB of memory. Asymptotically (which is relevant for complex blocks in practice) the memory
usage for a block is `O((number of states) * sum(number of values per property))`. By
replacing these with one `FastMap` per block this can be reduced to `O(number of states)`,
with a similar coefficient. A `FastMap` in this case is simply an `ArrayList` used as a
multi-dimensional array. Finding a neighbor state can be done by a few integer modulo,
multiplication and addition operations.

Saved memory: Around 600 MB (the `FastMap`s are around 7 MB total)  
CPU impact: hard to prove, but most likely near zero  
Side: both  
Mixin subpackage: `fastmap`  

### 3. BlockState property storage
Each blockstate stores its properties as an `ImmutableMap<Property<?>, Comparable<?>>`,
which takes around 170 MB in total. Most operations do not actually require this map, they
can be implemented with similar speed using the `FastMap` from the previous point.  There
is one problematic exception: `getValues`. This is a simple getter for the property map in
vanilla, if the map is no longer stored it needs to be created on the fly. The method
returns an `ImmutableMap`, which can't be easily extended due to package-private methods.
Otherwise it would be possible to return a `Map`-implementation that only requires a
`FastMap` and the index in that `FastMap`. The current approach to this is to implement a
second version of `getValues` which returns such a custom map, and to replace calls to the
old one with the new implementation where possible.

Saved memory: Around 170 MB  
CPU impact: unclear (see second paragraph)  
Side: both  
Mixin subpackage: `nopropertymap`  

### 4. Multipart model predicate caching
Each multipart model stores a number of predicates to determine which parts to show under
what conditions. These predicates take up 300-400 MB. However in many cases these
predicates are checking the same thing, they are just newly created every time. For
`PropertyValueCondition` the predicates can be cached by using the property and its value
as a key, for `And/OrCondition` (and multi-value `PropertyValueCondition`s) the key is the
list of input predicates sorted by hash value.  
One detail that makes this even more effective is that a block can never have two
properties that are equal according to `equals`, while the common property implementations
include `equals`. Additionally `StateHolder#get` also considers `equals` (as opposed to
reference equality), so using the same lambda for equivalent (but non reference-equivalent) properties and values is
actually possible. This is particularly useful as one of the most common usages of multipart models is pipes, where the
states are nearly always boolean properties named `north` etc. As a result the number of predicates is reduced from
between 10s of thousands and millions to a few ten or hundred instances.

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
