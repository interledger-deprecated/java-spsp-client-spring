# Interledger SPSP REST client (Java) [![CircleCI](https://circleci.com/gh/interledger/java-spsp-client-spring.svg?style=svg)](https://circleci.com/gh/interledger/java-spsp-client-spring)

Interledger Simple Payment Setup Protocol REST client.

A Java client implementing the SPSP api [simple-payment-setup-protoco](https://github.com/interledger/rfcs/blob/master/0009-simple-payment-setup-protocol/0009-simple-payment-setup-protocol.md).

## Develop

### Dependencies

The project is setup to find project dependencies in the _same directory_ so the easiest way to work on the code is to fetch the dependencies as-is from GitHub.

```bash

    $ git checkout https://github.com/interledger/java-crypto-conditions.git
    $ git checkout https://github.com/interledger/java-ilp-core.git

```

### Gradle/Maven

The project supports both Gradle and Maven build tools. A special Gradle task is defined which will generate a POM file for Maven.

```bash

    $ gradle writePom

```

### CheckStyles

The project uses Checkstyle for consitency in code style. We use the Google defined Java rules which can be configured for common IDE's by downloading configuration files from the [GitHub repo](https://github.com/google/styleguide).

## Use

The library adheres to the interfaces in the [Interledger Protocol Core](https://github.com/interledger/java-ilp-core) library. The client is instantiated with a RestTemplate for autowiring, or can be constructed directly.

```java

  SpspService service = new SpringSpspClientService(restTemplate);
  Receiver response = service.query(URI.create("http://red.ilpdemo.org/api/receivers/alice"));

```

## TODO

    [X] Fix Checkstyle issues
    [ ] Update according to changes in dependencies

## Contributors

Any contribution is very much appreciated!

## License

This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.
