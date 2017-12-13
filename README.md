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
    <version>2.8.0</version>
</dependency>
```

### Gradle
#### Kor-Common
```groovy
compile 'com.sefford:kor-common:2.8.0'
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

### Executors

The communication is done via executing those Interactors which are Command patterns into a Executor element. This is an
execution queue abstracted, whose responsibility is to take the business decision on when to actually execute the command.

The `Executor` interface provides ways to execute single or multiple requests. Again it is up to the concrete implementation
of the `Executor` to encapsulate such decisions.

### Delegates

The `Delegate` is characterized for definining two types of responses in the default implementation,
a `Response` and an `Error`. Those are nothing more than messages that define the success or a failure of such `Delegate`.

A failure can happen by many reasons, being unexpectedly from an Exception of the program or by a controlled situation
during the flow of the `Delegate` itself. However, a response gives the chance to provide a successful, but not satisfiable
termination by means of `isSucess()` method.

An external element of the architecture will be listening somehow for those responses, it is part of the contract. However
it is not strongly bound, and depending on the requirements a Request sent by a component can be listened by a completely
unrelated one.

A typical Request should (but it is not enforced to) implement all these stages. In the case of a `NetworkDelegate`:

* **execute:** In this stage the code belonging to fetch the information from the server, API or webservice.
A successful completion of this stage should already generate a Response-type result with the results converted into POJO
via JSon deserialization or any other means necessary.
* **postProcess:** This stage is meant to analyze the data and give the chance to set the success flag to false or other
crossing from the data.
* **saveToCache:** This stage is provided to encapsulate all the necessary logic to persist the information to memory or disk.
* **composeErrorResponse:** This stage will only happen in exceptional situations which will lead to a failed execution. The
idea is to analyze the exception and output an ErrorResponse which the UI layer can use to give the user detailed information.

For heavy-duty operations, `FastDelegate` interface is available to provide a chance to make a preliminary persistence to
memory and notifiy the user to give out the response while offloading to a slower lru in the background of the `Request`.

The case of the `CacheDelegate` the behavior is slightly different. A `CacheDelegate`is supposed to fetch the information
from the persistence system. As such, it lacks `postProcess` and `saveToCache` stages. In the earlier step, because the
information is supposed to have been saved on the system via an early `NetworkDelegate`, and as such, having passed through the
necessary steps. In the latter, for obvious reasons.

* **isCacheValid:** This stage is meant to be executed _before_ the actual retrieveFromCache, and it is meant to peek at
the lru data to know in advance if the information is valid (e.g. has not expired or it is present) before trying to
retrieve the data
* **execute:** As stated this is the stage for actually getting the information from the lru.

Finally, the `UpdateableDelegate` adds a **keepLooping()** method to check the execution of a `NetworkDelegate` flow.
Remember that while a `UpdateableDelegate` can keep periodically running, this is battery and network-costly and
many of these kind of delegates might end up starving the `Executor`.

### Interactors

An `Interactor` backbone responsibility is to notify when the `Delegate` has completed sucessfully or not. An implementation
of an `Interactor` will delegate to a `Delegate` _(d'oh)_ to do all the work, and will simply execute the request steps in a certain order.

Each of the `Interactors` work typically with a specified kind of `Delegate`.

* **CacheInteractor:** Retrieves the information form the persistence system. If the content is no longer valid it
does not notify of anything to reduce the noise during the execution.
* **StandardNetworkInteractor:** Retrieves the information from network, postprocesses it, saves it into the persistence and
notifies it.
* **FastNetworkInteractor:** Retrieves the information from the network, postprocesses it, saves into a fast lru (typically
memory), notifies to the result and then resumes the saving to the persistence.
* **UpdateableInteractor:** Performs a loop over the `StandardNetworkInteractor` until the condition is fullfilled. On each
of the loops notifies the result to the system.
* **StandaloneInteractor:** Builds and executes any kind of the previous types of interactors ad-hoc.

### Notification System

Kor does not enforce any particular system of notification. It can be used via callbacks that implement the `Postable`
interface. However, **we encourage to use an Event Bus system** (Otto or Greenrobot's) or **RxJava** in order to provide a real
decoupled architecture.

### Repositories

Kor provides a persistence abstraction via a _Repository_ pattern. This is nothing more than a CRUD interface.

The basic implementations available are nothing more than a Key, Value implementation on a memory Map for both normal and `FastSaving`
requests.

The Repositories are also built on a _Chain of Responsibility_ command. Can work standalone or chained together to provide
multitier persistence systems on both memory and disk implementations.

Kor-Android module also provides a LRUMemory implementation.

For a model element to be usable with repositories, it requires to implement both `RepoElement` interface to provide
an unique ID to save and get from the Repository and a Updateable implementation to update information inside the Model
element itself.

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



