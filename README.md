Kor
===

A Clean Architecture Implementation core library.

Download
--------

### Maven
#### Kor-Common
```XML
<dependency>
    <groupId>com.sefford</groupId>
    <artifactId>kor-common</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Gradle
#### Kor-Common
```groovy
compile 'com.sefford:kor-common:4.0.0'
```


What is Kor?
============

Kor is the skeleton of a clean-ish architecture implementation. There are many ways of implementing a clean architecture:

* Ports and adapters
* Hexagonal Architecture
* VIPER Architecture

The aim of Kor is to provide a fast implementation of a proper architecture where the developer can iterate quickly and
and with the ability to use good practices as SOLID and TDD from day 0.

Kor is not meant to be a canonical implementation of such architecture, but a personal interpretation of how it should be.

How is Kor structured?
======================

Basic concepts
--------------

Kor relies on the classic concept of "Use Cases". Each "Use Case" comprises a full request to the model. This request
can be to Cache, Network or other types of aynchronous running operations. By this, the UI layer is completely decoupled
of the rest of the Model logic.

A key feature of Kor is that any element of the model is fetched from an `Interactor` and by using the `Repositories`, the
Model elements are single-instanced (the same object is returned always for the same unique ID) and updating the model
is done automatically thanks to Java pointer to object features.

### Use cases

The `Use case` is the single element of logic of a functional requirement of an information system.

Kor allows you to execute up to three phases which is typical of any implementation of such requirements.

* **Execution:** In this stage the code belonging to fetch the information from the cache, API or webservice.
* **Post process:** _(Optional)_ This stage allows the user to manipulate the data, by filtering, sorting or transforming the data obtained in the execution step.
* **Persistance:** _(Optional)_ This stage is provided to encapsulate all the necessary logic to persist the information, typically to memory or disk.
* **Composing the error response:** This stage will only happen in exceptional situations which will lead to a failed execution. The
idea is to analyze the exception and output an ErrorResponse which the UI layer can use to give the user detailed information.

Ideally your logic should be able to mix and match parts of your logic; so most of the elements are reusable.

`StandaloneUseCase` allows you to encapsulate this logic on an unit which will reusable. It provides also several utilities
to choose to execute your use case syncronously or asyncronously, choosing the correct thread via Kotlin Coroutines.

### Notification System

Kor does not enforce any particular system of notification. It can be used via callbacks that implement the `Postable`
interface. We have widely tested it with a Event Bus system and RxJava backbone. Still you can use it directly with no
penalty.

### Repositories

Kor provides a persistence abstraction via a _Repository_ pattern. This is nothing more than a CRUD interface.

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

If you want to build new and amazing repos, you can help yourself by extending from `StubDataSource` where most of the
helper methods are already implemented.

Migrating to 4.0.0
======================

Many breaking changes were done in 4.0.0, mostly related to how the core works.

- All old `Interactor` system got deprecated. All delegates must be chopped and separated on each phase.
- Now `Use cases` can return `Either<Error,Response>`. If you use the `Postable`-compatible method, your Postable
will keep receiving these separated response.
- Save and Get `Repository` methods will receive an `Either<RepositoryError, V>` response instead of a `null`; so the
receiving class can have a little information of what went wrong.
- In order to ease the long signatures and known problems with the error log grouping, you will need to log manually each
of the errors.

Interesting literature
======================

Martin, Robert Cecil: [The Clean Architecture](http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html)

Cockburn, Alistair: [Hexagonal Architecture](http://alistair.cockburn.us/Hexagonal+architecture)

[VIPER Architecture](http://mutualmobile.github.io/blog/2013/12/04/viper-introduction/)

License
-------
    Copyright 2014 Sefford.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



