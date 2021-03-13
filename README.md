**Warning**: This branch is a demonstration of a) how much additional memory can be saved by combining FerriteCore and
Hydrogen and b) how much additional complexity it adds to the Mixins. It is not intended for general use.  
The answer to the first question is "some, but not a lot". Using All of Fabric 3 version 2.5.9 with just FerriteCore
reduces memory usage from 1,792 MB to 984 MB. Adding Hydrogen to the mix reduces this to 958 MB (26 MB saved). Test
setup: Generating a standard world on peaceful with a fixed seed and taking a heapdump after 2 minutes in world
(according to the on-screen timer).  
The answer to the second question is "quite a bit". One straight `Overwrite`s needs to be replaced by injections with
captured locals that need to be able to target both the vanilla code and Hydrogens `Overwrite`. One `Overwrite` needs to
be "enhanced" with a `RETURN`-injection that serves no purpose other than to stop Hydrogens `RETURN`-injection from
running. Converting the `Overwrite` into a `RETURN`-injection injected after Hydrogen's code works in principle, but
causes Hydrogen to keep 50 MB of useless cached data.  
As a conclusion I do not consider it worth the effort to merge this into the main branch.

A coremod to save a few of these:  
<img src="https://upload.wikimedia.org/wikipedia/commons/d/da/KL_CoreMemory.jpg" width="400"/>

or rather their modern equivalent: RAM.

(Image: Konstantin Lanzet, CC BY-SA 3.0 <http://creativecommons.org/licenses/by-sa/3.0/>, via Wikimedia Commons)

I am working on getting at least some of the changes made by this mod into Forge (
see [this issue](https://github.com/MinecraftForge/MinecraftForge/issues/7559) for progress), features will be removed
from the Forge version of FerriteCore if and when they are added to Forge. They will still be present on Fabric. For a
high-level description of the improvements implemented by this mod see [here](summary.md).

### Mappings

This mod currently uses MCP names for everything, in case someone involved in MCP mapping wants to look at it. There are
a few commits using official names, so be careful when looking at the commit history if you are concerned about "mapping
taint".
