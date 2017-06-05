Changelog
=========
## Kor common 2.5.3
_2017-06-05_

* Now you do not need to extend `TwoTierRepository` and `MemoryRepository` to use it directly
* `BaseRepository` renamed to `TwoTierRepository`
* Minor improvements


* Added DiskJsonRepository to allow to-disk JSon peristence 
## Kor common 2.5.1
_2017-06-05_

* Added DiskJsonRepository to allow to-disk JSon peristence 

## Kor common 2.5.0
_2017-06-05_

* Refactored Repository structures to apply better OOP methodologies
* Added `LruRepository` to allow Repositories to hold a limited number of elements
* Added `ExpirationRepository` to allow Repositories to have an Expiration Policy
* Added `TimeExpirationPolicy` as a PoC expiration policy

## Kor common 2.5.0
_2017-06-05_

* Refactored Repository structures to apply better OOP methodologies
* Added `LruRepository` to allow Repositories to hold a limited number of elements
* Added `ExpirationRepository` to allow Repositories to have an Expiration Policy
* Added `TimeExpirationPolicy` as a PoC expiration policy

## Kor Common 2.4.3
_2017-04-21_

* Minor fixes on certain circumstances with `BaseRepository`

## Kor Common 2.4.1
_2017-02-27_

* Corrected a small error with the signature of `logErrorResponse(Loggable)` method

## Kor Common 2.4.0
_2017-02-27_

* Added new `OptimisticNetworkInteractor`for providing fast UI updates then doing the hard work
* Correction of `BaseRepository` where `get(K)` was relying on `Repository.get(K)` instead of using `Repository.contains(K)`, prompting for less efficient lookups
* Added a new `logErrorResponse(Loggable)`method that allows better error reporting on Fabric 

## Kor Common 2.1
_2015-05-16_

* Additional classes renaming. Now `ResponseInterface` and `Error Interface` becomes `Response` and `Error`, respectively.
* `retrieveNetworkResponse` and `retrieveCacheResponse` unifies on `execute` method.
* Now all delegates extend from `Delegate` class. This class has the basic functionality of executing, composing an error message
and returning its name.
* Due to the previous change, now CacheDelegate can produce error responses. However the functionality of not returning a
response if the `Response.isSuccess()` remains.
* Added a `StandaloneInteractor` to reduce constructor signature on dependency injection by avoiding to provide a 
`Provider`, something alike an Interactor factory and a Builder.
 
## Kor Common 2.0
_2014-11-7_

 * Renamed classes to provide better understanding of what means each of the components on a clean architecture. Now `Requests` are renamed
 to `Delegates`, `Strategies` are renamed to `Interactors` and `Provider` `Executors`. From now on we will be using the new names instead of the old ones.
 * Removed dependencies towards `JobQueue`. Now `Interactors` are pure `Runnables` that can be executed via a `ThreadPoolExecutor` _(implementation
 not provided)_
 * Included a new `UpdateableInteractor` and `UpdateableDelegate` to perform looped request to the network _(e.g. Bluetooth-like Discovery Service)_

## Kor Retrofit 1.1
2014-11-7

 * Moved all the Strategies towards Kor-Commons.
 * Now this is a pure Java implementation.

## Kor Common 1.31
_2014-10-24_

 * `retrieveNetworkResponse` now throws an exception in order to properly catch erroneous responses
 through `composeErrorResponse(Exception`.

##Kor Retrofit 1.0.9
_2014-10-24_

 * Updated dependencency to kor-common 1.31

##Kor Android 1.0.1
_2014-10-24_

 * Updated dependencency to kor-common 1.31

##Kor Retrofit 1.0.8
_2014-08-21_

 * Minor bump that supports Kor-Common 1.2
 
## Kor Common 1.2
_2014-08-21_

 * New structure for `BaseRepository`, now delegates the calls to both internal `currentLevel` and
 `nextLevel` repositories, making the BaseRepository much more flexible.
 * Now `BaseRepository` has better implementations of `getAll()` and `getAll(ids)` methods. 
 * Now Kor-Common provide a `BaseFastRepository` which extends `BaseRepository` and implements 
 `FastRepository` interface.
 * Memory Map repository extracted to a individual Repository module that supposrts `FastRepository`
 interface.
 * Added `LruMemoryRepository` that supports recycling memory repositories via LRUCAche from
 android support library.
 
## Kor Retrofit 1.0.5
_2014-07-25_

 * Fixed bug that was preventing `composeErrorResponse(RetrofitError)` to be properly dispatched.