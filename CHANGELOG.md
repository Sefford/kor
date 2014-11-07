Changelog
=========

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