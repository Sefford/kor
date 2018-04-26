Kor Repositories
===

Kor provides a persistence abstraction via a _Repository_ pattern. This is nothing more than a CRUD interface, but can be tailored to your needs

We provide several basic flavors of persisting as `MemoryDataSource`, to simply save elements in memory in a Map, `JsonDiskDataSource`
and `MemoryJsonDataSource` to persist Json directly to disk or memory. You will require to provide your own easy implementation
of a `JsonConverter`.

The Repositories are also built on a _Chain of Responsibility_ command. Can work standalone or chained together to provide
multitier persistence systems on both memory and disk implementations by using `TwoTierRepository`. 

Kor-Android module also provides repository decorators like `LRURepository` to control the population of objects in the persistence. This is done
by element counting, not a memory counting. `ExpirationRepository` on the other hand will allow you to implement keep-alive policies
in the cache layer.  

For a model element to be usable with repositories, it requires to implement both `RepoElement` interface to provide
an unique ID to save and get from the Repository.

If you want to build new and amazing repos, you can start by extending from `StubDataSource` where most of the
helper methods are already implemented.