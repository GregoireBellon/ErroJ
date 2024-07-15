<h1 align="center">ErroJ</h1>

ErroJ is a Java 21 library which attempts to take the best from Vavr's error handling, while leveraging the latest Java 21 features like pattern matching, records and sealed classes. It aims to provide a native feeling, by using the Optional's terminology.

The library is standalone, thoroughly tested and works with GraalVM.

Any feedback on the features, API or abstractions is genuinely appreciated, don't hesitate to create an issue, or even a pull request !

Documentation will progress as the API is validated and becomes stable.

## Current features

The library provides `Result<Value, Throwable>` implemented by `Ok<Value, Throwable>` and `Err<Value, Throwable>`.

It enables pattern matching :

```java
Result<Integer, ArithmeticException> res = new Ok<>(10);

switch(res){
    case Ok(var value) -> System.out.println(value);
    case Err(var exception) -> System.out.println("ouch");
}
// 10
```

It provides all the niceties you would expect from a Monadic container :

```java
Result<Integer, ArithmeticException> res = new Ok<>(10);
res.isOk() // true
res.ok() // Optional.of(10)
res.map(Integer::toString) // Ok<String, ArithmeticException>("10")
res.mapErr(e-> new CustomException(e.getMessage())) // Ok<Integer, CustomException>(10)
```

The type VariableThrowable is usefull when you chain multiples operations :

```java
Result<Integer, ArithmeticException> res = dangerousIntOperation();

res.flatMap(r -> Result.of(() -> stringOrNullPointerException(r))) // Result<String, VariableThrowable>
```

As you saw it, you can wrap dangerous operations into a `Result.of(CheckedProvider)`, which wrap any thrown exception into a VariableThrowable.

You can also chain dangerous operations, with `andThen(CheckedFunction)` or `andThen(CheckedConsumer)`

```java
Result<Integer, ArithmeticException> res = dangerousIntOperation();

res.andThen(r -> stringOrNullPointerException(r)) // Result<String, VariableThrowable>
```

Cleaner !

You can then "rescue" your results, with :

```java
Result<String, VariableThrowable> res = lastExample();

res.rescue(
    Rescue.of(ArithmeticException.class, "Can't do maths"),
    Rescue.of(NullPointerException.class, () -> provideJustificationForThisFailure())
).orElse("I don't know how this happened") // String
```

You can also use `VariableThrowable::getWrapped` and pattern match on it, like :

```java
Result<String, VariableThrowable> res = lastExample();

res.recover((VariableThrowable vt) -> switch (vt.getWrapped()) {
            case ArithmeticException ae -> "Can't do maths";
            case NullPointerException npe -> provideJustificationForThisFailure();
            default -> "I don't know how this happened";
        }); // String
```

## Disclaimer

This is mostly a toy project to better understand errors as values. The Java API is slowly shifting toward data oriented programming via Project Amber to support functional patterns. As the new `Future` type hints it, Java is already trying to make error handling as value a viable approach. So it's _VERY_ likely that a native replacement will emerge eventually.

In the meantime, let's have fun !
