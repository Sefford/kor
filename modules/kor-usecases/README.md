Kor Usecases
===
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

Kor does not enforce any particular system of notification. 

It can be used via callbacks that implement the `Postable`
interface. 

We have widely tested it with a Event Bus system and RxJava backbone. 

Still you can use it directly with no penalty, through Kotlin Coroutines