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



# Basic Setup

Make sure to have the latest version of JDK 1.8 installed.

Add the dependencies into the project's `build.gradle`

```groovy
dependencies {
    compile 'com.sefford:kor-usecases:4.0.0'
    compile 'com.sefford:kor-repositories:4.0.0'
    compile 'com.sefford:kor-repositories-extensions:4.0.0' // optional 
    compile 'com.sefford:kor-repositories-gson-converter:4.0.0' 
    compile 'com.sefford:kor-repositories-moshi-converter:4.0.0'
}
```
How is Kor structured?
======================
- [Use Cases](http://arrow-kt.io): Abstractions to apply use cases and interactors concepts to your architecture
- [Repositories](http://arrow-kt.io/docs/patterns/glossary/): Repository pattern to abstract cache and persistence 
- [Repositories Extensions](http://arrow-kt.io/docs/typeclasses/intro/): Basic implementations of policies for specialized repositories _(optional_)
- [Repositories Gson Converter](http://arrow-kt.io/docs/datatypes/intro/): Simple Gson Json converter for json-based repositories _(optional)_ 
- [Repositories Moshi Converter](http://arrow-kt.io/docs/effects/io/): Simple Gson Moshi converter for json-based repositories _(optional)_ 

Migrating to 4.0.0
======================

Many breaking changes were done in 4.0.0, mostly related to how the core works.

- Now all Kor is separated in individual modules
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



